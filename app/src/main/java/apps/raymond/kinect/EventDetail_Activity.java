package apps.raymond.kinect;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventDetail_Activity extends AppCompatActivity implements View.OnClickListener,
        EventMessages_Fragment.MessagesFragment_Interface{

    User_Model mUserModel;
    Core_ViewModel mViewModel;
    Event_Model mEventModel;
    Toolbar toolbar;
    ViewGroup informationLayout, usersLayout;
    ViewFlipper profilesFlipper;
    TextView textName, textHost, textDesc, textMonth, textDate, textTime;
    ViewPager mViewPager;
    Button btnFlipPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_);

        mEventModel = getIntent().getExtras().getParcelable("event");
        mUserModel = getIntent().getExtras().getParcelable("user");

        toolbar = findViewById(R.id.toolbar_event);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(this);

        //ToDo: Consider refactoring this back to a fragment.
        mViewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);

        informationLayout = findViewById(R.id.layout_information);
        textName = findViewById(R.id.text_name);
        textName.setText(mEventModel.getName());
        textHost = findViewById(R.id.text_host);
        String hostString = getString(R.string.host) + " " + mEventModel.getCreator();
        textHost.setText(hostString);
        textDesc = findViewById(R.id.text_description);
        textDesc.setText(mEventModel.getDesc());

        textMonth = findViewById(R.id.text_month);
        textDate = findViewById(R.id.text_date);
        textTime = findViewById(R.id.text_time);
        convertLongToDate();

        mViewPager = findViewById(R.id.viewpager_messages);
        DetailPagerAdapter adapter = new DetailPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);


        usersLayout = findViewById(R.id.layout_users);
        btnFlipPager = findViewById(R.id.button_flippager);
        btnFlipPager.setOnClickListener(this);

        profilesFlipper = findViewById(R.id.viewflipper_profiles);
    }

    /**
     * Converts mEventModel long field to a date field and populates the appropriate views.
     */
    private void convertLongToDate(){
        long long1 = mEventModel.getLong1();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(long1);
        textMonth.setText(new SimpleDateFormat("MMM", Locale.getDefault()).format(c.getTime()));
        textDate.setText(String.valueOf(c.get(Calendar.DATE)));
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a",Locale.getDefault());
        textTime.setText(sdf.format(new Date(long1)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case -1:
                onBackPressed();
                break;
            case R.id.button_accepted_users:
                profilesFlipper.setDisplayedChild(0);
                break;
            case R.id.button_declined_users:
                profilesFlipper.setDisplayedChild(1);
                break;
            case R.id.button_invited_users:
                profilesFlipper.setDisplayedChild(2);
                break;
            case R.id.button_flippager:
                if(mViewPager.getCurrentItem()==0){
                    btnFlipPager.setText(R.string.view_event_users);
                    mViewPager.setCurrentItem(1);
                } else if(mViewPager.getCurrentItem()==1){
                    btnFlipPager.setText(R.string.view_event_messages);
                    mViewPager.setCurrentItem(0);
                }
                break;
        }
    }

    @Override
    public User_Model getCurrentUser() {
        return mUserModel;
    }

    //ToDo:Need to test this on a device, it is too difficult to test via emulator.
    private int scrollDist = 0;
    boolean isVisible = true;
    @Override
    public void onMessagesScrolled(View view, int dy) {
        if(isVisible && scrollDist > 25){
            informationLayout.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            isVisible = false;
            scrollDist = 0;
        } else if(!isVisible && scrollDist <-25){
            informationLayout.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            isVisible = true;
            scrollDist = 0;
        }

        if((isVisible && dy > 0)||(!isVisible && dy < 0)){
            scrollDist += dy;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(menu.size()==0){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.event_detail_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.option_leave_event:
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetail_Activity.this);
                builder.setTitle("Warning!")
                        .setMessage(R.string.leave_event_message)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(),"Leaving mEventModel",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(),"Cancel",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.create().show();
                return true;
            default:
                return false;

        }
    }

    //TODO: WHEN CREATING THE FRAGMENTS, CONSIDER RETRIEVING THE DATA IN THE ACTIVITY AND PASSING TO THE FRAGMENTS.
    private class DetailPagerAdapter extends FragmentStatePagerAdapter{

        private List<Fragment> fragments;
        private DetailPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragments.add(position, fragment);
            return fragment;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return EventMessages_Fragment.newInstance(mEventModel);
                case 1:
                    return EventUsers_Fragment.newInstance(mEventModel,mUserModel);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
