package apps.raymond.kinect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import apps.raymond.kinect.Login.Login_Activity;

public class Launch_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ToDo: Determine the pattern required to remove FirebaseAuth from the application context WITHOUT having a black screen.
        /*
        On app launch, check to see if there is a signed in mUser. If a mUser exists, the app will
        go straight into the Core_Activity or will otherwise start the Login_Activity.
         */
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
                            .putExtra("userID",currUser.getEmail());
                    startActivity(coreIntent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
                finish();
                FirebaseAuth.getInstance().removeAuthStateListener(this);
            }
        });
    }
}
