/**
 * This is an attempt at consolidating the code from EventExplore_Fragment and Locations_Fragment.
 * -Both of these fragments contain similar attributes and methods so we can experiment with creating
 *  a base class.
 */
package apps.raymond.kinect.MapsPackage;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Location_Model;
import apps.raymond.kinect.Locations_Adapter;
import apps.raymond.kinect.R;

public class BaseMap_Fragment extends Fragment implements OnMapReadyCallback,
        Locations_Adapter.LocationClickInterface {
    private GoogleMap mMap;
    private Location_Model mLocationModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    public void onLocationItemClick(Location_Model location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void geoLocate(String query){
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> queryResults = new ArrayList<>(1);
        try{
            queryResults = geocoder.getFromLocationName(query, 1);
        }catch (IOException e){
            Log.e("MapFragment", "geoLocate: IOException: " + e.getMessage() );
        }
        if(queryResults.size() > 0){
            LatLng latLng = new LatLng(queryResults.get(0).getLatitude(),queryResults.get(0).getLongitude());
            mLocationModel = new Location_Model(null,queryResults.get(0));
            mMap.addMarker(new MarkerOptions().position(latLng)).setTag(mLocationModel);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
        }
    }
}
