package apps.raymond.kinect.Events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventCreate_Activity extends AppCompatActivity {
    private static final String TAG ="EventCreateActivity";

    private User_Model mUserModel;
    private String mUserID;
    private ArrayList<Event_Model> mEventList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_create_activity);



        if(getIntent().getExtras()!=null){
            mUserModel = getIntent().getExtras().getParcelable("user");
            mUserID = mUserModel.getEmail();
            if(getIntent().hasExtra("events")){
                mEventList = getIntent().getExtras().getParcelableArrayList("events");
            }
        }

        ViewPager mViewPager = findViewById(R.id.viewpager_eventcreate);
        EventCreateAdapter mAdapter = new EventCreateAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        TabLayout mTabs = findViewById(R.id.tablayout_eventcreate);
        mTabs.setupWithViewPager(mViewPager);
    }

    private class EventCreateAdapter extends FragmentPagerAdapter{

        private EventCreateAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    //DETAIL FRAGMENT
                    break;
                case 1:
                    //MAP FRAGMENT
                    //Pass the list of events to this fragment.
                    break;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Details";
                case 1:
                    return "Location";
            }
            return null;
        }
    }
}
