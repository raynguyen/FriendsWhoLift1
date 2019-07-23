package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import apps.raymond.kinect.ObjectModels.Event_Model;
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
        mRecycler.addItemDecoration(new Margin_Decoration_RecyclerView(requireActivity()));
        mAdapter = new ExploreRecycler_Adapter(this);
        mRecycler.setAdapter(mAdapter);

        setExitTransitionAnimation();
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
        mInterface.setItemPosition(position);
        mInterface.animateMapToLocation();

        getFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(transitionView, transitionView.getTransitionName())
                .replace(R.id.container_explore_fragments, new ExplorePager_Fragment(),
                        ExplorePager_Fragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Prepares the exit and the reenter transition animation for the this fragment (i.e. we set
     * the animations for exiting this fragment when a recycler view holder item is clicked).
     *
     */
    private void setExitTransitionAnimation(){
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.recycler_to_pager));

        setExitSharedElementCallback(new SharedElementCallback() {

            /**
             * Method called when an exit shared transition is required?
             *
             * @param names list containing the transition names of each view added to the map.
             * @param sharedElements mapping of views that we want to animate during the transition
             */
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                //Grab a handle on the view that we want to animate when transitioning to the pager.
                RecyclerView.ViewHolder selectedViewHolder = mRecycler
                        .findViewHolderForAdapterPosition(mInterface.getItemPosition());

                if(selectedViewHolder == null){
                    return;
                }

                sharedElements.put(names.get(0),
                        selectedViewHolder.itemView.findViewById(R.id.text_event_name));
            }
        });
    }
}
