/*
 * On back click, prompt user to save changes or discard changes.
 * When the activity starts, we want to attach listeners to all of our Documents of concern.
 * When paused we want to remove all listeners.
 */

package apps.raymond.friendswholift;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import apps.raymond.friendswholift.Groups.GroupPagerAdapter;

public class Groups_Activity extends AppCompatActivity {
    private static final String TAG = "Group_Activity";

    public FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ToDo: Potentially set up the same viewpager layout for the login_screen and the group_activity
        setContentView(R.layout.groups_activity);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ViewPager mViewPager = findViewById(R.id.groups_container);
        GroupPagerAdapter adapter = new GroupPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.group_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Starting Groups_Activity.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            //Handle the logout Menu click here.
            case R.id.action_logout:
                logout();
                break;
            case R.id.action_settings:
                //Toast.makeText(this,mAuth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
                openSettings();
                break;
        }
        return true;
    }

    //ToDo: This is shared in both the Main and this activity, find best practice to minimise the code.
    public void logout(){
        Log.d(TAG,"Logging out user:" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        AuthUI.getInstance().signOut(this);
    }

    public void openSettings(){
        Toast.makeText(this,"clicked Settings", Toast.LENGTH_SHORT).show();
    }
}
