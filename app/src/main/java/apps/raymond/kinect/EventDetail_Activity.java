package apps.raymond.kinect;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventDetail_Activity extends AppCompatActivity implements View.OnClickListener,
        ProfileRecyclerAdapter.ProfileClickListener{
    private static final String TAG = "EventDetail";
    public static final String EVENT = "Event";
    private static final String EVENT_ACCEPTED = "Accepted";
    private static final String EVENT_DECLINED = "Declined";
    private static final int NUM_PAGES = 1;

    public static void init(Event_Model event, Context context){
        Intent intent = new Intent(context, EventDetail_Activity.class);
        intent.putExtra(EVENT, event);
        context.startActivity(intent);
    }

    Repository_ViewModel viewModel;
    Event_Model event;
    ViewGroup informationLayout, usersLayout;
    ViewFlipper profilesFlipper;
    List<User_Model> invitedProfiles,declinedProfiles,acceptedProfiles;
    ProfileRecyclerAdapter invitedAdapter, declinedAdapter, acceptedAdapter;
    ProgressBar updateBar;
    TextView textName, textHost, textDesc, textMonth, textDate, textTime;
    ViewPager mViewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_);
        Intent initIntent = getIntent();
        event = initIntent.getParcelableExtra(EVENT);

        viewModel = ViewModelProviders.of(this).get(Repository_ViewModel.class);

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

        updateBar = findViewById(R.id.update_progress_bar);

        profilesFlipper = findViewById(R.id.profiles_flipper);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
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
                    //mViewPager.setCurrentItem(0);
                }
                break;
        }
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
    public void onProfileClick(User_Model userModel) {
        Intent viewProfileIntent = new Intent(this, View_Profile_Activity.class);
        viewProfileIntent.putExtra(View_Profile_Activity.USER,userModel);
        startActivity(viewProfileIntent);
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
                    return new Messages_Fragment();
                case 1:
                    //return new EventUsers_Fragment();
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
