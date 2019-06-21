package apps.raymond.kinect.EventDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;

public class EventLocation_Fragment extends BaseMap_Fragment {
    private EventDetail_ViewModel mViewModel;
    private LatLng mLatLng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(EventDetail_ViewModel.class);
        if(mViewModel.getEventModel().getValue()!=null){
            mLatLng = mViewModel.getEventModel().getValue().getLatLng();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        Log.w("EventLocationFrag","Moving to latLng "+mLatLng.toString());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,17.0f));
    }
}
