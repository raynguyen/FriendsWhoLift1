package apps.raymond.kinect.StartUp;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;

import apps.raymond.kinect.ObjectModels.DataModel;
import apps.raymond.kinect.ObjectModels.User_Model;

/**
 * ViewModel class for handling Login and SignUp procedures.
 *
 * When an instance of the Login_Activity is started, an instance of this ViewModel is created to be
 * shared by the activity and its two child fragments. The principle purpose of this ViewModel class
 * is to contain a MutableLiveData object of the application's current user.
 *
 * mUserField is set via two methods:
 * 1. loadExistingUser which is called when an existing user logs in and
 * 2. createUserDocument which is called when a new user registers to the application.
 *
 * Todo: This ViewModel is janky because we would ideally have no android imports with the exclusion
 *  of the arch.lifecycle components.
 *
 * ToDo: Determine a pattern that removes the need to add onCompleteListener from the process of
 *  registering a new user with the project.
 */
public class Login_ViewModel extends ViewModel {
    private DataModel mRepository = new DataModel();
    private MutableLiveData<User_Model> mUserModel = new MutableLiveData<>();

    /**
     * Pass a reference to the current User_Model to be observed.
     * @return A reference to the User_Model held by this ViewModel's instance.
     */
    public MutableLiveData<User_Model> getCurrentUser(){
        return mUserModel;
    }

    public void setUserModel(User_Model user){
        mUserModel.setValue(user);
    }

    /**
     * Use the arguments provided to sign in the user via FirebaseAuth. If successful, this will
     * trigger loadExistingUser.
     * @param email User email credential
     * @param password User account password
     */
    public Task<Boolean> signInWithEmail(final String email, String password){
        return mRepository.signInViaEmail(email, password);
    }

    /**
     * On successful user authentication, this method is called to fetch a User_Model object from
     * the Repository and set mUserModel to the result.
     * @param userID ID of the user to retrieve from the database.
     */
    public void loadExistingUser(String userID){
        mRepository.getUserModel(userID)
                .addOnCompleteListener((@NonNull Task<User_Model> task)-> {
                    if(task.isSuccessful()){
                        mUserModel.setValue(task.getResult());
                    }
                });
    }

    /**
     * Function that registers new users with the application's FirebaseAuth project. If registration
     * is successful, we then call on FirebaseAuth to sign the user into the application which will
     * update the
     * @param userEmail Registration email
     * @param password Registration password
     */
    public Task<Boolean> registerWithEmail(String userEmail, String password){
        return mRepository.registerViaEmail(userEmail, password);

        /*
        * FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail,password);
                        createUserDocument(newUser);*/
    }

    /**
     * Calls upon the Repository to create a document for the newly registered user. On complete, we
     * set mUserModel with to the User_Model passed through the argument. This method is called when
     * the database does not contain a document for the user (i.e. new registration).
     * @param user Object to set LiveData mUserModel.
     */
    public Task<Void> createUserDocument(final User_Model user){
        return mRepository.createNewUserDocument(user);
    }

}
