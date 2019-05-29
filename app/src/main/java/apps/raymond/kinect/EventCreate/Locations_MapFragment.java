package apps.raymond.kinect.EventCreate;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Location_Model;
import apps.raymond.kinect.Locations_Adapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;

public class Locations_MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, Locations_Adapter.LocationClickInterface {
    private static final int LOCATION_REQUEST_CODE = 0;

    /*
     * Need an identifier to determine if we need to load the card to add as a user location or if
     * we want to set the mAddress for an event.
     */
    public static Locations_MapFragment newInstance(String userID){
        Locations_MapFragment fragment = new Locations_MapFragment();
        Bundle args = new Bundle();
        args.putString("userid",userID);
        fragment.setArguments(args);
        return fragment;
    }

    private FusedLocationProviderClient mFusedLocationClient;
    private String mUserID;
    private EventCreate_ViewModel mViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(EventCreate_ViewModel.class);
        mUserID = getArguments().getString("userid");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_locations,container,false);
    }

    private MapView mapView;
    private EditText editSearchMap;
    private ImageButton btnShowRecycler;
    private TextView txtNullData;
    private ProgressBar progressBar;
    private Locations_Adapter mAdapter;
    private Location mLastLocation;
    private ViewGroup mMapGroup, mRecyclerGroup;
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapview_event_create);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mMapGroup = view.findViewById(R.id.frame_mapview);
        mRecyclerGroup = view.findViewById(R.id.relative_locations_recycler);
        btnShowRecycler = view.findViewById(R.id.button_view_locations);
        editSearchMap = view.findViewById(R.id.edit_search_location);
        editSearchMap.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate(v.getText().toString());
                }
                return false;
            }
        });

        final LinearLayout.LayoutParams showParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,0,1.0f);
        final LinearLayout.LayoutParams hideParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,0,0f);

        editSearchMap.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mRecyclerGroup.setLayoutParams(hideParams);
                }
            }
        });


        btnShowRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((LinearLayout.LayoutParams) mRecyclerGroup.getLayoutParams()).weight==0){
                    mRecyclerGroup.setLayoutParams(showParams);
                    btnShowRecycler.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.baseline_keyboard_arrow_down_black_18dp,null));
                } else {
                    mRecyclerGroup.setLayoutParams(hideParams);
                    btnShowRecycler.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.baseline_keyboard_arrow_up_black_18dp,null));
                }
            }
        });

        txtNullData = view.findViewById(R.id.text_null_locations);
        progressBar = view.findViewById(R.id.progress_bar_locations);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_locations);
        SearchView mSearchView = view.findViewById(R.id.searchview_locations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Locations_Adapter(this);
        recyclerView.setAdapter(mAdapter);
    }

    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(requireActivity().getCurrentFocus()!=null){
                requireActivity().getCurrentFocus().clearFocus();
                hideKeyboardFrom(getContext(),getView());
                }
            }
        });
        mViewModel.getLocations().observe(this, new Observer<List<Location_Model>>() {
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

    private void geoLocate(String query){
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(query, 1);
        }catch (IOException e){
            Log.e("MapFragment", "geoLocate: IOException: " + e.getMessage() );
        }
        if(list.size() > 0){
            moveToLocation(list.get(0));
        }
    }

    /**
     * Position the map to the first result from the Geolocate query.
     * @param address Result returned from Geolocate query
     */
    private void moveToLocation(Address address){
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()),17.0f));
        String markerTitle = String.format("%s %s, %s",
                address.getSubThoroughfare(),
                address.getThoroughfare(),
                address.getLocality());
        mMap.addMarker(new MarkerOptions().position(latLng)).setTitle(markerTitle);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Load prompt to either add location to user or set as event location
        return false;
    }

    @Override
    public void onLocationClick(Location_Model location) {
        //Move the map to the location
        //Load prompt to either add location to user or set as event location

    }

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

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
