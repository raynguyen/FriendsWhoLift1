package apps.raymond.kinect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import apps.raymond.kinect.login.Login_Activity;

public class Launch_Activity extends AppCompatActivity {
    private static final String TAG = "Launch_Activity";
    private final static int REQUEST_WRITE_STORAGE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //checkPermissions();

        Log.i(TAG,"LAUNCH ACTIVITY FIRST THING OR NO?");
        // ToDo: This needs to be moved to the repository.
        FirebaseAuth.getInstance().addAuthStateListener( new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
                Log.i(TAG,"Calling auth state change!");
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currUser == null){
                    Intent loginIntent = new Intent(Launch_Activity.this, Login_Activity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Log.i(TAG,"Starting login activity.");
                    startActivity(loginIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Log.d(TAG,"Current user:" + currUser.getEmail());
                    Intent coreIntent = new Intent(Launch_Activity.this, Core_Activity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Log.i(TAG,"Starting core activity.");
                    startActivity(coreIntent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    finish();
                }
                Log.i(TAG,"Removing auth listener");
                FirebaseAuth.getInstance().removeAuthStateListener(this);
            }
        });
    }

    //Todo: Checking for permissions here prevents animations from executing as intended.
    //Check for image write permissions to external storage.
    private void checkPermissions(){
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED) {
            //Request permission to write to storage.
            Log.i(TAG, "Requesting permission to write to external storage.");
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_WRITE_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG,"YOU CAN NOW WRITE TO STORAGE!");
                }
        }
    }
}
