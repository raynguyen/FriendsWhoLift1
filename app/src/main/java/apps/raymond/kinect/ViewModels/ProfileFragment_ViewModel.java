package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.kinect.ObjectModels.DataModel;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.ObjectModels.User_Model;

/**
 * ViewModel class that holds information regarding a given profile.
 */
public class ProfileFragment_ViewModel extends ViewModel {
    private DataModel mRepo = new DataModel();
    private MutableLiveData<User_Model> mProfileModel = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mProfileConnections = new MutableLiveData<>();
    private MutableLiveData<List<Location_Model>> mProfileLocations = new MutableLiveData<>();

    public ProfileFragment_ViewModel(){
    }

    public void loadProfileModel(String profileID) {

    }

    public void setProfileModel(User_Model profileModel){
        mProfileModel.setValue(profileModel);
    }

    public MutableLiveData<User_Model> getProfileModel(){
        return mProfileModel;
    }

    public int getConnectionsCount(){
        return mProfileModel.getValue().getNumconnections();
    }

    public int getLocationsCount(){
        return mProfileModel.getValue().getNumlocations();
    }

    public void loadUserLocations(){
        String profileID = mProfileModel.getValue().getEmail();
        mRepo.getUsersLocations(profileID).addOnCompleteListener((Task<List<Location_Model>> task)->
                mProfileLocations.setValue(task.getResult())
        );
    }

    public MutableLiveData<List<Location_Model>> getLocations(){
        return mProfileLocations;
    }

    public void loadProfileConnections(String profileID){
        mRepo.getUserConnections(profileID).addOnCompleteListener((@NonNull Task<List<User_Model>> task)->{
            if (task.isSuccessful()) {
                mProfileConnections.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<User_Model>> getConnections(){
        return mProfileConnections;
    }


}
