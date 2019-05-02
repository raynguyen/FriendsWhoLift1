package apps.raymond.kinect.FireBaseRepo;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuth_Layer {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public FirebaseAuth_Layer(){
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        });
    }
}
