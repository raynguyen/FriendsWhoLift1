package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

public class ProfileFragment_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepo = new Core_FireBaseRepo();
    private MutableLiveData<User_Model> mProfileModel = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mProfileConnections = new MutableLiveData<>();

    public ProfileFragment_ViewModel(){
    }

    public void setProfileModel(User_Model profileModel){
        mProfileModel.setValue(profileModel);
    }

    public int getConnectionsCount(){
        return mProfileModel.getValue().getNumconnections();
    }

    public int getLocationsCount(){
        return mProfileModel.getValue().getNumlocations();
    }

    public void loadProfileConnections(String profileID){
        mRepo.getUserConnections(profileID).addOnCompleteListener((@NonNull Task<List<User_Model>> task)->{
            if (task.isSuccessful()) {
                mProfileConnections.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<User_Model>> getProfileConnections(){
        return mProfileConnections;
    }


}
