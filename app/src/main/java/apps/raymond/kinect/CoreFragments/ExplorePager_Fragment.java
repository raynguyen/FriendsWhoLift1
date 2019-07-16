package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.Interfaces.ExploreEventsInterface;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Core_ViewModel;


public class ExplorePager_Fragment extends Fragment implements
        PagerEvent_Fragment.PagerEventInterface {
    private static final String TAG = ExplorePager_Fragment.class.getSimpleName();
    private ExploreEventsInterface mInterface;
    private ViewPager viewPager;
    private TextView textFooter;
    private Core_ViewModel mViewModel;
    private ExplorePager_Adapter mPagerAdapter;

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

        mPagerAdapter = new ExplorePager_Adapter(this);
        viewPager.setAdapter(mPagerAdapter);

        mViewModel.getSuggestedEvents().observe(requireActivity(), (List<Event_Model>  events)->{
            if(events != null){
                mPagerAdapter.setData(events);
                setFooter();
            }
        });

        viewPager.setCurrentItem(mInterface.getItemPosition());
        //Page change listener added so that we can reflect user scrolling in the parent recycler.
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                mInterface.setItemPosition(i);
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
                    Fragment currentFragment = (Fragment) viewPager.getAdapter()
                            .instantiateItem(viewPager, mInterface.getItemPosition());

                    View view = currentFragment.getView();
                    if(view == null){
                        return;
                    }
                    sharedElements.put(names.get(0), view.findViewById(R.id.text_event_name));
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
    public class ExplorePager_Adapter extends FragmentStatePagerAdapter {

        private List<Event_Model> eventSet;
        ExplorePager_Adapter(Fragment fragment){
            super(fragment.getChildFragmentManager());
        }

        public void setData(List<Event_Model> newDataSet){
            if(eventSet == null){
                eventSet = new ArrayList<>(newDataSet);
            } else {
                eventSet.clear();
                eventSet.addAll(newDataSet);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int i) {
            if(eventSet.size() == 0 ){
                return null;
            }
            return PagerEvent_Fragment.newInstance(eventSet.get(i), i);
        }

        @Override
        public int getCount() {
            if(eventSet!=null){
                return eventSet.size();
            } else {
                return 0;
            }
        }
    }

}
