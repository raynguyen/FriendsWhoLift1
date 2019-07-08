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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import apps.raymond.kinect.CoreFragments.Create_Fragment;
import apps.raymond.kinect.CoreFragments.Events_Fragment;
import apps.raymond.kinect.CoreFragments.Explore_Fragment;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Invitations.PersonalMessages_Fragment;
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
public class Core_Activity extends AppCompatActivity{
    private static final String TAG = "Core_Activity";
    public static final String USER = "user";
    public static final String USER_ID = "userID";
    private static final String INV_FRAG = "EventInvitations_Fragment";
    public static final int EVENTCREATE = 22;
    private Core_ViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        mViewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);

        /*
        Setup the Core_ViewModel with the data retrieved during application launch. We can either
        start this activity with a User_Model (calling activity was Login_Activity) or this instance
        could have been started from a previously logged in user which gives us the userID.
         */
        if(getIntent().hasExtra(USER)){
            User_Model userModel = getIntent().getExtras().getParcelable("user");
            mViewModel.setUserDocument(userModel);
        } else if(getIntent().hasExtra(USER_ID)){
            String userID = getIntent().getExtras().getString("userID");
            mViewModel.setUserID(userID);
            mViewModel.loadUserDocument(userID);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_core);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getColor(R.color.colorAccent));
        toolbar.setNavigationOnClickListener((View v)-> {
            User_Model userModel = mViewModel.getUserModel().getValue();
            if(userModel !=null){
                Intent profileIntent = new Intent(this, Profile_Activity.class);
                profileIntent.putExtra(Profile_Activity.CURRENT_USER, userModel);
                startActivity(profileIntent);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
            }
        });



        final Create_Fragment createFrag = new Create_Fragment();
        final Explore_Fragment exploreFrag = new Explore_Fragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_core_container, createFrag, "create").hide(createFrag).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_core_container, exploreFrag, "explore").hide(exploreFrag).commit();
        final Events_Fragment eventsFrag = new Events_Fragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_core_container,eventsFrag,"events").commit();
        Fragment activeFrag = eventsFrag;




        mViewModel.getUserModel().observe(this,(@Nullable User_Model user_model)-> {
            if(user_model!=null){
                String userID = user_model.getEmail();
                mViewModel.loadEventInvitations(userID);
                mViewModel.loadConnectionRequests(userID);
                mViewModel.loadPublicEvents();
                mViewModel.loadUserConnections(userID);
            } else {
                //Todo: add safety prevention?
            }
        });

        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        navView.setOnNavigationItemSelectedListener((@NonNull MenuItem menuItem) -> {
            switch (menuItem.getItemId()){
                case R.id.nav_events:
                    return true;
                case R.id.nav_explore:
                    return true;
                case R.id.nav_create:
                    return true;
            }
            return false;
        });

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
                PersonalMessages_Fragment fragment = new PersonalMessages_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_core_container,fragment,INV_FRAG)
                        .addToBackStack(INV_FRAG)
                        .commit();
                return true;
            case R.id.action_create_event_launch:
                /*ArrayList<Event_Model> mEventList = new ArrayList<>();
                Intent eventCreateIntent = new Intent(this, EventCreate_Activity.class);
                eventCreateIntent.putExtra("user",mUserModel);
                if(mViewModel.getAcceptedEvents().getValue()!=null){
                    mEventList.addAll(mViewModel.getAcceptedEvents().getValue());
                    eventCreateIntent.putExtra("events",mEventList);
                }
                startActivityForResult(eventCreateIntent,EVENTCREATE);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);*/
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

}