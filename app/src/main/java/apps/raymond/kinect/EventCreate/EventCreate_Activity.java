package apps.raymond.kinect.EventCreate;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
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

import com.google.android.gms.tasks.Task;

import java.util.Calendar;

import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Events.EventExplore_Fragment;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Location_Model;
import apps.raymond.kinect.MapsPackage.Locations_MapFragment;
import apps.raymond.kinect.R;
import apps.raymond.kinect.SoftInputAnimator.FluidContentResizer;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventCreate_Activity extends AppCompatActivity implements
        Locations_MapFragment.MapCardViewClick, YesNoDialog.YesNoCallback {

    private EventCreate_ViewModel mViewModel;
    private User_Model mUserModel;
    private String mUserID, mEventName, mEventDesc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);
        new FluidContentResizer().listen(this);
        mViewModel = ViewModelProviders.of(this).get(EventCreate_ViewModel.class);

        Toolbar mToolbar = findViewById(R.id.toolbar_event_create);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener((View v) -> onBackPressed());

        if(getIntent().getExtras()!=null){
            mUserModel = getIntent().getExtras().getParcelable("user");
            mUserID = mUserModel.getEmail();
        }

        ViewPager mViewPager = findViewById(R.id.viewpager_eventcreate);
        EventCreateAdapter mAdapter = new EventCreateAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        TabLayout mTabs = findViewById(R.id.tablayout_eventcreate);
        mTabs.setupWithViewPager(mViewPager);

        //Observes the Details fragment to determine if the there is a non-empty string for the name
        mViewModel.getEventName().observe(this, (String s) ->{
            mEventName = s;
            Log.w("EventCreateAct","the event name = "+mEventName);
            checkFields();
        });

        mViewModel.getEventDesc().observe(this, (String s) ->{
            mEventDesc = s;
            checkFields();
        });
    }

    boolean mCreateOptionFlag = false;
    private void checkFields(){
        if(mEventName!=null && mEventDesc!=null){
            if(mEventName.trim().length()>0 && mEventDesc.trim().length()>0){
                mCreateOptionFlag=true;
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
    public void onBackPressed() {
        YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
        yesNoDialog.setCancelable(false);
        yesNoDialog.show(getSupportFragmentManager(),"YESNO");
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
    }

    /**
     * Callback interface when the positive button in the CardView for the LocationsMap fragment is
     * clicked. In the case of the fragment attaching to this activity, we bind
     * @param location the address where the event is being hosted.
     */
    @Override
    public void onCardViewPositiveClick(Location_Model location) {
        Toast.makeText(this,"Location has been set for the event.",Toast.LENGTH_LONG).show();
        mViewModel.setEventLat(location.getLat());
        mViewModel.setEventLong(location.getLng());
        mViewModel.setEventAddress(location.getAddress());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_event_create){
            if(mCreateOptionFlag){
                final Event_Model event = mViewModel.getEventModel();
                if(event.getLong1() == Calendar.getInstance().getTimeInMillis()){
                    Log.w("EventCreateAct: ","Do you want to set a time for the event?");
                }

                event.setCreator(mUserID);
                event.setPrimes(mViewModel.getEventPrimes());
                event.setInvited(mViewModel.getInviteList().size());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("event",event);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

                mViewModel.createEvent(event).addOnCompleteListener((Task<Void> task)->
                        mViewModel.addUserToEvent(mUserID,mUserModel,event.getName()));
                mViewModel.addEventToUser(mUserID,event);
                mViewModel.sendEventInvites(event, mViewModel.getInviteList());
            } else {
                Toast.makeText(this,"Mandatory event fields must be completed.",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the user confirms that they want to exit the event create activity.
     */
    @Override
    public void onPositiveClick() {
        finish();
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
                    return Locations_MapFragment.newInstance(mUserID, Locations_MapFragment.EVENT_ACTIVITY);
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
