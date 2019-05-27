package apps.raymond.kinect.EventCreate;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.Location_Model;
import apps.raymond.kinect.UserProfile.User_Model;
//Create more fields that are set from the EventDetails and Map fragments. When the user clicks create event
//from the activity, get all the required fields from this viewmodel and create the event from the activity.
public class EventCreate_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepo = new Core_FireBaseRepo();
    private MutableLiveData<List<Location_Model>> mLocationsSet = new MutableLiveData<>();
    private MutableLiveData<String> mEventName = new MutableLiveData<>();
    private MutableLiveData<String> mEventDesc = new MutableLiveData<>();
    private List<User_Model> mInviteList = new ArrayList<>();

    public void setEventName(String s){
        mEventName.setValue(s);
    }

    public MutableLiveData<String> getEventName(){
        return mEventName;
    }

    public void setEventDesc(String s){
        mEventDesc.setValue(s);
    }

    public MutableLiveData<String> getEventDesc(){
        return mEventDesc;
    }

    public void addToInviteList(User_Model userModel){
        mInviteList.add(userModel);
    }

    public void removeFromInviteList(User_Model userModel){
        mInviteList.remove(userModel);
    }

    public List<User_Model> getInviteList(){
        return mInviteList;
    }

    public Task<List<User_Model>> getInvitableUsers(String userID){
        return mRepo.getAllUsers(userID);
    }

    public void loadUserLocations(String userID){
        mRepo.getUsersLocations(userID)
                .addOnCompleteListener(new OnCompleteListener<List<Location_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Location_Model>> task) {
                        mLocationsSet.setValue(task.getResult());
                    }
                });
    }

    public MutableLiveData<List<Location_Model>> getLocationSet(){
        return mLocationsSet;
    }
}
