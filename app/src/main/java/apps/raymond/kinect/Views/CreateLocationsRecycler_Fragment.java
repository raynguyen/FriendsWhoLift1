package apps.raymond.kinect.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import apps.raymond.kinect.CoreFragments.EventCreate_ViewModel;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.R;

public class CreateLocationsRecycler_Fragment extends Fragment {
    private static final String TAG = "CreationLocationsRecycler";
    private EventCreate_ViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            mViewModel = ViewModelProviders.of(getParentFragment()).get(EventCreate_ViewModel.class);
        } catch (NullPointerException npe){
            Log.w(TAG,"Error getting parent fragment.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getUserLocations().observe(this, (List<Location_Model> location_models) -> {
            Log.w(TAG,"There was a change in the user locations!");
        });
    }

}
