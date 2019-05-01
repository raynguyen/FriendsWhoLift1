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
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import apps.raymond.kinect.DialogFragments.Event_Invites_Fragment;
import apps.raymond.kinect.DialogFragments.Invite_Messages_Fragment;
import apps.raymond.kinect.Events.EventCreate_Fragment;
import apps.raymond.kinect.Events.EventsCore_Fragment;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Events.Events_Search_Fragment;
import apps.raymond.kinect.Groups.Core_Groups_Fragment;
import apps.raymond.kinect.Groups.GroupBase;
import apps.raymond.kinect.Groups.Group_Create_Fragment;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.UserProfile.UserModel;

public class Core_Activity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, SearchView.OnQueryTextListener,
        EventCreate_Fragment.EventCreatedListener, Group_Create_Fragment.AddGroup,
        Event_Invites_Fragment.EventResponseListener, EventsCore_Fragment.SearchEvents {

    private static final String TAG = "Core_Activity";
    private static final String INV_FRAG = "Invite_Messages_Fragment";
    private static final String CREATE_EVENT_FRAG = "CreateEvent";
    private static final String CREATE_GROUP_FRAG = "CreateGroup";
    private static final String SEARCH_EVENTS_FRAG = "SearchEvents";
    public static final String INVITE_USERS_FRAG = "InviteUsersFrag";

    public static final int YESNO_REQUEST = 21;

    public UpdateGroupRecycler updateGroupRecycler;
    public interface UpdateGroupRecycler{
        void updateGroupRecycler(GroupBase groupBase);
    }

    Activity thisInstance;
    ViewPager viewPager;
    Repository_ViewModel viewModel;
    SearchView toolbarSearch;
    Core_Adapter pagerAdapter;
    Toolbar toolbar;
    Bundle instanceBundle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_activity);
        if(savedInstanceState!=null){
            instanceBundle = savedInstanceState;
        }
        thisInstance = this;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        viewModel = ViewModelProviders.of(this).get(Repository_ViewModel.class);

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

        fmListener();
        getUserModel();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        /*if(fragment instanceof EventsCore_Fragment){
            try {
                updateEventRecycler = (UpdateEventRecycler) fragment;
            } catch (ClassCastException e){
                Log.i(TAG,"EventsCore_Fragment does not implement UpdateEventRecycler interface.");
            }
        }*/
        if(fragment instanceof Core_Groups_Fragment){
            try {
                updateGroupRecycler = (UpdateGroupRecycler) fragment;
            } catch (ClassCastException e){
                Log.i(TAG,"Core_Groups_Fragment does not implement UpdateGroupRecycler interface.");
            }
        }
    }

    MenuItem eventCreate, groupCreate;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
       if(getSupportFragmentManager().getBackStackEntryCount()>0){
            return false;
        } else {
            getMenuInflater().inflate(R.menu.core_menu,menu);
            toolbar.setBackgroundColor(getColor(R.color.colorAccentLight));
            toolbar.setNavigationOnClickListener(this);
            eventCreate = menu.findItem(R.id.action_create_event);
            groupCreate = menu.findItem(R.id.action_create_group);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_invites:
                Log.i(TAG,"Clicked on invites button");
                Invite_Messages_Fragment inviteDialog = new Invite_Messages_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,inviteDialog,INV_FRAG)
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
            case R.id.action_edit:
                return false;
            case R.id.action_save:
                return false;
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
        switch(i){
            case -1:
                Intent profileIntent = new Intent(this,Profile_Activity.class);
                startActivity(profileIntent);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
                break;
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
                ((Core_Groups_Fragment) fragment).filterRecycler(s);
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void addToGroupRecycler(GroupBase groupBase) {
        Log.i(TAG,"Called addToGroupRecycler implementation in Core Activity");
        updateGroupRecycler.updateGroupRecycler(groupBase);
    }

    @Override
    public void notifyEventCreated(Event_Model event) {
        EventsCore_Fragment fragment = (EventsCore_Fragment) pagerAdapter.getFragment(Core_Adapter.EVENTS_FRAGMENT);
        fragment.updateEventRecycler(event);
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

    public UserModel userModel;
    private void getUserModel(){
        viewModel.getCurrentUser().addOnCompleteListener(new OnCompleteListener<UserModel>() {
            @Override
            public void onComplete(@NonNull Task<UserModel> task) {
                if(task.isSuccessful()){
                    userModel = task.getResult();
                    Toast.makeText(getBaseContext(),"Welcome "+userModel.getEmail(),Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG,"Unable to fetch user doc to POJO");
                }
            }
        });
    }

    private void fmListener(){
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                //Log.w(TAG,"Back stack includes: " +getSupportFragmentManager().getFragments().toString());
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

    @Override
    public void eventAccepted(Event_Model event) {
        Log.i(TAG,"Accepted invite to: "+event.getName());
        EventsCore_Fragment fragment = (EventsCore_Fragment) pagerAdapter.getFragment(Core_Adapter.EVENTS_FRAGMENT);
        fragment.updateEventRecycler(event);
    }

    @Override
    public void eventDeclined(Event_Model event) {
        Log.i(TAG,"Declined invite to: "+event.getName());
    }

    @Override
    public void searchEvents() {
        Events_Search_Fragment searchFragment = new Events_Search_Fragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.full_core_frame,searchFragment,SEARCH_EVENTS_FRAG)
                .addToBackStack(SEARCH_EVENTS_FRAG)
                .commit();
    }
}
