/*
 * ToDo:
 * 1) In the event that a user is unable to connect to the internet and therefore our Firebase db,
 *  there should be a queue of tasks to execute to the Firebase db once a connection is established.
 * 2) On back press of NewGroupActivity, it should store the current state in the backstack.
 * 3) When clicking out of a User Input field, we should unfocus the view and close the keyboard.
 * 4) Make a new activity for Groups with a swipe user interface:
 * one shown a CardView with recycler view for all groups attached to the user
 * another for creating group.
 * 5) Obtain the 'Membership' authorization from the radio button and pass to the 'Group' document.
 * 6) Obtain the 'Invite' power from the spinner item and pass to document group.
 */

package apps.raymond.friendswholift;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import apps.raymond.friendswholift.HomeActFrags.TempAddStat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private static final String ADD_DIALOG = "AddStatDialog";
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    FirebaseFirestore fireDB;
    Button checkPRS_Btn, cancel_Btn, new_group_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        fireDB = FirebaseFirestore.getInstance();

        currentUser = mAuth.getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser == null){
                    Log.e(TAG,"There is no signed in user.:");
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                } else {
                    Log.d(TAG,"Current user:" + mAuth.getCurrentUser().getEmail());
                }
            }
        };

        Toolbar top_toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(top_toolbar);

        checkPRS_Btn = findViewById(R.id.checkpr_btn);
        cancel_Btn = findViewById(R.id.cancel_btn);
        new_group_Btn = findViewById(R.id.new_group);

        checkPRS_Btn.setOnClickListener(this);
        cancel_Btn.setOnClickListener(this);
        new_group_Btn.setOnClickListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        /*
        Listener monitors for changes in the User. This listener will trigger even if we are outside
        the MainActivity.
         */
        mAuth.addAuthStateListener(authStateListener);
        Log.d(TAG,"Creating instance of AuthStateListener.");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"MainActivity is shutting down.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.home_actionbar, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.checkpr_btn:
                Log.d(TAG,"Creating intent to start ManageStatsActivity.");
                Intent manage_intent = new Intent(MainActivity.this,
                        ManageStatsActivity.class);
                startActivity(manage_intent);
                break;
            case R.id.cancel_btn:
                Log.d(TAG,"Starting AddStatFragment.");
                TempAddStat addStatFrag = new TempAddStat();
                addStatFrag.setStyle(DialogFragment.STYLE_NORMAL,R.style.CustomDialog);
                addStatFrag.show(getSupportFragmentManager(),ADD_DIALOG);
                break;
            case R.id.new_group:
                Log.d(TAG, "Starting activity to create new group.");
                Intent new_group_intent = new Intent(MainActivity.this,
                        NewGroupActivity.class);
                startActivity(new_group_intent);
                break;
        }
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

    //This method is called when the user clicks the 'Logout' item on the toolbar.
    public void logout(){
        Log.d(TAG,"Logging out user:" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        AuthUI.getInstance().signOut(this);
    }

    public void openSettings(){
        Toast.makeText(this,"clicked Settings", Toast.LENGTH_SHORT).show();
    }
}
