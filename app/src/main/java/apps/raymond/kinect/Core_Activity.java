/*
 * * Core_Activity Controls:
 * 1. Observe the User_Model held by the Core_ViewModel. On changes, we need to finish all processes
 *  that is a child of the Core_Activity or parallel in the application hierarchy (i.e. EventDetail,
 *  Profile, and Map activities).
 * 2. Handle intents to start activities as requested by it's child fragments.
 * 3.
 *
 *
 * ToDo:
 * 1. Get the mUserModel permission for camera and document access on start up and store as a SharedPreference.
 * 3. Move all the currentUser stuff to repository return methods.
 * 4. Allow mUserModel to remove/delete events and or Groups if they are owner for deletes.
 * 6. Recycler View for events does not properly display information.
 * 8. When inflating detail and clicking edit, the edit text should be filled with the current data instead of blank.
 * 10.Redefine the layout for the edit group/mEventModel
 *
 * APP LAUNCH
 * -On first launch, fetch the User object, associated events and groups, and connections?
 * -Connections: Only needed when mUserModel clicks on their profile to view their friends.
 * --Will have to query connections events to see if mUserModel is attending any open events that they are
 * --attending.
 *
 * INVITING USERS TO EVENT
 * -When creating an mEventModel, you should be only allowed in invite users you have connected with
 * (potentially for users who have connections with users you connected with) otherwise there may
 * be too much spam invites to events.
 * -Each mUserModel has control whether they can be invited to events. Open to suggestions via locations
 * of previous events.
 *
 * MESSAGING FOR INVITES:
 * Doc listener for a invites doc.
 *
 * OPTIMIZE:
 * If a fragment is committed and has setHasOptionsMenu(true), it will call the parent activity's
 * onCreateOptionsMenu. This means that to optimize computing, only mandatory calls should be executed
 * inside the activity's onCreateOptionsMenu method.
 *
 * SIDE NOTE:
 * A fragment should always be self contained such that it should never depend on other fragments. By
 * default, a fragment will always hold a reference to its host activity.
 */

package apps.raymond.kinect;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.EventCreate.EventCreate_Activity;
import apps.raymond.kinect.EventDetail.EventDetail_Activity;
import apps.raymond.kinect.Events.EventExplore_Fragment;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Events.EventsCore_Adapter;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.Invitations.EventInvitations_Fragment;
import apps.raymond.kinect.Invitations.PersonalMessages_Fragment;
import apps.raymond.kinect.UIResources.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.UserProfile.Profile_Activity;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

/**
 * Todo:
 *  Test to see invitation accept does the following:
 *  1a. Add UserModel to the Event's Accepted collection.
 *  1b.Event is added to User's Event collection.
 *  2. Increments accepted count.
 *  3. Remove Event invitation from User's EventInvitations collection.
 *  4. Decrements invited count.
 *  5. Make sure that the Event Invitation RecyclerView and set are updated.
 *
 * Todo:
 *  Test to see if Accepting a public mEventModel from explore does the following:
 *  1a. See above.
 *  1b. See above.
 *  2. See above.
 *  3. Properly update the Events RecyclerView in the Core Events Fragment.
 *
 *
 * ToDo:
 *  Reconfigure home screen. NEWS FEED TYPE FEATURE
 *  1.Searchbar to filter upcoming events.
 *  2.Upcoming events that you may be interested from TAGS or PAST LOCATIONS.
 *  3.Upcoming events that your friends are interested/attending.
 *
 */
