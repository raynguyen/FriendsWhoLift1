package apps.raymond.kinect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import apps.raymond.kinect.login.Login_Activity;

public class Launch_Activity extends AppCompatActivity {
    private static final String TAG = "Launch_Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ToDo: This needs to be moved to the repository.
        FirebaseAuth.getInstance().addAuthStateListener( new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currUser == null){
                    Intent loginIntent = new Intent(Launch_Activity.this, Login_Activity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Log.i(TAG,"Starting login activity.");
                    startActivity(loginIntent);
                    finish();
                } else {
                    Log.d(TAG,"Current user:" + currUser.getEmail());
                    Intent loginIntent = new Intent(Launch_Activity.this, Core_Activity.class);
                    Log.i(TAG,"Starting core activity.");
                    startActivity(loginIntent);
                    finish();
                }
                overridePendingTransition(R.anim.fui_slide_in_right, R.anim.fui_slide_out_left);
            }
        });

    }
}