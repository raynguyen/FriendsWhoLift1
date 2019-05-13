/*
 * ToDo:
 * 1. Get the user permission for camera and document access on start up and store as a SharedPreference.
 * 3. Move all the currentUser stuff to repository return methods.
 * 4. Allow user to remove/delete events and or Groups if they are owner for deletes.
 * 6. Recycler View for events does not properly display information.
 * 8. When inflating detail and clicking edit, the edit text should be filled with the current data instead of blank.
 * 10.Redefine the layout for the edit group/event
 *
 * APP LAUNCH
 * -On first launch, fetch the User object, associated events and groups, and connections?
 * -Connections: Only needed when user clicks on their profile to view their friends.
 * --Will have to query connections events to see if user is attending any open events that they are
 * --attending.
 *
 * INVITING USERS TO EVENT
 * -When creating an event, you should be only allowed in invite users you have connected with
 * (potentially for users who have connections with users you connected with) otherwise there may
 * be too much spam invites to events.
 * -Each user has control whether they can be invited to events. Open to suggestions via locations
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
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.EventControl_Fragment;
import apps.raymond.kinect.Events.EventCreate_Fragment;
import apps.raymond.kinect.Events.EventsCore_Fragment;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Events.EventExplore_Fragment;
import apps.raymond.kinect.Groups.Group_Model;
import apps.raymond.kinect.Groups.GroupsCore_Fragment;
import apps.raymond.kinect.Groups.Group_Create_Fragment;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.UserProfile.User_Model;

public class Core_Activity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, SearchView.OnQueryTextListener,
        Group_Create_Fragment.AddGroup, EventsCore_Fragment.EventCore_Interface,
        EventControl_Fragment.EventControlInterface {

    private static final String TAG = "Core_Activity";
    private static final int NUM_PAGES = 2;
    private static final String INV_FRAG = "ViewInvitations_Fragment";
    private static final String CREATE_EVENT_FRAG = "CreateEvent";
    private static final String CREATE_GROUP_FRAG = "CreateGroup";
    private static final String SEARCH_EVENTS_FRAG = "EventCore_Interface";
    public static final String INVITE_USERS_FRAG = "InviteUsersFrag";
    public static final int YESNO_REQUEST = 21;

    public UpdateGroupRecycler updateGroupRecycler;
    public interface UpdateGroupRecycler{
        void updateGroupRecycler(Group_Model groupBase);
    }

    Activity thisInstance;
    ViewPager viewPager;
    Core_ViewModel mViewModel;
    SearchView toolbarSearch;
    Core_Adapter pagerAdapter;
    Toolbar toolbar;
    Bundle instanceBundle;
    private User_Model mCurrUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_activity);
        if(savedInstanceState!=null){
            instanceBundle = savedInstanceState;
        }
        thisInstance = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mViewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);
        mViewModel.getCurrentUser().addOnCompleteListener(new OnCompleteListener<User_Model>() {
            @Override
            public void onComplete(@NonNull Task<User_Model> task) {
                if(task.isSuccessful()){
                    mCurrUser = task.getResult();
                    Toast.makeText(getBaseContext(),"Welcome "+ mCurrUser.getEmail(),Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG,"Unable to fetch user doc to POJO");
                }
            }
        });

        toolbar = findViewById(R.id.core_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarSearch = findViewById(R.id.toolbar_search);
        toolbarSearch.setOnQueryTextListener(this);

        viewPager = findViewById(R.id.core_ViewPager);
        pagerAdapter = new Core_Adapter(getSupportFragmentManager());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);

        observeInvitations();
        toolbarListener();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        //ToDo: The fragment should not implement activity method. Check what was changed for the events and do the same for Group.
        if(fragment instanceof GroupsCore_Fragment){
            try {
                updateGroupRecycler = (UpdateGroupRecycler) fragment;
            } catch (ClassCastException e){
                Log.i(TAG,"GroupsCore_Fragment does not implement UpdateGroupRecycler interface.");
            }
        }
    }

    MenuItem eventCreate, groupCreate;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
       if(getSupportFragmentManager().getBackStackEntryCount()>0){
            return false;
        } else {
           if(menu.size()==0){
               getMenuInflater().inflate(R.menu.core_menu,menu);
               toolbar.setBackgroundColor(getColor(R.color.colorAccentLight));
               toolbar.setNavigationOnClickListener(this);
               eventCreate = menu.findItem(R.id.action_create_event);
               groupCreate = menu.findItem(R.id.action_create_group);
               return true;
           }
           return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_invitations:
                Log.i(TAG,"Clicked on invites button");
                ViewInvitations_Fragment fragment =
                        ViewInvitations_Fragment.newInstance(mEventInvitations,mGroupInvitations);

                //ViewInvitations_Fragment inviteDialog = new ViewInvitations_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,fragment,INV_FRAG)
                        .addToBackStack(INV_FRAG)
                        .commit();
                return true;
            case R.id.action_create_event:
                EventCreate_Fragment eventFragment = new EventCreate_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,eventFragment,CREATE_EVENT_FRAG)
                        .addToBackStack(CREATE_EVENT_FRAG)
                        .commit();
                return true;
            case R.id.action_create_group:
                Group_Create_Fragment groupFragment = new Group_Create_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,groupFragment,CREATE_GROUP_FRAG)
                        .addToBackStack(CREATE_GROUP_FRAG)
                        .commit();
                return true;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i){
            case 0:
                eventCreate.setVisible(true);
                eventCreate.setEnabled(true);
                groupCreate.setVisible(false);
                groupCreate.setEnabled(false);
                break;
            case 1:
                eventCreate.setVisible(false);
                eventCreate.setEnabled(false);
                groupCreate.setVisible(true);
                groupCreate.setEnabled(true);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(v.getId()== -1){
            Intent profileIntent = new Intent(this,Profile_Activity.class);
            startActivity(profileIntent);
            overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        int i = viewPager.getCurrentItem();
        Fragment fragment = pagerAdapter.getFragment(i);
        switch (i){
            case 0:
                ((EventsCore_Fragment) fragment).filterRecycler(s);
                break;
            case 1:
                ((GroupsCore_Fragment) fragment).filterRecycler(s);
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void addToGroupRecycler(Group_Model groupBase) {
        Log.i(TAG,"Called addToGroupRecycler implementation in Core Activity");
        updateGroupRecycler.updateGroupRecycler(groupBase);
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!toolbarSearch.isIconified()){
            toolbarSearch.setIconified(true);
        }

        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof EditText){
                v.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(),0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    //This is called to update the Core Recycler views when the user accepts an invitation.
    /*@Override
    public void eventAccepted(Event_Model event) {
        Log.i(TAG,"Accepted invite to: "+event.getName());
        EventsCore_Fragment fragment = (EventsCore_Fragment) pagerAdapter.getFragment(Core_Adapter.EVENTS_FRAGMENT);
        fragment.notifyNewEvent(event);
    }

    @Override
    public void eventDeclined(Event_Model event) {
        Log.i(TAG,"Declined invite to: "+event.getName());
    }*/

    @Override
    public void exploreEvents() {
        EventExplore_Fragment searchFragment = EventExplore_Fragment.newInstance(mCurrUser);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.full_core_frame,searchFragment,SEARCH_EVENTS_FRAG)
                .addToBackStack(SEARCH_EVENTS_FRAG)
                .commit();
    }

    @Override
    public void startDetailActivity(Event_Model event) {
        EventDetail_Activity.init(event, mCurrUser, this);
    }

    public class Core_Adapter extends FragmentStatePagerAdapter {
        private static final int EVENTS_FRAGMENT = 0;

        private List<Fragment> fragments;
        private Core_Adapter(FragmentManager fm) {
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
                    return new EventsCore_Fragment();
                case 1:
                    return new GroupsCore_Fragment();
                default:
                    return null;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Events";
                case 1:
                    return "Groups";
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            fragments.remove(position);
            super.destroyItem(container, position, object);
        }

        private Fragment getFragment(int position) {
            return fragments.get(position);
        }
    }

    /**
     * Interface method whenever a user opts to attending an event. Process flow:
     * 1.   The Event is added to the User's event collection.
     * 2a.  Add the user to the Event's attending collection.
     * 2b.  If flag == 0, remove the invitation from the User's and Event's Invitation collections.
     * 3.   Call updateEventRecycler to update the appropriate views.
     *
     * @param event The event of which the user opted to attend.
     * @param flag Indication flag to determine whether the user was invited to the event or if the
     *             user opted to attend the event via exploration.
     *
     * ToDo: Check both the ExploreEvent and EventInvitation to see if we update the Attending and Invited counts appropriately.
     */
    @Override
    public void onAttendEvent(Event_Model event, int flag) {
        final String eventName = event.getOriginalName();
        //ToDo:
        //The counts are not correct for the attending and invited count, we are pulling an Event POJO
        //before the writes to the database are completed. Considering doin an onComplete to the increment/decrement calls.
        mViewModel.incrementEventAttending(eventName);
        if(flag==EventControl_Fragment.INVITATION){
            //Todo: If the user attends an event via the explore map but they are also invited to the
            //event, we need to determine a method to also remove the invitation from the user collection
            //as well as the invited collection from the event.
            mViewModel.decrementEventInvited(eventName);
            mViewModel.removeEventInvitation(eventName);
        }

        mViewModel.addEventToUser(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Attending "+eventName,Toast.LENGTH_LONG).show();
            }
        });
        mViewModel.addUserToEvent(eventName);

        updateEventRecycler(event);
    }

    /**
     * Interface method that is called when the current user opts to attending a new event. The
     * event is trickled down stream to the presentation fragment in order to update necessary view.
     *
     * Future: Consider observing the User's Event collection and trigger UI updates on changes.
     *
     * @param event
     */
    @Override
    public void updateEventRecycler(Event_Model event) {
        EventsCore_Fragment fragment =
                (EventsCore_Fragment) pagerAdapter.getFragment(Core_Adapter.EVENTS_FRAGMENT);
        fragment.notifyNewEvent(event);
    }

    @Override
    public void onDeclineEvent(Event_Model event) {
    }

    List<Event_Model> mEventInvitations;
    List<Group_Model> mGroupInvitations;
    /**
     * Method call that establishes the CoreActivity as an observer to any changes in the Invitation
     * LiveData held by the ViewModel.
     */
    private void observeInvitations(){
        mViewModel.getEventInvitations().observe(this, new Observer<List<Event_Model>>() {
            @Override
            public void onChanged(@Nullable List<Event_Model> event_models) {
                Log.w(TAG,"There was a change in the event invitations!");
                mEventInvitations = event_models;
            }
        });
        mViewModel.getGroupInvitations().observe(this, new Observer<List<Group_Model>>() {
            @Override
            public void onChanged(@Nullable List<Group_Model> group_models) {
                mGroupInvitations = group_models;
            }
        });
    }

    private void toolbarListener(){
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int i = getSupportFragmentManager().getBackStackEntryCount();
                if(i > 0){
                    toolbar.setNavigationIcon(R.drawable.baseline_keyboard_arrow_left_black_18dp);
                    String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(i-1).getName();
                    final Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
                    if(fragment instanceof Group_Create_Fragment || fragment instanceof EventCreate_Fragment){
                        //More efficient to create an onclicklistener once and reuse the same isntead of calling new everytime backstack is changed.
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((BackPressListener) fragment).onBackPress();
                            }
                        });
                    } else {
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                                Log.w(TAG,"Back stack includes: " +getSupportFragmentManager().getFragments().toString());
                            }
                        });
                    }
                } else {
                    toolbar.setNavigationIcon(R.drawable.baseline_face_black_18dp);
                    toolbar.setNavigationOnClickListener((Core_Activity) thisInstance);
                }
            }
        });
    }
}
