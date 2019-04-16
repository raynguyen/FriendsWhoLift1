package apps.raymond.kinect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import apps.raymond.kinect.UserProfile.UserModel;

public class View_Profile_Activity extends AppCompatActivity {
    private static final String TAG ="ViewProfile";
    public static final String USER = "UserModel";

    UserModel user;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile_activity);
        Bundle args = getIntent().getExtras();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = args.getParcelable(USER);

        Log.i(TAG,"Starting view profile activity for user: " + user.getEmail());
    }

    MenuItem addUserAction, msgUserAction;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        addUserAction = menu.findItem(R.id.action_connect);
        msgUserAction = menu.findItem(R.id.action_message);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_connect:
                Log.i(TAG,"Connect with user.");
                return true;
            case R.id.action_message:
                Log.i(TAG,"Message user.");
                return true;
            default:
                return false;
        }
    }
}
