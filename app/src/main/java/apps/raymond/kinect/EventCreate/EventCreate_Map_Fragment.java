package apps.raymond.kinect.EventCreate;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Location_Model;
import apps.raymond.kinect.Locations_Adapter;
import apps.raymond.kinect.R;

public class EventCreate_Map_Fragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, Locations_Adapter.LocationClickInterface {
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
    private EventCreate_ViewModel mViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventList = getArguments().getParcelableArrayList("myevents");
        mUserID = getArguments().getString("userid");
        mViewModel = ViewModelProviders.of(requireActivity()).get(EventCreate_ViewModel.class);
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_create_map,container,false);
    }

    private MapView mapView;
    private TextView txtNullData;
    private ProgressBar progressBar;
    private FloatingActionButton btnMaxMap;
    private Locations_Adapter mAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapview_event_create);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        txtNullData = view.findViewById(R.id.text_null_locations);
        progressBar = view.findViewById(R.id.progress_bar_locations);
        btnMaxMap = view.findViewById(R.id.fab_maximize_map);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_locations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Locations_Adapter(this);
        recyclerView.setAdapter(mAdapter);
    }

    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.w("CreateEventMap","The height of the mapview is = "+mapView.getHeight());
        View btnLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) btnLocation.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_END,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_START,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 40);
        try{
            googleMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } catch (SecurityException e){ }

        mViewModel.getLocationSet().observe(this, new Observer<List<Location_Model>>() {
            @Override
            public void onChanged(@Nullable List<Location_Model> location_models) {
                progressBar.setVisibility(View.GONE);
                if(location_models.size()==0){
                    txtNullData.setVisibility(View.VISIBLE);
                }
                mAdapter.setData(location_models);

                for(Location_Model location : location_models){
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLat(),location.getLng()))
                            .title(location.getLookup())
                    );
                }

            }
        });
        mViewModel.loadUserLocations(mUserID);

        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onLocationClick(Location_Model location) {
        //Move the map to the location
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

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
