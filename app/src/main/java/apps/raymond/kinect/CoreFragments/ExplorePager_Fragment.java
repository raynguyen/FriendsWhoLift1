package apps.raymond.kinect.CoreFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.R;


public class ExplorePager_Fragment extends Fragment {

    public interface PagerFragmentInterface{
        int getCurrentPosition();
        void setCurrentPosition(int position);
    }

    private PagerFragmentInterface pagerFragmentInterface;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof PagerFragmentInterface){
            pagerFragmentInterface = (PagerFragmentInterface) context;
        }
    }

    private ViewPager viewPager;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewPager = (ViewPager) inflater.inflate(R.layout.fragment_pager_explore, container,
                false);
        viewPager.setAdapter(new ExplorePager_Adapter(this));
        if(pagerFragmentInterface != null){
            viewPager.setCurrentItem(pagerFragmentInterface.getCurrentPosition());
            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

                @Override
                public void onPageSelected(int i) {
                    pagerFragmentInterface.setCurrentPosition(i);
                }

            });
        }
        return viewPager;
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
