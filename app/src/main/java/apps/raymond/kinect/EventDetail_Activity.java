package apps.raymond.kinect;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import apps.raymond.kinect.Groups.Members_Panel_Fragment;
import apps.raymond.kinect.UIResources.VerticalTextView;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class EventDetail_Activity extends AppCompatActivity implements
        EventMessages_Fragment.MessagesFragment_Interface{
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a",Locale.getDefault());

    User_Model mUserModel;
    Core_ViewModel mViewModel;
    Event_Model mEventModel;
    ViewGroup informationLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_);

        mEventModel = getIntent().getExtras().getParcelable("event"); //RETRIEVE THE EVENT, can't rely on detail click
        mUserModel = getIntent().getExtras().getParcelable("user");

        Toolbar toolbar = findViewById(R.id.toolbar_event);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener((View v)->onBackPressed());

        TextView txtEventStart = findViewById(R.id.text_event_start);
        TextView textName = findViewById(R.id.text_name);
        TextView textHost = findViewById(R.id.text_host);
        TextView textDesc = findViewById(R.id.text_description);

        textName.setText(mEventModel.getName());
        String hostString = getString(R.string.host) + " " + mEventModel.getCreator();
        textHost.setText(hostString);
        textDesc.setText(mEventModel.getDesc());

        VerticalTextView vTxtMembers = findViewById(R.id.vtext_members);
        vTxtMembers.setOnClickListener((View v)->{
            Log.w("EventDetailAct: ","Should expand the members tray.");
        });

        if(mEventModel.getLong1()!=0){
            String mEventStart = sdf.format(new Date(mEventModel.getLong1()));
            txtEventStart.setText(mEventStart);
        } else {
            txtEventStart.setText(R.string.date_tbd);
        }

        mViewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);
        informationLayout = findViewById(R.id.layout_information);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
