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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.EventCreate.EventCreate_Activity;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Events.EventsCore_Fragment;
import apps.raymond.kinect.Groups.GroupCreate_Fragment;
import apps.raymond.kinect.Groups.Group_Model;
import apps.raymond.kinect.Groups.GroupsCore_Fragment;
import apps.raymond.kinect.Interfaces.BackPressListener;
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
 */
public class Core_Activity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        SearchView.OnQueryTextListener, GroupCreate_Fragment.AddGroup{

    private static final String TAG = "Core_Activity";
    private static final String INV_FRAG = "ViewInvitations_Fragment";
    private static final String CREATE_GROUP_FRAG = "CreateGroup";
    public static final String INVITE_USERS_FRAG = "InviteUsersFrag";
    public static final int YESNO_REQUEST = 21;
    public static final int EVENTCREATE = 22;

    public UpdateGroupRecycler updateGroupRecycler;
    public interface UpdateGroupRecycler{
        void updateGroupRecycler(Group_Model groupBase);
    }

    ViewPager viewPager;
    SearchView toolbarSearch;
    Toolbar toolbar;
    private Core_Adapter pagerAdapter;
    private User_Model mUserModel;
    private String mUserID;
    private Core_ViewModel mViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        mViewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);
        if(getIntent().hasExtra("user")){
            mUserModel = getIntent().getExtras().getParcelable("user");
            mUserID = mUserModel.getEmail();
            mViewModel.setUserDocument(mUserModel);
        } else if(getIntent().hasExtra("userID")){
            mUserID = getIntent().getExtras().getString("userID");
            Log.w(TAG,"Starting core activity with mUserID: "+ mUserID);
            Log.w(TAG,"Have to fetch the UserModel document from database.");
            mViewModel.loadUserDocument(mUserID);
        }

        toolbar = findViewById(R.id.core_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setBackgroundColor(getColor(R.color.colorAccentLight));
        toolbar.setNavigationOnClickListener((View v)-> {
            if(mUserModel !=null){
                Intent profileIntent = new Intent(this, Profile_Activity.class);
                profileIntent.putExtra("user", mUserModel);
                startActivity(profileIntent);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
            }
        });

        toolbarSearch = findViewById(R.id.toolbar_search);
        toolbarSearch.setOnQueryTextListener(this);

        viewPager = findViewById(R.id.core_ViewPager);
        pagerAdapter = new Core_Adapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        mViewModel.getUserModel().observe(this, (User_Model user_model)->{
            if(user_model==null){
                //Should never occur but may have to implement a safety.
            } else {
                mUserModel = user_model;
                mUserID = mUserModel.getEmail();
                mViewModel.loadUserInvitations(mUserID);
            }
        });
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
               eventCreate = menu.findItem(R.id.action_create_event_launch);
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
                ViewInvitations_Fragment fragment = new ViewInvitations_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,fragment,INV_FRAG)
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
            case R.id.action_create_group:
                GroupCreate_Fragment groupFragment = GroupCreate_Fragment.newInstance(mUserModel);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,groupFragment,CREATE_GROUP_FRAG)
                        .addToBackStack(CREATE_GROUP_FRAG)
                        .commit();
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
    public void addToGroupRecycler(Group_Model groupBase) {
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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        int i = viewPager.getCurrentItem();
        Fragment fragment = pagerAdapter.getFragment(i);
        switch (i) {
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

    /**
     * Adapter class that is designated to create Fragments for a ViewPager.
     */
    public class Core_Adapter extends FragmentStatePagerAdapter {
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
            return 2;
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
}
