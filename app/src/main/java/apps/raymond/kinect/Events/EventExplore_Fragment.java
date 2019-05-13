package apps.raymond.kinect.Events;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.R;
import apps.raymond.kinect.Core_ViewModel;
import apps.raymond.kinect.UserProfile.User_Model;

/*
 * ToDo: ********************************
 * If an event is accepted via the map, we have to check that the user does not have an invite pending
 * in their inbox. If the event invite exists, we must remove it and update the InvitedUsers collection
 * for the respective event as well.
 *
 * ToDo:
 * Consider consolidating this Activity with the EventExplore_Fragment. Set the architecture such that
 * the Maps_Activity loads the map and requests permissions as required, but also overlay one of two
 * fragments depending on the startActivity intent.
 *
 * Fragment1: Search nearby events Fragment that loads data and passes back to the activity to populate
 * the event locations with markers.
 *
 * Fragment2: Search functionality to call the Geolocate function and return search results.
 *
 * ToDo:
 * Filter out events that the user is already in attendance or invited to.
 */
public class EventExplore_Fragment extends EventControl_Fragment implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "EventsSearchFragment";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String USER = "User";
    private static final int LOCATION_REQUEST_CODE = 1;

    private EventControlInterface mEventManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mEventManager = (EventControlInterface) context;
        } catch (ClassCastException cce){}
    }

    public static EventExplore_Fragment newInstance(User_Model userModel){
        EventExplore_Fragment fragment = new EventExplore_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(USER,userModel);
        fragment.setArguments(args);
        return fragment;
    }

    FusedLocationProviderClient mFusedLocationClient;
    Core_ViewModel mViewModel;
    User_Model mUser;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);

        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }

        try{
            mUser = getArguments().getParcelable(USER);
        } catch (NullPointerException npe) {
            Log.w(TAG, "Error.", npe);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_events,container,false);
    }

    private ViewGroup detailsCardView;
    private TextView textEventName, textDesc, textThoroughfare, textMonth, textDate, textTime;
    Button btnAttend;
    private MapView mMapView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.events_list_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        detailsCardView = view.findViewById(R.id.cardview_event_search);
        textEventName = view.findViewById(R.id.text_name);
        textDesc = view.findViewById(R.id.text_description);
        textThoroughfare = view.findViewById(R.id.text_thoroughfare);
        textMonth = view.findViewById(R.id.text_month);
        textDate = view.findViewById(R.id.text_date);
        textTime = view.findViewById(R.id.text_time);

        btnAttend = view.findViewById(R.id.button_attend);
        btnAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(focusedEvent!=null){
                    mEventManager.onAttendEvent(focusedEvent, 0);
                    focusedMarker.remove();
                    detailsCardView.setVisibility(View.GONE);
                }
            }
        });
    }

    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(detailsCardView.getVisibility()==View.VISIBLE){
                    detailsCardView.setVisibility(View.GONE);
                }
            }
        });
        googleMap.setPadding(0,400,0,0);
        try{
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e){
            Log.w(TAG,"SecurityException.",e);
        }
        mMap.setOnMarkerClickListener(this);
        getDeviceLocation();
        getNearbyEventList();
    }

    Event_Model focusedEvent;
    Marker focusedMarker;
    @Override
    public boolean onMarkerClick(Marker marker) {
        //ToDo: Zoom in to the event!

        detailsCardView.setVisibility(View.VISIBLE);
        focusedEvent = (Event_Model) marker.getTag();
        focusedMarker = marker;

        textEventName.setText(focusedEvent.getName());
        textDesc.setText(focusedEvent.getDesc());
        textThoroughfare.setText(focusedEvent.getAddress());
        textThoroughfare.setText("Papa Johns"); //ToDo: Should just be the address.

        Calendar c = Calendar.getInstance();
        Date date = new Date(focusedEvent.getLong1());
        c.setTimeInMillis(focusedEvent.getLong1());
        textMonth.setText(new DateFormatSymbols().getMonths()[c.get(Calendar.MONTH)]);
        textDate.setText(String.valueOf(c.get(Calendar.DATE)));
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a",Locale.getDefault());
        textTime.setText(sdf.format(date));

        marker.getPosition();
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    //Consolidate getDeviceLocations to a single class?
    Location mLastLocation;
    private void getDeviceLocation(){
        Log.w(TAG,"In getDeviceLocation.");
        try{
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful()){
                                if(task.getResult()!=null){
                                    Log.w(TAG,"MOVING TO LAST KNOWN LOCATION.");
                                    mLastLocation = task.getResult();
                                    //ToDo: Uncomment this when done testing.
                                    /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(mLastLocation.getLatitude(),
                                                    mLastLocation.getLongitude()),
                                            DEFAULT_ZOOM
                                    ));*/
                                }
                            }
                            //ToDo: Set a default location and zoom when unable to retrieve current location.
                        }
                    });
        } catch (SecurityException e){
            Log.w(TAG,"There was an error retrieving the device location.",e);
        }
    }

    /**
     * ToDo: We are currently retrieving the full list of public events. This has to refined to only
     * nearby events to limit the reads from Firestore and to reduce load time.
     */
    private void getNearbyEventList(){
        mViewModel.getPublicEvents().observe(this, new Observer<List<Event_Model>>() {
            @Override
            public void onChanged(@Nullable List<Event_Model> event_models) {
                Log.w(TAG,"I don't think it changes!~");
                if(event_models!=null && !event_models.isEmpty()){
                    for(Event_Model event:event_models){
                        double lat = event.getLat();
                        double lng = event.getLng();
                        LatLng latLng = new LatLng(lat,lng);
                        if(mMap !=null){
                            //Todo: Custom marker to show the primes on top of the date.
                            Marker marker =  mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(event.getName()));
                            marker.setTag(event);
                        }
                        //ToDo:Remove this line once this fragment is complete.
                        Log.w(TAG,"Does it move?");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
