package apps.raymond.kinect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import apps.raymond.kinect.Login.Login_Activity;

public class Launch_Activity extends AppCompatActivity {
    private static final String TAG = "Launch_Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ToDo: Determine the pattern required to remove FirebaseAuth from the application context.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /*
        On app launch, check to see if there is a signed in mUser. If a mUser exists, the app will
        go straight into the Core_Activity or will otherwise start the Login_Activity.
         */
        //ToDo: The animation from launch to the login activity takes long. Figure out why, may have to do with the fade_in transition.
        if(user==null){
            Log.w(TAG,"There is no active mUser so we are launching LoginActivity.");
            Intent loginIntent = new Intent(Launch_Activity.this, Login_Activity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            Log.w(TAG,"Active mUser detected so we are launching Core");
            Intent coreIntent = new Intent(Launch_Activity.this, Core_Activity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(coreIntent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }
        finish();
    }
}
