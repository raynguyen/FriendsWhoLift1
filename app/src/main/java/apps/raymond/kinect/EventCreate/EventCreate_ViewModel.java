package apps.raymond.kinect.EventCreate;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.UserProfile.User_Model;
//Create more fields that are set from the EventDetails and Map fragments. When the user clicks create event
//from the activity, get all the required fields from this viewmodel and create the event from the activity.
public class EventCreate_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepo = new Core_FireBaseRepo();
    private MutableLiveData<List<Location_Model>> mLocations = new MutableLiveData<>();
    private MutableLiveData<String> mEventName = new MutableLiveData<>();
    private MutableLiveData<String> mEventDesc = new MutableLiveData<>();
    private MutableLiveData<Long> mEventStart = new MutableLiveData<>();
    private List<User_Model> mInviteList = new ArrayList<>();
    private List<String> mEventPrimes = new ArrayList<>();
    private Event_Model mEventModel = new Event_Model();

    void setEventName(String s){
        mEventName.setValue(s);
        mEventModel.setName(s);
    }

    MutableLiveData<String> getEventName(){
        return mEventName;
    }

    void setEventDesc(String s){
        mEventDesc.setValue(s);
        mEventModel.setDesc(s);
    }

    MutableLiveData<String> getEventDesc(){
        return mEventDesc;
    }

    MutableLiveData<Long> getEventStart(){
        return mEventStart;
    }

    public void setEventStart(long start){
        mEventStart.setValue(start);
        mEventModel.setLong1(start);
    }

    public void setEventAddress(String address){
        mEventModel.setAddress(address);
    }

    List<String> getEventPrimes(){
        return mEventPrimes;
    }

    Event_Model getEventModel(){
        return mEventModel;
    }

    void setEventLat(double lat){
        mEventModel.setLat(lat);
    }

    void setEventLong(double lng){
        mEventModel.setLng(lng);
    }

    void addEventPrime(String prime){
        mEventPrimes.add(prime);
    }

    void removeEventPrime(String prime){
        mEventPrimes.remove(prime);
    }

    void addToInviteList(User_Model userModel){
        mInviteList.add(userModel);
    }

    void removeFromInviteList(User_Model userModel){
        mInviteList.remove(userModel);
    }

    List<User_Model> getInviteList(){
        return mInviteList;
    }

    Task<List<User_Model>> getInvitableUsers(String userID){
        return mRepo.getAllUsers(userID);
    }

    public MutableLiveData<List<Location_Model>> getLocations(){
        return mLocations;
    }

    Task<Void> createEvent(Event_Model event){
        return mRepo.createEvent(event);
    }

    void addEventToUser(String userID, Event_Model event){
        mRepo.addEventToUser(userID, event);
    }
    void addUserToEvent(String userID,User_Model user, String eventName){
        mRepo.addUserToEvent(userID,user,eventName);
    }

    void sendEventInvites(Event_Model event, List<User_Model> inviteList){
        mRepo.sendEventInvites(event,inviteList);
    }
}
