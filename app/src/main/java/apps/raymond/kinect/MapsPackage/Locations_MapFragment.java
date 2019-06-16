package apps.raymond.kinect.MapsPackage;

import android.Manifest;
import android.app.Activity;
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
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import apps.raymond.kinect.Location_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;

/*
ToDo: When searching the Locations_Model recycler, slide up to hide the MapView and only show recycler.
 */
public class Locations_MapFragment extends Fragment implements OnMapReadyCallback,
        Locations_Adapter.LocationClickInterface {
    private static final int LOCATION_REQUEST_CODE = 0;
    public static final boolean EVENT_ACTIVITY = false;
    public static final boolean EVENT_PROFILE = true;
    private MapCardViewClick mPositiveCallback;
    private Map<LatLng, Marker> mMarkersMap = new ConcurrentHashMap<>();

    public interface MapCardViewClick {
        void onCardViewPositiveClick(Location_Model location);
    }
    /*
     * Need an identifier to determine if we need to load the card to add as a user location or if
     * we want to set the mAddress for an event.
     */
    public static Locations_MapFragment newInstance(String userID, boolean flag){
        Locations_MapFragment fragment = new Locations_MapFragment();
        Bundle args = new Bundle();
        args.putString("userid",userID);
        args.putBoolean("flag",flag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mPositiveCallback = (MapCardViewClick) context;
        }catch (ClassCastException e){
            //Some error
        }
    }

    private FusedLocationProviderClient mFusedLocationClient;
    private String mUserID;
    private Profile_ViewModel mViewModel;
    private boolean mFlagProfile;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(Profile_ViewModel.class); //Todo: Make a ViewModel Specific for Locations!
        mUserID = getArguments().getString("userid");
        mFlagProfile = getArguments().getBoolean("flag");

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
    private ImageButton btnShowRecycler;
    private TextView txtNullData, txtLocationName;
    private ProgressBar progressBar;
    private Locations_Adapter mAdapter;
    private ViewGroup mRecyclerGroup, mLocationCard;
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapview_event_create);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mRecyclerGroup = view.findViewById(R.id.relative_locations_recycler);

        btnShowRecycler = view.findViewById(R.id.button_view_locations);
        EditText editSearchMap = view.findViewById(R.id.edit_search_location);
        editSearchMap.setOnEditorActionListener((TextView v, int actionId, KeyEvent event)->{
            if(actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.KEYCODE_ENTER){
                geoLocate(v.getText().toString());
            }
            return false;
        });

        final LinearLayout.LayoutParams showParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,0,1.0f);
        final LinearLayout.LayoutParams hideParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,0,0f);

        editSearchMap.setOnFocusChangeListener((View v, boolean hasFocus)->{
            if(hasFocus){
                mRecyclerGroup.setLayoutParams(hideParams);
                if(mLocationCard.getVisibility()==View.VISIBLE){
                    mLocationCard.setVisibility(View.GONE);
                }
                btnShowRecycler.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.baseline_keyboard_arrow_up_black_18dp,null));
            }
        });

        btnShowRecycler.setOnClickListener((View v)->{
            if(((LinearLayout.LayoutParams) mRecyclerGroup.getLayoutParams()).weight==0){
                mRecyclerGroup.setLayoutParams(showParams);
                btnShowRecycler.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.baseline_keyboard_arrow_down_black_18dp,null));
            } else {
                mRecyclerGroup.setLayoutParams(hideParams);
                btnShowRecycler.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.baseline_keyboard_arrow_up_black_18dp,null));
            }
        });

        txtNullData = view.findViewById(R.id.text_null_locations);
        progressBar = view.findViewById(R.id.progress_bar_locations);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_locations);
        SearchView mSearchView = view.findViewById(R.id.searchview_locations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Locations_Adapter(this);
        recyclerView.setAdapter(mAdapter);

        mLocationCard = view.findViewById(R.id.cardview_marker_dialog);
        Button btnCardPositive = view.findViewById(R.id.button_positive_location);
        ImageButton btnReturn = view.findViewById(R.id.button_map_return);
        txtLocationName = view.findViewById(R.id.text_location_address);

        if(mFlagProfile){
            btnCardPositive.setText(getString(R.string.save_location));
            btnReturn.setOnClickListener((View v)->requireActivity().onBackPressed());
        } else {
            btnCardPositive.setText(getString(R.string.set_location));
            btnReturn.setVisibility(View.GONE);

        }
        btnCardPositive.setOnClickListener((View v)->{
            mPositiveCallback.onCardViewPositiveClick(mLocationResult);
            mLocationCard.setVisibility(View.GONE);
            if(mFlagProfile){
                mLastMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            } else {
                mLastMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            }
        });

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
        mMap.setOnMapClickListener((LatLng latLng)-> {
            if(requireActivity().getCurrentFocus()!=null){
                requireActivity().getCurrentFocus().clearFocus();
                hideKeyboardFrom(getContext(),getView());
            }
            if(mLocationCard.getVisibility()==View.VISIBLE){
                mLocationCard.setVisibility(View.GONE);
            }
        });

        mViewModel.getLocations().observe(this,(@Nullable List<Location_Model> location_models)->{
            progressBar.setVisibility(View.GONE);
            if(location_models!=null){
                if(location_models.size()==0){
                    txtNullData.setVisibility(View.VISIBLE);
                }
                mAdapter.setData(location_models);
                for(Location_Model location : location_models){
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLat(),location.getLng()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    marker.setTag(location);
                    mMarkersMap.put(marker.getPosition(),marker);
                }
            }
        });
        mViewModel.loadUserLocations(mUserID);

        mMap.setOnMarkerClickListener((Marker marker)-> {
            if(marker.getTag() instanceof Location_Model){
                onLocationClick((Location_Model) marker.getTag());
            }
            return true;
        });
    }

    Location myLocation;
    private void getDeviceLocation(){
        try{
            mFusedLocationClient.getLastLocation().addOnCompleteListener(requireActivity(), (Task<Location> task)-> {
                if(task.isSuccessful() && task.getResult()!=null){
                    myLocation = task.getResult();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),
                            myLocation.getLongitude()),17.0f));
                }
            });
        } catch (SecurityException e){}
    }

    private Marker mLastMarker;
    private Location_Model mLocationResult;
    private void geoLocate(String query){
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> queryResults = new ArrayList<>(1);
        try{
            queryResults = geocoder.getFromLocationName(query, 1);
        }catch (IOException e){
            Log.e("MapFragment", "geoLocate: IOException: " + e.getMessage() );
        }
        if(queryResults.size() > 0){
            mLocationResult = new Location_Model(null, queryResults.get(0));
            LatLng newLatLng = mLocationResult.getLatLng();
            if(!mMarkersMap.containsKey(mLocationResult.getLatLng())){
                mLastMarker = mMap.addMarker(new MarkerOptions().position(newLatLng));
                mLastMarker.setTag(mLocationResult);
                mMarkersMap.put(newLatLng,mLastMarker);
            }
            mLocationCard.setVisibility(View.VISIBLE);
            txtLocationName.setText(mLocationResult.getAddress());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng,17.0f));
        }
    }

    /**
     * Method call whenever an item held by the Location_Model recycler is clicked.
     * @param location the Location_Model held by the view holder of the containing recycler.
     */
    @Override
    public void onLocationClick(Location_Model location) {
        mLocationResult = location;
        if(location.getLookup()==null || location.getLookup().length()<1){
            txtLocationName.setText(location.getAddress());
        } else {
            txtLocationName.setText(location.getLookup());
        }
        mLocationCard.setVisibility(View.VISIBLE);
        txtLocationName.setText(location.getAddress());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location.getLatLng(),17.0f));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==LOCATION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getDeviceLocation();
                try{
                    mMap.setMyLocationEnabled(true);
                }
                catch (SecurityException se){
                }
            }
        }
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
