/*
 * ToDo:
 * 1. Get the user permission for camera and document access on start up and store as a SharedPreference.
 * 3. Move all the currentUser stuff to repository return methods.
 * 4. Allow user to remove/delete events and or Groups if they are owner for deletes.
 * 6. Recycler View for events does not properly display information.
 * 7. DetailFragment for events does not write proper info.
 * 8. When inflating detail and clicking edit, the edit text should be filled with the current data instead of blank.
 * 9. **LIST OF USERS TO INVITE TO EVENTS AND GROUPS!!!!!!!!!!
 * 10.Redefine the layout for the edit group/event
 *
 * MESSAGING FOR INVITES:
 * Doc listener for a invites doc.
 */

package apps.raymond.kinect;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.kinect.DialogFragments.InviteDialog;
import apps.raymond.kinect.Events.Event_Create_Fragment;
import apps.raymond.kinect.Events.GroupEvent;
import apps.raymond.kinect.Groups.Core_Group_Fragment;
import apps.raymond.kinect.Groups.GroupBase;
import apps.raymond.kinect.Groups.Group_Create_Fragment;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.UserProfile.ProfileFrag;

public class Core_Activity extends AppCompatActivity implements
        Group_Create_Fragment.AddGroup, Event_Create_Fragment.AddEvent {
    private static final String TAG = "Core_Activity";
    private static final String INV_FRAG = "InviteFragment";

    public static final int YESNO_REQUEST = 21;
    public UpdateGroupRecycler updateGroupRecycler;

    public interface UpdateGroupRecycler{
        void updateGroupRecycler(GroupBase groupBase);
    }

    public UpdateEventRecycler updateEventRecycler;
    public interface UpdateEventRecycler{
        void updateEventRecycler(GroupEvent groupEvent);
    }

    ViewPager viewPager;
    Repository_ViewModel viewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        viewModel = ViewModelProviders.of(this).get(Repository_ViewModel.class);

        postponeEnterTransition();
        setContentView(R.layout.core_activity);

        Toolbar toolbar = findViewById(R.id.core_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"FUCKHOLE");
            }
        });
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = findViewById(R.id.core_ViewPager);
        Core_Activity_Adapter pagerAdapter = new Core_Activity_Adapter(getSupportFragmentManager());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        viewModel.getEventInvites().observe(this, new Observer<List<GroupEvent>>() {
            @Override
            public void onChanged(@Nullable List<GroupEvent> groupEvents) {
                Log.i(TAG,"LiveData of even invites has changed.");
            }
        });
         */
    }

    @Override
    public void onAttachFragment(Fragment fragment) {

        if(fragment instanceof Core_Group_Fragment){
            try {
                updateGroupRecycler = (UpdateGroupRecycler) fragment;
            } catch (ClassCastException e){
                Log.i(TAG,"Core_Group_Fragment does not implement UpdateGroupRecycler interface.");
            }
            return;
        }

        if(fragment instanceof Core_Events_Fragment){
            try {
                updateEventRecycler = (UpdateEventRecycler) fragment;
            } catch (ClassCastException e){
                Log.i(TAG,"Core_Events_Fragment does not implement UpdateEventRecycler interface.");
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.clear();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.home_actionbar,menu);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.action_edit).setEnabled(false);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_save).setEnabled(false);
        //int i = viewPager.getCurrentItem();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_logout:
                Log.i(TAG,"Clicked the logout button.");
                logout();
                return true;
            case R.id.action_settings:
                Log.i(TAG,"Clicked the settings button.");
                return true;
            case R.id.action_profile:
                Log.i(TAG,"Clicked on profile button.");
                ProfileFrag profileFrag = new ProfileFrag();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,profileFrag)
                        .addToBackStack(null)
                        .show(profileFrag)
                        .commit();
                return true;
            case R.id.action_test:
                Log.i(TAG,getSupportFragmentManager().getFragments().toString());
                return true;
            case R.id.action_invites:
                Log.i(TAG,"Clicked on invites button");
                InviteDialog inviteDialog = new InviteDialog();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.core_frame,inviteDialog,INV_FRAG)
                        .addToBackStack(INV_FRAG)
                        .show(inviteDialog)
                        .commit();
                return true;
            case R.id.action_edit:
                Log.i(TAG,"HELLO?");
                return false;
            case R.id.action_save:
                return false;

        }
        return false;
    }

    public void logout(){
        Log.d(TAG,"Logging out user:" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        AuthUI.getInstance().signOut(this);
    }

    @Override
    public void addToGroupRecycler(GroupBase groupBase) {
        Log.i(TAG,"Called addToGroupRecycler implementation in Core Activity");
        updateGroupRecycler.updateGroupRecycler(groupBase);
    }

    @Override
    public void addToEventRecycler(GroupEvent groupEvent) {
        Log.i(TAG,"Called addToEventRecycler implementation in the CoreAct");
        updateEventRecycler.updateEventRecycler(groupEvent);
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
                Log.i(TAG,"Top fragment is an instanceof OnBackListener");
                ((BackPressListener)topFragment).onBackPress();
            } else {
                Log.i(TAG,"BackPressListener interface is not inherited by this fragment.");
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
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
    protected void onStop() {
        super.onStop();
        viewModel.removeInviteListeners();
    }

    public class Core_Activity_Adapter extends FragmentPagerAdapter {
        private static final int NUM_PAGES = 2;

        private Core_Activity_Adapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return new Core_Events_Fragment();
                case 1:
                    return new Core_Group_Fragment();
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
    }
}

/* Creates a connection to the current user. Should move so that this code is only called when you are viewing another user.
    userViewModel.createConnection().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                Log.i(TAG,"Successfully added connection.");
            }
        }
    });
 */
