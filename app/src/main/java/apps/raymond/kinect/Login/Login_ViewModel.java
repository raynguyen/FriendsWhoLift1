package apps.raymond.kinect.Login;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

public class Login_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepository;
    private MutableLiveData<User_Model> mUserModel = new MutableLiveData<>();

    public Login_ViewModel(){
        this.mRepository = new Core_FireBaseRepo();

        /*
        Whenever a change is detected in the current FirebaseUser, we trigger a call to retrieve the
        new User_Model document from FireStore and set mUserModel to the result of the call. Observers
        in the application context will then call upon the correct ViewModel methods to retrieve the
        appropriate data and set the corresponding views.
         */
        Log.w("LoginViewModel","Instance of LoginViewModel created.");
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    String userID = firebaseAuth.getCurrentUser().getEmail();
                    Log.w("ViewModel","New user detected:" + userID);
                    loadCurrentUser(userID);
                }
            }
        });
    }

    private void loadCurrentUser(String userID){
        mRepository.getCurrentUser(userID).addOnCompleteListener(new OnCompleteListener<User_Model>() {
            @Override
            public void onComplete(@NonNull Task<User_Model> task) {
                if(task.isSuccessful()){
                    mUserModel.setValue(task.getResult());
                }
            }
        });
    }

    public MutableLiveData<User_Model> getCurrentUser(){
        return mUserModel;
    }

    void signInWithEmail(String email, String password){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.w("LoginViewModel","Successfully signed in with email");
                        }
                    }
                });
    }
}
