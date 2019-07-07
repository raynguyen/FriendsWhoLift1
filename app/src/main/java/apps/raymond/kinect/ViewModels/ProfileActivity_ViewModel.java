package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.UserProfile.User_Model;

/**
 * ViewModel class that handles database transactions for the current user's profile.
 */
public class ProfileActivity_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepo = new Core_FireBaseRepo();
    private MutableLiveData<User_Model> mUserModel = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mConnections = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mAllPublicUsers = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mFilteredPublicUsers = new MutableLiveData<>(); //List of public users that are not connected with current user.
    private MutableLiveData<List<Location_Model>> mLocations = new MutableLiveData<>();

    public ProfileActivity_ViewModel(){}

    public void setUserModel(User_Model userModel){
        mUserModel.setValue(userModel);
    }

    public MutableLiveData<User_Model> getUserModel(){
        return mUserModel;
    }

    public void loadConnections(String userID){
        mRepo.getUserConnections(userID).addOnCompleteListener((@NonNull Task<List<User_Model>> task)-> {
            if(task.isSuccessful()){
                mConnections.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<User_Model>> getUserConnections(){
        return mConnections;
    }

    public void loadSuggestedConnections(String userID){
        //Todo: Determine an algorithm to determine how to find suggested users to connect with.
        // Currently returns the entire list of users for the application.
        mRepo.getAllUsers(userID).addOnCompleteListener((@NonNull Task<List<User_Model>> task)-> {
            if(task.isSuccessful()){
                mAllPublicUsers.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<User_Model>> getSuggestedConnections(){
        return mAllPublicUsers;
    }

    public void setSuggestedFiltered(List<User_Model> list){
        mFilteredPublicUsers.setValue(list);
    }

    public MutableLiveData<List<User_Model>> getSuggestedFiltered(){
        return mFilteredPublicUsers;
    }

    public Task<Void> requestUserConnection(String userID, User_Model userModel, String profileID,
                                            User_Model profileModel){
        Log.w("ProfileViewModel","REQUESTING CONNECTION WITH: "+profileModel.getEmail());
        return mRepo.requestUserConnection(userID, userModel, profileID, profileModel)
                .addOnCompleteListener((@NonNull Task<Void> task)-> {
                    if(task.isSuccessful()){
                        List<User_Model> suggestedList = mFilteredPublicUsers.getValue();
                        if(suggestedList!=null){
                            Log.w("ProfileViewModel","SUGGESTED LIST IS NOT NULL");
                            suggestedList.remove(profileModel);
                            //mFilteredPublicUsers.setValue(suggestedList);

                            /*
                            WHEN CLICKING ON A USER in a ConnectionsFragment, we load up a profile activity,
                            when we click the add user button, we are attempting to add the user using
                            the instance of the ViewModel when we click on the user. We need to route the
                            filtered suggested list to both the viewmodels some how.
                             */
                        } else {
                            Log.w("ProfileViewModel","SUGGESTED is null" );
                        }
                    }
                });
    }

    public Task<Void> deleteUserConnection(String userID, String connectionID){
        return mRepo.deleteUserConnection(userID, connectionID);
    }

    public Task<Boolean> checkForConnection(String userID, String profileID){
        return mRepo.checkForConnection(userID, profileID);
    }

    public Task<Boolean> checkForPendingConnection(String userID, String profileID){
        return mRepo.checkForPendingConnection(userID, profileID);
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

    public void loadUserLocations(String userID){
        mRepo.getUsersLocations(userID).addOnCompleteListener((Task<List<Location_Model>> task)->
            mLocations.setValue(task.getResult()));
    }

    public MutableLiveData<List<Location_Model>> getLocations(){
        return mLocations;
    }

    public Task<Void> addLocationToUser(String userID, Location_Model locationModel){
        return mRepo.addLocationToUser(userID,locationModel);
    }

}
