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
import android.support.v4.app.ActivityCompat;
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


import apps.raymond.friendswholift.Groups.Detailed_Group_Fragment;
import apps.raymond.friendswholift.Groups.Group_Create_Fragment;
import apps.raymond.friendswholift.UserProfile.ProfileFrag;

public class Core_Activity extends AppCompatActivity implements View.OnClickListener,
        Detailed_Group_Fragment.TransitionScheduler {
    private static final String TAG = "Core_Activity";

    private ViewPager viewPager;

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
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }


    // Returning false means Menu is never inflated and onPrepareOptionsMenu is never called.
    Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.clear();
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.home_actionbar,menu);

        if(!menu.findItem(R.id.action_edit_group).isVisible()){
            menu.findItem(R.id.action_edit_group).setVisible(false);
        }

        menu.findItem(R.id.action_edit_group).setVisible(false);
        int i = viewPager.getCurrentItem();
        switch(i){
            case 0:
                menu.findItem(R.id.action_profile).setVisible(true);
                menu.findItem(R.id.action_create_group).setVisible(false);
                menu.findItem(R.id.action_save_group).setVisible(false);
                menu.findItem(R.id.action_edit_event).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.action_profile).setVisible(false);
                menu.findItem(R.id.action_create_group).setVisible(true);
                menu.findItem(R.id.action_save_group).setVisible(false);
                menu.findItem(R.id.action_edit_event).setVisible(false);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_logout:
                Log.i(TAG,"Clicked the logout button.");
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
            case R.id.action_create_group: // Handle this in the Create Group Fragment
                return false;
            case R.id.action_edit_group: // Handle this in the Detailed Group Fragment
                //Log.i(TAG,"Edit group has id: "+item.getItemId());
                //MenuItem saveItem = menu.findItem(R.id.action_save_group);
                //saveItem.setVisible(true);
                //item.setVisible(false);
                return false; // Handle this in the Detailed Group Fragment
            case R.id.action_edit_event:
                return false;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        //int i = v.getId();
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


    public void scheduleStartTransition(final View sharedView){
        sharedView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        Log.i(TAG,"The sharedView called onPreDraw.");
                        sharedView.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                }
        );
    }
}
