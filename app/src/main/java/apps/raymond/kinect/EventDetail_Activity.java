package apps.raymond.kinect;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
        ProfileRecyclerAdapter.ProfileClickListener, EventMessages_Fragment.MessagesFragment_Interface{
    public static final String EVENT = "Event";
    public static final String USER = "User";
    private static final int NUM_PAGES = 2;

    public static void init(Event_Model event,User_Model user, Context context){
        Intent intent = new Intent(context, EventDetail_Activity.class);
        intent.putExtra(EVENT, event);
        intent.putExtra(USER, user);
        context.startActivity(intent);
    }

    User_Model currUser;
    Core_ViewModel viewModel;
    Event_Model event;
    Toolbar toolbar;
    ViewGroup informationLayout, usersLayout;
    ViewFlipper profilesFlipper;
    TextView textName, textHost, textDesc, textMonth, textDate, textTime;
    ViewPager mViewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_);
        Intent initIntent = getIntent();
        event = initIntent.getParcelableExtra(EVENT);
        currUser = initIntent.getParcelableExtra(USER);

        toolbar = findViewById(R.id.toolbar_event);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(this);

        //ToDo: Consider refactoring this back to a fragment.
        viewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);

        informationLayout = findViewById(R.id.layout_information);
        textName = findViewById(R.id.text_name);
        textName.setText(event.getName());
        textHost = findViewById(R.id.text_host);
        String hostString = getString(R.string.host) + " " + event.getCreator();
        textHost.setText(hostString);
        textDesc = findViewById(R.id.text_description);
        textDesc.setText(event.getDesc());

        textMonth = findViewById(R.id.text_month);
        textDate = findViewById(R.id.text_date);
        textTime = findViewById(R.id.text_time);
        convertLongToDate();

        mViewPager = findViewById(R.id.viewpager_messages);
        DetailPagerAdapter adapter = new DetailPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);


        usersLayout = findViewById(R.id.layout_users);
        Button btnViewUsers = findViewById(R.id.button_view_users);
        btnViewUsers.setOnClickListener(this);

        profilesFlipper = findViewById(R.id.viewflipper_profiles);
    }

    /**
     * Converts event long field to a date field and populates the appropriate views.
     */
    private void convertLongToDate(){
        long long1 = event.getLong1();
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
            case R.id.button_view_users:
                if(mViewPager.getCurrentItem()==0){
                    mViewPager.setCurrentItem(1);
                } else if(mViewPager.getCurrentItem()==1){
                    mViewPager.setCurrentItem(0);
                }
                break;
        }
    }

    @Override
    public User_Model getCurrentUser() {
        return currUser;
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
    public void onProfileClick(User_Model userModel) {
        Intent viewProfileIntent = new Intent(this, ViewProfile_Activity.class);
        viewProfileIntent.putExtra(ViewProfile_Activity.USER,userModel);
        startActivity(viewProfileIntent);
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
                                Toast.makeText(getBaseContext(),"Leaving event",Toast.LENGTH_LONG).show();
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
                    return EventMessages_Fragment.newInstance(event);
                case 1:
                    return EventUsers_Fragment.newInstance(event);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
