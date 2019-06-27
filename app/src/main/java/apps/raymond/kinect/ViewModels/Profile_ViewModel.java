package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.UserProfile.User_Model;

public class Profile_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepo;
    private MutableLiveData<List<User_Model>> mConnections = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mSuggestedConnections = new MutableLiveData<>();
    private MutableLiveData<List<Location_Model>> mLocations = new MutableLiveData<>();

    public Profile_ViewModel(){
        mRepo = new Core_FireBaseRepo();
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
        //Currently returns the entire list of users for the application.
        mRepo.getAllUsers(userID).addOnCompleteListener((@NonNull Task<List<User_Model>> task)-> {
            if(task.isSuccessful()){
                mSuggestedConnections.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<User_Model>> getSuggestedConnections(){
        return mSuggestedConnections;
    }

    //ToDo: OnComplete, we want to update the list held by suggested users. We want to remove the
    // suggested user should we create the connection.
    public Task<Void> createUserConnection(String userID, User_Model profileModel){
        return mRepo.addConnection(userID,profileModel);
    }

    public Task<Void> deleteUserConnection(String userID, String connectionID){
        return mRepo.deleteUserConnection(userID, connectionID);
    }

    public Task<Boolean> checkForConnection(String userID, String checkID){
        return mRepo.checkForConnection(userID,checkID);
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
