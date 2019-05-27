package apps.raymond.kinect.EventCreate;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventCreate_Activity extends AppCompatActivity{
    private static final int CANCEL_REQUEST_CODE = 21;
    private String mUserID;
    private ArrayList<Event_Model> mEventList;

    private EventCreate_ViewModel mViewModel;
    private User_Model mUserModel;
    private Event_Model mEvent = new Event_Model();
    private String mEventName;
    private String mEventDesc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);
        mViewModel = ViewModelProviders.of(this).get(EventCreate_ViewModel.class);

        Toolbar mToolbar = findViewById(R.id.toolbar_event_create);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
            }
        });

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

        mViewModel.getEventName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mEventName = s;
            }
        });

        mViewModel.getEventDesc().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mEventDesc = s;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }



    //ToDo: Show the YeSNoDialog.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
        yesNoDialog.setCancelable(false);

        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode== Activity.RESULT_OK) {
            if (requestCode == CANCEL_REQUEST_CODE) {
                //FINISH ACTIVITY
            }
        }

    }

    private void checkFields(){

    }

    private class EventCreateAdapter extends FragmentPagerAdapter{

        private EventCreateAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return EventCreate_Details_Fragment.newInstance(mUserModel);
                case 1:
                    return EventCreate_Map_Fragment.newInstance(mUserID,mEventList);
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
