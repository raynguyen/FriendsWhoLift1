package apps.raymond.friendswholift;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Implement the FirebaseUI login practices.

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser == null){
                    Log.e(TAG,"There is no signed in user.:");
                    Intent loginIntent = new Intent(MainActivity.this, LoginAct.class);
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
    }

    @Override
    public void onStart(){
        super.onStart();

        /*
        Listener monitors for changes in the User. This listener will trigger even if we are outside
        the MainActivity.
         */
        mAuth.addAuthStateListener(authStateListener);

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
    /*
    This method is called when the user clicks the 'Logout' item on the toolbar.
     */
    public void logout(){
        Log.d(TAG,"Logging out user:" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        AuthUI.getInstance().signOut(this);
    }

    public void openSettings(){
        Toast.makeText(this,"clicked Settings", Toast.LENGTH_SHORT).show();
    }
}