public class Core_Activity extends AppCompatActivity implements
        SearchView.OnQueryTextListener, EventsCore_Adapter.EventClickListener{

    private static final String TAG = "Core_Activity";
    private static final String INV_FRAG = "EventInvitations_Fragment";
    public static final int EVENTCREATE = 22;

    ViewPager viewPager;
    private User_Model mUserModel;
    private String mUserID;
    private Core_ViewModel mViewModel;
    private EventsCore_Adapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        mViewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);

        ProgressBar progressBar = findViewById(R.id.progress_connections);
        TextView textNullEvents = findViewById(R.id.text_null_connections);
        TextView textNumEvents = findViewById(R.id.text_events_count);

        final RecyclerView eventsRecycler = findViewById(R.id.events_Recycler);
        mAdapter = new EventsCore_Adapter(this);
        eventsRecycler.setAdapter(mAdapter);
        eventsRecycler.addItemDecoration(new Margin_Decoration_RecyclerView());
        eventsRecycler.setLayoutManager(new LinearLayoutManager(this));

        mViewModel.getAcceptedEvents().observe(this, (List<Event_Model> events)-> {
            if(events!=null){
                textNumEvents.setText(String.valueOf(events.size()));
                progressBar.setVisibility(View.GONE);
                if(!events.isEmpty()){
                    if(textNullEvents.getVisibility()==View.VISIBLE){
                        textNullEvents.setVisibility(View.GONE);
                    }
                    mAdapter.setData(events);
                } else {
                    textNullEvents.setVisibility(View.VISIBLE);
                }
            } else {
                textNumEvents.setText("??");
            }
        });

        mViewModel.getUserModel().observe(this,(@Nullable User_Model user_model)-> {
            if(user_model!=null){
                mUserModel = user_model;
                mUserID = mUserModel.getEmail();
                mViewModel.loadAcceptedEvents(mUserID);
                mViewModel.loadEventInvitations(mUserID);
                mViewModel.loadPublicEvents();
                mViewModel.loadUserConnections(mUserID);
            } else {
                //Todo: add safety prevention?
            }
        });

        if(getIntent().hasExtra("user")){
            //User signed in from login and we have retrieved document.
            mUserModel = getIntent().getExtras().getParcelable("user");
            mViewModel.setUserDocument(mUserModel);
        } else if(getIntent().hasExtra("userID")){
            //Called if previous user is detected. We must then fetch the user document from DB.
            mUserID = getIntent().getExtras().getString("userID");
            mViewModel.loadUserDocument(mUserID);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_core);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getColor(R.color.colorAccent));
        toolbar.setNavigationOnClickListener((View v)-> {
            if(mUserModel !=null){
                Intent profileIntent = new Intent(this, Profile_Activity.class);
                profileIntent.putExtra("current_user", mUserModel);
                startActivity(profileIntent);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
            }
        });

        SearchView searchView = findViewById(R.id.searchview_events);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //START FILTERING THE EVENTS ADAPTER FOR THE CONTENT.
                mAdapter.getFilter().filter(s);
                return false;
            }
        });


        FloatingActionButton btnExploreEvents = findViewById(R.id.button_explore_events);
        btnExploreEvents.setOnClickListener((View v)->{
            EventExplore_Fragment searchFragment = EventExplore_Fragment.newInstance(mUserModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_core_full,searchFragment,"exploreevents")
                    .addToBackStack("exploreevents")
                    .commit();
        });
    }

    @Override
    public void onEventClick(Event_Model event) {
        Intent detailActivity = new Intent(this, EventDetail_Activity.class);
        detailActivity.putExtra("user",mUserModel).putExtra("event_name",event.getName());
        startActivity(detailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.core_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_invitations:
                /*EventInvitations_Fragment fragment = new EventInvitations_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_core_full,fragment,INV_FRAG)
                        .addToBackStack(INV_FRAG)
                        .commit();*/

                PersonalMessages_Fragment fragment = new PersonalMessages_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_core_full,fragment,INV_FRAG)
                        .addToBackStack(INV_FRAG)
                        .commit();
                return true;
            case R.id.action_create_event_launch:
                ArrayList<Event_Model> mEventList = new ArrayList<>();
                Intent eventCreateIntent = new Intent(this, EventCreate_Activity.class);
                eventCreateIntent.putExtra("user",mUserModel);
                if(mViewModel.getAcceptedEvents().getValue()!=null){
                    mEventList.addAll(mViewModel.getAcceptedEvents().getValue());
                    eventCreateIntent.putExtra("events",mEventList);
                }
                startActivityForResult(eventCreateIntent,EVENTCREATE);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==Activity.RESULT_OK){
            if(requestCode==EVENTCREATE){
                Event_Model event = data.getParcelableExtra("event");
                List<Event_Model> acceptedEvents = mViewModel.getAcceptedEvents().getValue();
                acceptedEvents.add(event);
                mViewModel.setAcceptedEvents(acceptedEvents);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG,"List of fragments: \n " + getSupportFragmentManager().getFragments().toString());
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count==0){
            Log.i(TAG,"There is nothing in the back stack");
            if(viewPager.getCurrentItem() == 0){
                super.onBackPressed();
            } else if (viewPager.getCurrentItem() == 1){
                viewPager.setCurrentItem(0);
            }
        } else {
            String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(count-1).getName();
            Fragment topFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if(topFragment instanceof BackPressListener){
                ((BackPressListener)topFragment).onBackPress();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mAdapter.getFilter().filter(s);
        return true;
    }
}
