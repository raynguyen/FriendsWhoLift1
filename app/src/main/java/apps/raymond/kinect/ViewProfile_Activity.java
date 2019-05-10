package apps.raymond.kinect;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import apps.raymond.kinect.UserProfile.User_Model;

public class ViewProfile_Activity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG ="ViewProfile";
    public static final String USER = "User_Model";

    User_Model user;
    Core_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile_activity);
        Bundle args = getIntent().getExtras();

        viewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);

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
                viewModel.addUserConnection(user);
                return true;
            case R.id.action_message:
                Log.i(TAG,"Message user.");
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case -1:
                this.finish();
                break;
        }
    }
}
