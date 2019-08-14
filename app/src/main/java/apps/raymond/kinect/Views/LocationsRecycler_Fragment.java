/*
 * Fragment class whose Layout contains a RecyclerView that is to be populated with a Location_Model
 * data set. We expect that this fragment will be utilized in both the CreateEvent_Fragment and the
 * Profile_Fragment to display a user's stored Location set.
 */
package apps.raymond.kinect.Views;

import android.content.Context;
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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
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
    private RecyclerView recyclerView;
    private TextView textNull;
    private ProgressBar progressBar;
    private Locations_Adapter mAdapter;
    private LocationsRecyclerInterface mInterface;

    public interface LocationsRecyclerInterface{
        /*
        Interface to cascade interactions with adapter items upstream to the parent fragment.
         */
        void listenForLocations();
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
        recyclerView = view.findViewById(R.id.recycler_simple);
        textNull = view.findViewById(R.id.text_null_simple);
        progressBar = view.findViewById(R.id.progress_simple);

        mAdapter = new Locations_Adapter(this);
        recyclerView.setAdapter(mAdapter);

        mInterface.listenForLocations();
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

    public void setAdapterData(List<Location_Model> locations){
        Log.w(TAG,"Calling on the adapter to set a list of locations.");
        if(locations != null){
            Log.w(TAG,"Size of locations = "+ locations.size());
            mAdapter.setData(locations);
        }
    }
}
