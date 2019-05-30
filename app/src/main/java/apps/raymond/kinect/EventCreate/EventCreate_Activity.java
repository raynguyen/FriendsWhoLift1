package apps.raymond.kinect.EventCreate;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Location_Model;
import apps.raymond.kinect.Locations_MapFragment;
import apps.raymond.kinect.R;
import apps.raymond.kinect.SoftInputAnimator.FluidContentResizer;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventCreate_Activity extends AppCompatActivity implements
        Locations_MapFragment.MapMarkerClick{
    private static final int CANCEL_REQUEST_CODE = 21;
    private EventCreate_ViewModel mViewModel;
    private ArrayList<Event_Model> mEventList;
    private User_Model mUserModel;
    private String mUserID;

    private String mEventName;
    private String mEventDesc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);
        new FluidContentResizer().listen(this);
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

        //Observes the Details fragment to determine if the there is a non-empty string for the name
        mViewModel.getEventName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mEventName = s;
                checkFields();
            }
        });
        mViewModel.getEventDesc().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mEventDesc = s;
                checkFields();
            }
        });
    }

    boolean mCreateOptionFlag = false;
    private void checkFields(){
        if(mEventName !=null && mEventDesc !=null){
            if(mEventName.trim().length() > 0 && mEventDesc.trim().length() > 0 ){
                mCreateOptionFlag = true;
            } else {
                mCreateOptionFlag = false;
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_event_create,menu);
        MenuItem item = menu.findItem(R.id.action_event_create);
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        if(mCreateOptionFlag){
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE),0,spanString.length(),0);
        } else {
            spanString.setSpan(new ForegroundColorSpan(Color.GRAY),0,spanString.length(),0);
        }
        item.setTitle(spanString);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_event_create){
            if(mCreateOptionFlag){
                final Event_Model event = mViewModel.getEventModel();
                event.setCreator(mUserID);
                event.setPrimes(mViewModel.getEventPrimes());
                event.setInvited(mViewModel.getInviteList().size());
                event.setAttending(1);
                mViewModel.createEvent(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mViewModel.addUserToEvent(mUserID,mUserModel,event.getName());
                    }
                });
                mViewModel.addEventToUser(mUserID,event);
                mViewModel.sendEventInvites(event, mViewModel.getInviteList());

                Intent returnIntent = new Intent();
                returnIntent.putExtra("event",event);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Toast.makeText(this,"Mandatory event fields must be completed.",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onLocationPositiveClick(Location_Model locationModel) {
        //Load a dialog to prompt user if they want to set the location for their event
        Log.w("CreateEventAct","Display a cardview prompting user to set location");
    }

    /**
     * Adapter class to populate the ViewPager with Details and Map fragments.
     */
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
                    return Locations_MapFragment.newInstance(mUserID,"event");
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
