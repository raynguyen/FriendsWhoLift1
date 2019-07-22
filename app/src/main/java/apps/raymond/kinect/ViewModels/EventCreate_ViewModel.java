package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

/**
 * ViewModel class that holds the information pertaining to a new Event the user is creating.
 */
public class EventCreate_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepo;
    private MutableLiveData<List<User_Model>> mPublicUsers = new MutableLiveData<>();
    private String eventName;
    private String eventDesc;
    private int eventPrivacy;
    private List<String> eventPrimes;
    private List<User_Model> invitedUsers;

    public EventCreate_ViewModel(){
        mRepo = new Core_FireBaseRepo();
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public void setEventPrivacy(int eventPrivacy) {
        this.eventPrivacy = eventPrivacy;
    }

    public void setEventPrimes(List<String> eventPrimes) {
        this.eventPrimes = eventPrimes;
    }

    public void setInvitedUsers(List<User_Model> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }


}
