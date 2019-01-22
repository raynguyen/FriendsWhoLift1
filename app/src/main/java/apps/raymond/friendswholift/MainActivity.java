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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Remove the back stack on log out.

//The Login button still launches the wrong activity.
//Implement the FirebaseUI login practices.


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        //Firebase Authenticator
        mAuth = FirebaseAuth.getInstance();

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(top_toolbar);
    }

    @Override
    public void onStart(){
        super.onStart();

        //Check if user is already signed in (i.e. non-null) and launch the login Activity if not.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Log.d(TAG,"No current user signed in.");
            Intent loginIntent = new Intent(MainActivity.this, LoginAct.class);
            startActivity(loginIntent);
        } else {
            Log.d(TAG,"Current user:" + mAuth.getCurrentUser().getEmail());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.home_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_logout:
                Log.d(TAG,"Logging out user:" + mAuth.getCurrentUser().getEmail());
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG,"Successfully logged out.");
                                startActivity(new Intent(
                                        MainActivity.this,LoginAct.class));
                            }
                        });
        }

        Toast.makeText(this,mAuth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
        //Handle the logout Menu click here.
        return true;
    }
}
