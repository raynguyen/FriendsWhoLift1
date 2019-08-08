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
import apps.raymond.kinect.Interfaces.LocationsListenerInterface;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.R;

public class LocationsRecycler_Fragment extends Fragment implements LocationsListenerInterface {
    private static final String TAG = "CreationLocationsRecycler";
    private EventCreate_ViewModel mViewModel;
    private RecyclerView recyclerView;
    private TextView textNull;
    private ProgressBar progressBar;
    private Locations_Adapter mAdapter;

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
        View view = inflater.inflate(R.layout.fragment_simple_recycler, container, false);
        recyclerView = view.findViewById(R.id.recycler_simple);
        textNull = view.findViewById(R.id.text_null_simple);
        progressBar = view.findViewById(R.id.progress_simple);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void setLocations(List<Event_Model> events) {

    }
}
