package apps.raymond.kinect.UserProfile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.MapsPackage.Locations_Adapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;
import apps.raymond.kinect.ViewModels.ProfileFragment_ViewModel;

public class ProfileLocations_Fragment extends BaseMap_Fragment implements
        Locations_Adapter.LocationClickInterface {
    private Profile_ViewModel mActViewModel;
    private ProfileFragment_ViewModel mFragViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActViewModel = ViewModelProviders.of(requireActivity()).get(Profile_ViewModel.class);
        if(getParentFragment()!=null){
            mFragViewModel = ViewModelProviders.of(getParentFragment()).get(ProfileFragment_ViewModel.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager_profile_locations, container, false);
        mMapView = view.findViewById(R.id.mapview_profile);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_profile_locations);
        Locations_Adapter adapter = new Locations_Adapter(this);
        recyclerView.setAdapter(adapter);

        ProgressBar progressBar = view.findViewById(R.id.progress_profile_locations);
        TextView txtNullData = view.findViewById(R.id.text_profile_locations_null);

        if(getParentFragment() instanceof PersonalProfile_Fragment){
            mActViewModel.getLocations().observe(getParentFragment(),
                    (@Nullable List<Location_Model> locations) -> {
                progressBar.setVisibility(View.GONE);
                if(locations!= null){
                    if(locations.size()>0){
                        adapter.setData(locations);
                    } else {
                        txtNullData.setVisibility(View.VISIBLE);
                    }
                }
            });
            mActViewModel.loadUserLocations();
        } else if (getParentFragment() instanceof Profile_Fragment){
            mFragViewModel.loadUserLocations();
        }


    }

    @Override
    public void onLocationClick(Location_Model location) {
        if(!(mMapView.getHeight() > 0)){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,0,1.0f);
            mMapView.setLayoutParams(params);
        }
    }
}
