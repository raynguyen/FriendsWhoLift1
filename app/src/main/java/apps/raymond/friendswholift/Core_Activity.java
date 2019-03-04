/*
 * ToDo:
 * 1. Get the user permission for camera and document access on start up and store as a SharedPreference.
 * 2. When the user calls createEvent or createGroup and then completes the process, manually update
 *    the RecyclerView to limit having to read all the Documents.
 * 3. Move all the currentUser stuff to repository return methods.
 */

package apps.raymond.friendswholift;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.friendswholift.Groups.Group_Create_Fragment;
import apps.raymond.friendswholift.Groups.Group_Detail_Fragment;
import apps.raymond.friendswholift.UserProfile.ProfileFrag;

public class Core_Activity extends AppCompatActivity{
    private static final String TAG = "Core_Activity";

    ViewPager viewPager;

    public BackPressInterface backPressListener;
    public interface BackPressInterface{
        void backPress();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        postponeEnterTransition();
        setContentView(R.layout.core_activity);

        Toolbar toolbar = findViewById(R.id.core_toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = findViewById(R.id.core_ViewPager);
        Core_Activity_Adapter pagerAdapter = new Core_Activity_Adapter(getSupportFragmentManager());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                //invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);
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
    public void onBackPressed() {
        Log.i(TAG,getSupportFragmentManager().getFragments().toString());
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
            if(topFragment instanceof BackPressInterface){
                ((BackPressInterface) topFragment).backPress();
            } else {
                super.onBackPressed();
            }
        }
    }

}
