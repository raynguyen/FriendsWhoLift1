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
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import apps.raymond.kinect.R;

/**
 * Base fragment that controls the UI for a MapView.
 */
public class BaseMap_Fragment extends Fragment implements OnMapReadyCallback{
    private static final int LOCATION_REQUEST_CODE = 0;
    protected FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    protected Marker mResultMarker;
    protected Address mResultAddress;
    protected Map<LatLng, Marker> mMarkersMap = new ConcurrentHashMap<>(); //Concurrent hash-map allows multiple threads to access the data should it not be accessing the same keys.
    protected MapView mMapView;
    protected GoogleMap mMap;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_base,container,false);
        mMapView = v.findViewById(R.id.map_view);
        EditText textSearch = v.findViewById(R.id.text_search_map);
        textSearch.setOnEditorActionListener((TextView view, int actionId, KeyEvent event)->{
            if(actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.KEYCODE_ENTER){
                geoLocate(view.getText().toString());
            }
            return false;
        });
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try{
            googleMap.setMyLocationEnabled(true);
            View btnLocation = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent())
                    .findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) btnLocation.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_END,0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_START,0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 0, 40);
            getDeviceLocation();
        } catch (SecurityException e){
            //Some error
        }
    }

    protected void geoLocate(String query){
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> queryResults = new ArrayList<>(1);
        try{
            queryResults = geocoder.getFromLocationName(query, 1);
        }catch (IOException e){
            Log.e("MapFragment", "geoLocate: IOException: " + e.getMessage() );
        }
        if(queryResults.size() > 0){
            mResultAddress = queryResults.get(0);
            LatLng latLng = new LatLng(mResultAddress.getLatitude(), mResultAddress.getLongitude());
            if(!mMarkersMap.containsKey(latLng)){
                Location_Model resultLocation = new Location_Model(null,mResultAddress);
                mResultMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                mResultMarker.setTag(resultLocation);
                mMarkersMap.put(latLng,mResultMarker);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
        }
    }

    protected void getDeviceLocation(){
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
        } catch (SecurityException e){
            //Unable to get device location for some reason.
        }
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
                    //Permission not granted to find location.
                }
            }
        }
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
