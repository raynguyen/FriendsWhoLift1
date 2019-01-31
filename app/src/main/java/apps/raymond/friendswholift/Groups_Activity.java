/*
 * When the activity starts, we want to attach listeners to all of our Documents of concern.
 * When paused we want to remove all listeners.
 */

package apps.raymond.friendswholift;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
        setContentView(R.layout.groups_activity_screen);

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
}
