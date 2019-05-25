package apps.raymond.kinect.EventCreate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventCreate_Activity extends AppCompatActivity{
    private String mUserID;
    private ArrayList<Event_Model> mEventList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        if(getIntent().getExtras()!=null){
            User_Model mUserModel = getIntent().getExtras().getParcelable("user");
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

    //ToDo: Show the YeSNoDialog.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
        yesNoDialog.setCancelable(false);

        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
    }

    private class EventCreateAdapter extends FragmentPagerAdapter{

        private EventCreateAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return new EventCreate_Details_Fragment();
                case 1:
                    return EventCreate_Map_Fragment.newInstance(mUserID,mEventList); //TEST THIS.
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
