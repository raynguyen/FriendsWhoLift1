package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.Model.DataModel;
import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.ObjectModels.User_Model;

//ToDo: The fields should be bound to the view as opposed to fields held by the view model.
/**
 * ViewModel class that holds the information pertaining to a new Event the user is creating.
 */
public class EventCreate_ViewModel extends ViewModel {
    private DataModel mRepo;
    private Event_Model mEventModel;
    private MutableLiveData<Boolean> mValidEvent = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mPublicUsers = new MutableLiveData<>();
    private MutableLiveData<List<Location_Model>> mLocations = new MutableLiveData<>();
    private String eventName;
    private String eventDesc;
    private double lat;
    private double lng;
    private int eventPrivacy = 0;
    private long eventStart = 0;
    private List<String> eventPrimes = new ArrayList<>(5);
    private List<User_Model> invitedUsers;

    public EventCreate_ViewModel(){
        mRepo = new DataModel();
        mValidEvent.setValue(false);
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
        validateEventModel();
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
        validateEventModel();
    }

    public void setEventPrivacy(int eventPrivacy) {
        this.eventPrivacy = eventPrivacy;
        validateEventModel();
    }

    public void addEventPrime(String prime) {
        eventPrimes.add(prime);
    }

    public void removeEventPrime(String prime){
        eventPrimes.remove(prime);
    }

    public void setEventStart(long eventStart){
        this.eventStart = eventStart;
    }

    public void addInvitedUser(User_Model user){
        invitedUsers.add(user);
    }

    public void removeInvitedUser(User_Model user){
        invitedUsers.remove(user);
    }

    public void setEventLat(double lat){
        this.lat = lat;
    }

    public void setEventLng(double lng){
        this.lng = lng;
    }

    public MutableLiveData<List<Location_Model>> getUserLocations(){
        return mLocations;
    }


    public MutableLiveData<Boolean> getValid(){
        return mValidEvent;
    }

    /**
     * Function that is called whenever there is a change in the data pertaining to the creation of
     * an Event_Model.
     */
    private void validateEventModel(){
        if(eventName == null || eventDesc == null || eventStart == 0 ){
            return;
        }
        if(eventName.length() > 0 && eventDesc.length() >0 && eventPrivacy != 0){
            mValidEvent.postValue(true);
        } else {
            mValidEvent.postValue(false);
        }
    }

    public void createEventModel(){
        mEventModel = new Event_Model(
                "me",
                eventName,
                eventDesc,
                eventPrivacy,
                null,
                (ArrayList) eventPrimes,
                eventStart,
                lat,
                lng
        );

        mRepo.createEvent(mEventModel);
        //ToDo: Add field in our Model class that is set to true for successful event creates that is observed via transform here.
    }
}
