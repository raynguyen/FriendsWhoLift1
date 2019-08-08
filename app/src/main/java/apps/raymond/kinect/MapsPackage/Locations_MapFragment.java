package apps.raymond.kinect.MapsPackage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import apps.raymond.kinect.Adapters.Locations_Adapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;

/*
ToDo: When searching the Locations_Model recycler, slide up to hide the MapView and only show recycler.
 When setting a new location for the event, check to see if there is already an existing set location,
 if yes change the marker color of the old location back to red/cyan depending on what it is.
 */
public class Locations_MapFragment extends BaseMap_Fragment implements OnMapReadyCallback,
        Locations_Adapter.LocationClickInterface {
    public static final boolean EVENT_ACTIVITY = false; //Flag to determine of this map fragment instance is related to Event creation or detail.
    public static final boolean EVENT_PROFILE = true; //Flag to determine of this map fragment instance is related to viewing a profile.
    private MapCardViewClick mPositiveCallback;

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
            Log.w("LocationsMapFragment","The parent context does not implement the interface.");
            //Some error
        }
    }

    //private String mUserID;
    private Profile_ViewModel mViewModel;
    private boolean mFlagProfile;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Profile_ViewModel.class);
        if(getArguments()!=null){
            //mUserID = getArguments().getString("userid");
            mFlagProfile = getArguments().getBoolean("flag");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_locations,container,false);
        mMapView = v.findViewById(R.id.mapview_event_create);
        return v;
    }

    private ImageButton btnShowRecycler;
    private TextView txtNullData, txtLocationName;
    private ProgressBar progressBar;
    private Locations_Adapter mAdapter;
    private ViewGroup mLocationCard;
    private Marker mLastMarker;
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup mRecyclerGroup = view.findViewById(R.id.relative_locations_recycler);

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
                        R.drawable.ic_keyboard_arrow_up_black_24dp,null));
            }
        });

        btnShowRecycler.setOnClickListener((View v)->{
            if(((LinearLayout.LayoutParams) mRecyclerGroup.getLayoutParams()).weight==0){
                mRecyclerGroup.setLayoutParams(showParams);
                btnShowRecycler.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_keyboard_arrow_down_black_24dp,null));
            } else {
                mRecyclerGroup.setLayoutParams(hideParams);
                btnShowRecycler.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_keyboard_arrow_up_black_24dp,null));
            }
        });

        txtNullData = view.findViewById(R.id.text_null_locations);
        progressBar = view.findViewById(R.id.progress_bar_locations);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_locations);
        SearchView mSearchView = view.findViewById(R.id.searchview_locations);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        getDeviceLocation();

        View btnLocation = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) btnLocation.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_END,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_START,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 40);

        mMap.setOnMapClickListener((LatLng latLng)-> {
            if(requireActivity().getCurrentFocus()!=null){
                requireActivity().getCurrentFocus().clearFocus();
                hideKeyboardFrom(getContext(),getView());
            }
            if(mLocationCard.getVisibility()==View.VISIBLE){
                mLocationCard.setVisibility(View.GONE);
            }
        });

        mMap.setOnMarkerClickListener((Marker marker)-> {
            if(marker.getTag() instanceof Location_Model){
                onLocationClick((Location_Model) marker.getTag());
            }
            return true;
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
        //mViewModel.loadUserLocations(mUserID);

    }

    private Location_Model mLocationResult;

    /**
     * Method call whenever an item held by the Location_Model recycler is clicked.
     * @param location the Location_Model held by the view holder of the containing recycler.
     */
    @Override
    public void onLocationClick(Location_Model location) {
        mLastMarker = mMarkersMap.get(location.getLatLng());
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

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
