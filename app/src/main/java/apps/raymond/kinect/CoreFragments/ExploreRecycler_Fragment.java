package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.Interfaces.ExploreEventsInterface;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UIResources.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class ExploreRecycler_Fragment extends Fragment implements ExploreRecycler_Adapter.ExploreAdapterInterface {
    private static final String TAG = "ExploreRecycler";
    private Core_ViewModel mViewModel;
    private ExploreEventsInterface mInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        try{
            mInterface = (ExploreEventsInterface) getParentFragment();
        } catch (ClassCastException e){
            //Do something??
        }
    }

    private RecyclerView mRecycler;
    private ExploreRecycler_Adapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_explore_recycler, container, false);
        mRecycler.addItemDecoration(new Margin_Decoration_RecyclerView());
        mAdapter = new ExploreRecycler_Adapter(this);
        mRecycler.setAdapter(mAdapter);
        return mRecycler;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getSuggestedEvents().observe(requireActivity(), (List<Event_Model> events)->{
            if(events != null){
                mAdapter.setData(events);
            }
        });
    }

    @Override
    public void onItemViewClick(Event_Model event, int position, View transitionView) {
        Log.w(TAG,"test for click call back");
        mInterface.setItemPosition(position);
        LatLng latLng = new LatLng(event.getLat(), event.getLng());

        mInterface.animateMap(latLng);

        getFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(transitionView, transitionView.getTransitionName())
                .replace(R.id.container_explore_fragments, new ExplorePager_Fragment(),
                        ExplorePager_Fragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
    }
}
