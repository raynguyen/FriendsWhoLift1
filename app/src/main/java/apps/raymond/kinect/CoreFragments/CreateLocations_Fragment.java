package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.MapsPackage.Locations_Adapter;
import apps.raymond.kinect.R;

public class CreateLocations_Fragment extends Fragment implements
        Locations_Adapter.LocationClickInterface{
    private static final String TAG = "CreateLocationsFragment";
    private EventCreate_ViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getParentFragment()).get(EventCreate_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressBar progressBar = view.findViewById(R.id.progress_simple);
        TextView textNull = view.findViewById(R.id.text_null_simple);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_simple);
        Locations_Adapter adapter = new Locations_Adapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        mViewModel.getUserLocations().observe(this, (List<Location_Model> locations)->{
            progressBar.setVisibility(View.GONE);
            if(locations != null){
                if(locations.size() > 0){
                    adapter.setData(locations);
                    textNull.setVisibility(View.GONE);
                } else {
                    textNull.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onLocationClick(Location_Model location) {
        Log.w(TAG,"Clicked on a location.");
    }
}
