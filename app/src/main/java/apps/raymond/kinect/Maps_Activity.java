package apps.raymond.kinect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//ToDo: Load the user's existing location storage from FireStore. Add markers to each location.
public class Maps_Activity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "MapsActivity";
    public static final String ADDRESS = "Address";
    private static final int LOCATION_REQUEST_CODE = 0;
    private static final float DEFAULT_ZOOM = 17.0f;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    boolean locationGranted;
    private EditText addressSearch;
    private RelativeLayout mConfirmDialog;
    private Button addLocationBtn, cancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_);

        addressSearch = findViewById(R.id.address_search_txt);
        addressSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationGranted = false;
        int locationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(locationPermission != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        } else {
            locationGranted = true;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mConfirmDialog = findViewById(R.id.location_confirmation_dialog);
        addLocationBtn = findViewById(R.id.confirm_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        addLocationBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void geoLocate(String query){
        Geocoder geocoder = new Geocoder(Maps_Activity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(query, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            moveToLocation(list);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getBaseContext(),"Implement details. Show confirmation dialog.",Toast.LENGTH_LONG).show();
                mConfirmDialog.setVisibility(View.VISIBLE);
                return false;
            }
        });
        getDeviceLocation();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mConfirmDialog.getVisibility()==View.VISIBLE){
                    mConfirmDialog.setVisibility(View.GONE);
                }
            }
        });
    }

    Location mLastLocation;
    private void getDeviceLocation(){
        try{
            if(locationGranted){
                mFusedLocationClient.getLastLocation()
                        .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult()!=null){
                                        mLastLocation = task.getResult();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(mLastLocation.getLatitude(),
                                                        mLastLocation.getLongitude()),
                                                DEFAULT_ZOOM
                                        ));
                                    }
                                } else {
                                    Log.d(TAG, "Current location is null. Using defaults.");
                                    Log.e(TAG, "Exception: %s", task.getException());
                                    //ToDo: Set a default location and zoom when unable to retrieve current location.
                                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                }
                            }
                        });
            }
        } catch (SecurityException e){
            Log.w(TAG,"There was an error retrieving the device location.",e);
        }
    }

    private Address address;
    private void moveToLocation(List<Address> addresses){
        address = addresses.get(0);
        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM));
        mMap.addMarker(new MarkerOptions().position(latLng)).setTitle("HAHAHA");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationGranted=true;
                    if(mMap!=null){
                        getDeviceLocation();
                    }
                } else {
                    Toast.makeText(this,"Unable to determine your location.",Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i){
            case R.id.confirm_btn:
                if(address!=null){
                    Intent intent = new Intent();
                    intent.putExtra(ADDRESS, address);
                    setResult(RESULT_OK,intent);
                    finish();
                }
                break;
            case R.id.cancel_btn:
                mConfirmDialog.setVisibility(View.GONE);
                break;
        }
    }
}
