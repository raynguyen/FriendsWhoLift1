package apps.raymond.kinect.CoreFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.Interfaces.ExploreEventsInterface;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Core_ViewModel;


public class ExplorePager_Fragment extends Fragment implements
        PagerEvent_Fragment.PagerEventInterface {
    private static final String TAG = ExplorePager_Fragment.class.getSimpleName();
    private ExploreEventsInterface mInterface;
    private ViewPager2 viewPager;
    private TextView textFooter;
    private Core_ViewModel mViewModel;
    private ExplorePager2_Adapter mPager2Adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getParentFragment() != null){
            mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
            mInterface = (ExploreEventsInterface) getParentFragment();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_explore, container, false);
        viewPager = view.findViewById(R.id.pager_explore);
        textFooter = view.findViewById(R.id.text_pages_footer);

        DisplayMetrics metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int padding = (int) Math.ceil(4 * metrics.density);
        viewPager.setPadding(padding, 0, padding, 0);

        mPager2Adapter = new ExplorePager2_Adapter(this);
        viewPager.setAdapter(mPager2Adapter);

        mViewModel.getSuggestedEvents().observe(requireActivity(), (List<Event_Model>  events)->{
            if(events != null){
                mPager2Adapter.setData(events);
                setFooter();
            }
        });

        viewPager.setCurrentItem(mInterface.getItemPosition());
        //Page change listener added so that we can reflect user scrolling in the parent recycler.
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mInterface.setItemPosition(position);
                mInterface.animateMapToLocation();
                setFooter();
            }
        });

        setEnterTransitionAnimation();

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        /*if (savedInstanceState == null) {
            postponeEnterTransition();
        }*/
        return view;
    }

    /**
     * Set the transition when we move from the ExplorePager fragment back to the ExploreRecycler
     * fragment.
     */
    private void setEnterTransitionAnimation(){
        Transition transition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.shared_element_transition);
        setSharedElementEnterTransition(transition);

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if(viewPager.getAdapter() != null){
                    /*
                    Fragment currentFragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, mInterface.getItemPosition());

                    View view = currentFragment.getView();
                    if(view == null){
                        return;
                    }
                    sharedElements.put(names.get(0), view.findViewById(R.id.text_event_name));
                    */
                }
            }
        });
    }

    private void setFooter(){
        int position = mInterface.getItemPosition() + 1;
        int size = mViewModel.getSuggestedEvents().getValue().size();
        String footer = position + "/" + size;
        textFooter.setText(footer);
    }

    @Override
    public void attendEvent() {
        mInterface.attendEvent();
    }

    @Override
    public void ignoreEvent() {

    }

    @Override
    public void observeEvent() {

    }

    /**
     * Adapter class that binds to the ExplorePager_Fragment's ViewPager. This mPagerAdapter is responsible
     * for managing the events that get displayed by the ExploreEvent's ViewPager. When an event is
     * clicked on from the ExploreEvent's RecyclerView, we instantiate an instance of this Fragment and
     * load the contents to the correct position (i.e. the  mPagerAdapter position of the recycler view item
     * that was clicked).
     */

    private class ExplorePager2_Adapter extends FragmentStateAdapter{
        private List<Event_Model> dataSet;

        ExplorePager2_Adapter(Fragment fragment){
            super(fragment);
        }

        public void setData(List<Event_Model> newDataSet){
            if(dataSet == null){
                dataSet = new ArrayList<>(newDataSet);
            } else {
                dataSet.clear();
                dataSet.addAll(newDataSet);
            }
            notifyItemRangeChanged(0, dataSet.size());
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return PagerEvent_Fragment.newInstance(dataSet.get(position), position);
        }

        @Override
        public int getItemCount() {
            if(dataSet != null){
                return dataSet.size();
            }
            return 0;
        }
    }

}
