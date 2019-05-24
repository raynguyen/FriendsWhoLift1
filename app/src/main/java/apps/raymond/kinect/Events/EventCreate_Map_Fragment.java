package apps.raymond.kinect.Events;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import apps.raymond.kinect.R;

public class EventCreate_Map_Fragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {
    private static final int LOCATION_REQUEST_CODE = 0;

    public static EventCreate_Map_Fragment newInstance(String userID, ArrayList<Event_Model> myEvents){
        EventCreate_Map_Fragment fragment = new EventCreate_Map_Fragment();
        Bundle args = new Bundle();
        args.putString("userid",userID);
        args.putParcelableArrayList("myevents",myEvents);
        fragment.setArguments(args);
        return fragment;
    }

    private FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<Event_Model> mEventList;
    private String mUserID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventList = getArguments().getParcelableArrayList("myevents");
        mUserID = getArguments().getString("userid");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_create_map_fragment,container,false);
    }

    private MapView mMapView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.mapview_event_create);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setPadding(0,400,0,0);
        try{
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e){ }

        mMap.setOnMarkerClickListener(this);
        getDeviceLocation();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    Location mLastLocation;
    private void getDeviceLocation(){
        try{
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful() && task.getResult()!=null){
                                mLastLocation = task.getResult();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastLocation.getLatitude(),
                                                mLastLocation.getLongitude()),17.0f));
                            }
                        }
                    });
        } catch (SecurityException e){}
    }
}
