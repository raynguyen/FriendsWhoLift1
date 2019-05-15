package apps.raymond.kinect.Login;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

/**
 * ViewModel class for handling Login and SignUp protocols required by the application.
 *
 * When an instance of the Login_Activity is started, an instance of this ViewModel is created to be
 * shared by the activity and its two child fragments. When this ViewModel is instantiated, we
 * attach an observer to the FirebaseAuth to detect when there is a state change (i.e. a User token
 * is returned and held by this application).
 */
public class Login_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepository;
    private MutableLiveData<User_Model> mUserModel = new MutableLiveData<>();

    public Login_ViewModel(){
        this.mRepository = new Core_FireBaseRepo();

        /*
         * Whenever a change is detected in FirebaseAuthState, we trigger a call to retrieve the active
         * User_Model from the Repository and set mUserModel to the result of the call. Observers in
         * the application context will then cascade the appropriate methods to launch the Core_Activity.
         *
         * ToDo: May have to consider refactoring to make the AuthState an observable object held
         *  by the ViewModel and observe from the application (under the principle that there should
         *  be no "code" within the ViewModel class).
         */
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    String userID = firebaseAuth.getCurrentUser().getEmail();
                    loadCurrentUser(userID);
                }
            }
        });
    }

    /**
     * Pass a reference to the current User_Model to be observed.
     * @return A reference to the User_Model held by this ViewModel's instance.
     */
    public MutableLiveData<User_Model> getCurrentUser(){
        return mUserModel;
    }

    /**
     * Fetch the User_Model object from the Repository and set mUserModel with the result.
     * @param userID ID of the user to retrieve from the database.
     */
    private void loadCurrentUser(String userID){
        mRepository.getCurrentUser(userID)
                .addOnCompleteListener(new OnCompleteListener<User_Model>() {
                    @Override
                    public void onComplete(@NonNull Task<User_Model> task) {
                        if(task.isSuccessful()){
                            mUserModel.setValue(task.getResult());
                        }
                    }
                });
    }

    /**
     * Use the arguments provided to sign in the user via FirebaseAuth. This will in turn trigger
     * any listeners of FirebaseAuthState to proceed with their onAuthStateChange method.
     * @param email User email credential
     * @param password User account password
     */
    void signInWithEmail(String email, String password){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password);
    }
}
