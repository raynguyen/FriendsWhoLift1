package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Core_ViewModel;


public class ExplorePager_Fragment extends Fragment {
    private static final String TAG = ExplorePager_Fragment.class.getSimpleName();

    public interface PagerFragmentInterface{
        int getCurrentPosition();
        void setCurrentPosition(int position);
    }


    private PagerFragmentInterface pagerFragmentInterface;
    private ViewPager viewPager;
    private Core_ViewModel mViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getParentFragment() != null){
            pagerFragmentInterface = (PagerFragmentInterface) getParentFragment();
            mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewPager = (ViewPager) inflater.inflate(R.layout.fragment_pager_explore, container,
                false);

        ExplorePager_Adapter adapter = new ExplorePager_Adapter(this);
        viewPager.setAdapter(new ExplorePager_Adapter(this));
        adapter.setData(mViewModel.getSuggestedEvents().getValue());

        if(pagerFragmentInterface != null){
            viewPager.setCurrentItem(pagerFragmentInterface.getCurrentPosition());
            //Page change listener added so that we can reflect user scrolling in the parent recycler.
            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int i) {
                    pagerFragmentInterface.setCurrentPosition(i);
                }
            });
        }
        return viewPager;

        //return inflater.inflate(R.layout.fragment_pager_explore, container, false);
    }


    /**
     * Adapter class that binds to the ExplorePager_Fragment's ViewPager. This adapter is responsible
     * for managing the events that get displayed by the ExploreEvent's ViewPager. When an event is
     * clicked on from the ExploreEvent's RecyclerView, we instantiate an instance of this Fragment and
     * load the contents to the correct position (i.e. the  adapter position of the recycler view item
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
                //DIFFUTIL SHIT
            }
        }

        @Override
        public Fragment getItem(int i) {
            Log.w(TAG,"DOES THIS SHIT GET CALLED/: "+ eventSet.size());
            return PagerEvent_Fragment.newInstance(eventSet.get(i));
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
