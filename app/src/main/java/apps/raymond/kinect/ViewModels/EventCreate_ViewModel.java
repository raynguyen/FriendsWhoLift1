package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Model.DataModel;
import apps.raymond.kinect.ObjectModels.User_Model;

/**
 * ViewModel class that holds the information pertaining to a new Event the user is creating.
 */
public class EventCreate_ViewModel extends ViewModel {
    private DataModel mRepo;
    private MutableLiveData<Boolean> mValidEvent = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mPublicUsers = new MutableLiveData<>();
    private String eventName;
    private String eventDesc;
    private int eventPrivacy = 0;
    private long eventStart = 0;
    private List<String> eventPrimes;
    private List<User_Model> invitedUsers;

    public EventCreate_ViewModel(){
        mRepo = new DataModel();
        mValidEvent.setValue(false);
        eventPrimes = new ArrayList<>(5);
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

    public void setInvitedUsers(List<User_Model> invitedUsers) {
        this.invitedUsers = invitedUsers;
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

}
