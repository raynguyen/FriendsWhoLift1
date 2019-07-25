/*
 * Splash screen activity for the application. As FirebaseAuth is the user backend for this project,
 * we listen to the FirebaseAuth state on launch.
 *
 * If a user is detected, we want to finish this activity and start the Core_Activity.
 * Otherwise, start the Login_Activity.
 */
package apps.raymond.kinect.StartUp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.R;

public class Launch_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ToDo: Determine the pattern required to remove FirebaseAuth from the application context
        // WITHOUT having a black screen.
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currUser == null) {
                    Intent loginIntent = new Intent(Launch_Activity.this, Login_Activity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Intent coreIntent = new Intent(Launch_Activity.this, Core_Activity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .putExtra(Core_Activity.USER_ID,currUser.getEmail());
                    startActivity(coreIntent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
                finish();
                FirebaseAuth.getInstance().removeAuthStateListener(this);
            }
        });
    }
}
