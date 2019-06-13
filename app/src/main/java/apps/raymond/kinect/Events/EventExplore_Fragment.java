/*
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
package apps.raymond.kinect.Events;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Core_ViewModel;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventExplore_Fragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "EventsSearchFragment";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String USER = "User";
    private static final int LOCATION_REQUEST_CODE = 1;

    public static EventExplore_Fragment newInstance(User_Model userModel){
        EventExplore_Fragment fragment = new EventExplore_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(USER,userModel);
        fragment.setArguments(args);
        return fragment;
    }

    FusedLocationProviderClient mFusedLocationClient;
    Core_ViewModel mViewModel;
    private User_Model mUserModel;
    private String mUserID;
    private boolean mLocationPermission = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        } else {
            mLocationPermission = true;
        }

        try{
            mUserModel = getArguments().getParcelable(USER);
            mUserID = mUserModel.getEmail();
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

        Button btnAttend = view.findViewById(R.id.button_attend);
        btnAttend.setOnClickListener((View v)->{
            if(focusedEvent!=null){
                List<Event_Model> inviteList = mViewModel.getEventInvitations().getValue();
                //Delete the event invitation from DB and ViewModel set if it exists.
                if(inviteList.contains(focusedEvent)){
                    inviteList.remove(focusedEvent);
                    mViewModel.setEventInvitations(inviteList); //Remove the invitation from the ViewModel set and increment attending count.
                    mViewModel.deleteEventInvitation(mUserID,focusedEvent.getName()); //Delete the invitation doc and decrement invited count.
                }
                mViewModel.addUserToEvent(mUserID,mUserModel,focusedEvent.getName());//Add user to event's Accepted collection and increment attending.
                mViewModel.addEventToUser(mUserID,focusedEvent);//Add the event to User's Event collection.
                List<Event_Model> acceptedEvents = mViewModel.getAcceptedEvents().getValue();
                acceptedEvents.add(focusedEvent);
                mViewModel.setAcceptedEvents(acceptedEvents);
            }
        });
    }

    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setPadding(0,0,0,0);
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener((LatLng latlng)->{
            if(detailsCardView.getVisibility()==View.VISIBLE){
                detailsCardView.setVisibility(View.GONE);
            }
        });

        mViewModel.getPublicEvents().observe(this, (List<Event_Model> event_models)->{
            if(event_models!=null){
                List<Event_Model> eventList = mViewModel.getAcceptedEvents().getValue();
                for(Event_Model event : event_models){
                    if(!eventList.contains(event)){
                        LatLng latLng = new LatLng(event.getLat(),event.getLng());
                        //Todo: Custom marker to show the primes on top of the date.
                        Marker marker=  mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(event.getName()));
                        marker.setTag(event);
                    }
                }
            }
        });
        mViewModel.loadPublicEvents();

        if(mLocationPermission){
            getDeviceLocation();
            try{
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e){
                Log.w(TAG,"SecurityException.",e); }
        }
    }

    Event_Model focusedEvent;
    Marker focusedMarker;
    @Override
    public boolean onMarkerClick(Marker marker) {
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

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),17.0f));
        return true;
    }

    private void getDeviceLocation(){
        try{
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), (Location location) -> {
                        if(location!=null){
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(location.getLatitude(),
                                            location.getLongitude()),17.0f));
                        }
                    });
        } catch (SecurityException e){
            Log.w(TAG,"There was an error retrieving the device location.",e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==LOCATION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.w(TAG,"we were granted permission for location!");
                mLocationPermission = true;
                getDeviceLocation();
                try{
                mMap.setMyLocationEnabled(true);
                }
                catch (SecurityException se){
                    Log.w(TAG,"NO permission for location!");
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
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
    public void onDestroy(){
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
