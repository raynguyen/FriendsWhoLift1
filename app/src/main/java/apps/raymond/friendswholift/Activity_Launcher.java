package apps.raymond.friendswholift;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Launcher extends AppCompatActivity {
    private static final String TAG = "Activity_Launcher";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currUser == null){
                    Intent loginIntent = new Intent(Activity_Launcher.this, Login_Activity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                } else {
                    Log.d(TAG,"Current user:" + currUser.getEmail());
                    Intent loginIntent = new Intent(Activity_Launcher.this, Main_Activity.class);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }
}
