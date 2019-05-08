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

import java.util.List;

import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventSearch_Fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "EventsSearchFragment";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String USER = "User";
    private static final float DEFAULT_ZOOM = 17.0f; //This is already defined in Maps_Activity.

    public static EventSearch_Fragment newInstance(User_Model userModel){
        EventSearch_Fragment fragment = new EventSearch_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(USER,userModel);
        fragment.setArguments(args);
        return fragment;
    }


    FusedLocationProviderClient mFusedLocationClient;
    Repository_ViewModel mViewModel;
    User_Model mUser;
    List<Event_Model> eventList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mViewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);

        try{
            mUser = getArguments().getParcelable(USER);
        } catch (NullPointerException npe) {
            Log.w(TAG, "Error.", npe);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_events_fragment,container,false);
        return view;
    }

    private MapView mMapView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.events_list_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
        getDeviceLocation();
        getNearbyEventList();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Event_Model markerEvent = (Event_Model) marker.getTag();

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

    //Consolidate getDeviceLocations to a single class?
    Location mLastLocation;
    private void getDeviceLocation(){
        try{
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful()){
                                if(task.getResult()!=null){
                                    Log.w(TAG,"MOVING TO LAST KNOWN LOCATION.");
                                    mLastLocation = task.getResult();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(mLastLocation.getLatitude(),
                                                    mLastLocation.getLongitude()),
                                            DEFAULT_ZOOM
                                    ));
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
        Observer<List<Event_Model>> mObserver = new Observer<List<Event_Model>>() {
            @Override
            public void onChanged(@Nullable List<Event_Model> event_models) {
                for(Event_Model event:event_models){
                    double lat = event.getLat();
                    double lng = event.getLng();
                    LatLng latLng = new LatLng(lat,lng);
                    if(mMap !=null){
                        Marker marker =  mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(event.getName()));
                        marker.setTag(event);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
                }
            }
        };
        mViewModel.getPublicEvents().observe(this, mObserver);
    }

}
