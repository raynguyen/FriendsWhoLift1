/*
 * ToDo:
 * Get the user permission for camera and document access on start up and store as a SharedPreference.
 */
package apps.raymond.friendswholift;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import apps.raymond.friendswholift.Groups.Group_Create_Fragment;

public class Core_Activity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Core_Activity";

    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        Log.i(TAG,"Launching the Core Activity.");
        Toolbar toolbar = findViewById(R.id.core_toolbar);
        setSupportActionBar(toolbar);

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
                Log.i(TAG,"NEW PAGE SELECTED");
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    // Returning false means Menu is never inflated and onPrepareOptionsMenu is never called.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int i = viewPager.getCurrentItem();
        switch(i){
            case 0:
                menu.clear();
                getMenuInflater().inflate(R.menu.home_actionbar,menu);
                break;
            case 1:
                menu.clear();
                getMenuInflater().inflate(R.menu.groups_toolbar,menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_logout:
                Log.i(TAG,"Clicked the logout button.");
                break;
            case R.id.action_settings:
                Log.i(TAG,"Clicked the settings button.");
                break;
            case R.id.action_create_group:
                Log.i(TAG,"Clicked on create group button.");
                Fragment createGroupFragment = new Group_Create_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.core_frame,createGroupFragment)
                        .addToBackStack(null)
                        .show(createGroupFragment)
                        .commit();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){

        }
    }

}
