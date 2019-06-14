/**
 * This is an attempt at consolidating the code from EventExplore_Fragment and Locations_Fragment.
 * -Both of these fragments contain similar attributes and methods so we can experiment with creating
 *  a base class.
 */
package apps.raymond.kinect.MapsPackage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import apps.raymond.kinect.Location_Model;
import apps.raymond.kinect.R;

public class BaseMap_Fragment extends Fragment implements OnMapReadyCallback,
        Locations_Adapter.LocationClickInterface {
    private static final int LOCATION_REQUEST_CODE = 0;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private Marker mLastMarker;
    private Address mLastAddress;
    private Map<LatLng, Marker> mMarkersMap = new ConcurrentHashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_locations,container,false);
    }

    private MapView mMapView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = view.findViewById(R.id.mapview_event_create);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);



        EditText editSearchMap = view.findViewById(R.id.edit_search_location);
        editSearchMap.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                geoLocate(v.getText().toString());
                return true;
            }
            return false;
        });
    }

    @Override
    public void onLocationClick(Location_Model location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    private void geoLocate(String query){
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> queryResults = new ArrayList<>(1);
        try{
            queryResults = geocoder.getFromLocationName(query, 1);
        }catch (IOException e){
            Log.e("MapFragment", "geoLocate: IOException: " + e.getMessage() );
        }
        if(queryResults.size() > 0){
            //We check the Markers map to determine if the location exists in the user's Location_Models.
            mLastAddress = queryResults.get(0);
            LatLng latLng = new LatLng(mLastAddress.getLatitude(), mLastAddress.getLongitude());
            if(!mMarkersMap.containsKey(latLng)){
                mLastMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                mLastMarker.setTag(mLastAddress);
                mMarkersMap.put(latLng,mLastMarker);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
            //mLocationCard.setVisibility(View.VISIBLE);
            //txtLocationName.setText(mLastAddress.getAddressLine(0));
        }
    }

    private void getDeviceLocation(){
        try{
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(requireActivity(), (Task<Location> task)-> {
                        if(task.isSuccessful() && task.getResult()!=null){
                            mLastLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastLocation.getLatitude(),
                                            mLastLocation.getLongitude()),17.0f));
                        }
                    });
        } catch (SecurityException e){}
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
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
