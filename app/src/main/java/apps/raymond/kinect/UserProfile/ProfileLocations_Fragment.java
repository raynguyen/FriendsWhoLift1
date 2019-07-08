package apps.raymond.kinect.UserProfile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.MapsPackage.Locations_Adapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.ProfileActivity_ViewModel;
import apps.raymond.kinect.ViewModels.ProfileFragment_ViewModel;

public class ProfileLocations_Fragment extends BaseMap_Fragment implements
        Locations_Adapter.LocationClickInterface {
    private ProfileActivity_ViewModel mActViewModel;
    private ProfileFragment_ViewModel mFragViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActViewModel = ViewModelProviders.of(requireActivity()).get(ProfileActivity_ViewModel.class);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);



        if(getParentFragment() instanceof PersonalProfile_Fragment){
            mActViewModel.getLocations().observe(getParentFragment(),
                    (@Nullable List<Location_Model> locations) -> {
                if(locations!= null){
                    adapter.setData(locations);
                }
            });
            mActViewModel.loadUserLocations();
        } else if (getParentFragment() instanceof Profile_Fragment){
            mFragViewModel.loadUserLocations();
        }


    }

    @Override
    public void onLocationClick(Location_Model location) {

    }
}
