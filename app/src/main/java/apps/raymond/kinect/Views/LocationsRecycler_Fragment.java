/*
 * Fragment class whose Layout contains a RecyclerView that is to be populated with a Location_Model
 * data set. We expect that this fragment will be utilized in both the CreateEvent_Fragment and the
 * Profile_Fragment to display a user's stored Location set.
 */
package apps.raymond.kinect.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apps.raymond.kinect.Adapters.Locations_Adapter;
import apps.raymond.kinect.CoreFragments.EventCreate_ViewModel;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.R;

public class LocationsRecycler_Fragment extends Fragment implements
        Locations_Adapter.LocationsListenerInterface {
    private static final String TAG = "CreationLocationsRecycler";
    private LocationsRecyclerInterface mInterface;

    public interface LocationsRecyclerInterface{
        /*
        Interface to cascade interactions with adapter items upstream to the parent fragment.
         */
        //void listenForLocations(LocationsRecycler_Fragment fragment);
        void clickLocationTest();
    }

    public LocationsRecycler_Fragment(Fragment fragment){
        try {
            mInterface = (LocationsRecyclerInterface) fragment;
        } catch (ClassCastException cce){
            Log.w(TAG,"ClassCastException on calling parent.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_recycler, container, false);
        TextView textNull = view.findViewById(R.id.text_null_simple);
        ProgressBar progressBar = view.findViewById(R.id.progress_simple);

        RecyclerView locationsRecycler = view.findViewById(R.id.recycler_simple);
        Locations_Adapter mAdapter = new Locations_Adapter(this);
        locationsRecycler.setAdapter(mAdapter);

        //mInterface.listenForLocations(this);
        EventCreate_ViewModel tempVM = ViewModelProviders.of(getParentFragment()).get(EventCreate_ViewModel.class);
        tempVM.getUserLocations().observe(this, (List<Location_Model> locations) ->{
            Log.w(TAG,"My parent is = " + getParentFragment().getClass().getSimpleName());
            progressBar.setVisibility(View.GONE);
            if(locations == null || locations.size() < 1) {
                Log.w(TAG,"Locations received but result is null or size is 0");
                textNull.setVisibility(View.VISIBLE);
            }
            else {
                for(int w = 0; w < locations.size(); w++ ){
                    Log.w(TAG,"Location name = " + locations.get(w).getAddress());
                }
                textNull.setVisibility(View.INVISIBLE);
                mAdapter.setData(locations);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLocationClick(Location_Model location) {
        /*
        ToDo: Cascade the location click up to the parent fragment.
            1. Event Create Fragment: Instantiate a new BaseMap Fragment underneath the LocationsRecyclerFragment.
            We want to still display the list, but now want to add a an interactable mapview so that
            the user has the ability to search for a location viewing panning.
            2. Profile Fragment: Simply pan the map to the clicked location and display the information
            as required.
         */
        mInterface.clickLocationTest();
    }

    /*public void setAdapterData(List<Location_Model> locations){
        Log.w(TAG,"Calling on the adapter to set a list of locations.");
        progressBar.setVisibility(View.GONE);
        if(locations != null){
            if(locations.size() < 1){
                textNull.setVisibility(View.VISIBLE);
            }
            Log.w(TAG,"Size of locations = "+ locations.size());
            mAdapter.setData(locations);
        }
    }*/
}
