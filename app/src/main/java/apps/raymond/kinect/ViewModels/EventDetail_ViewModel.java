package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventDetail_ViewModel extends ViewModel {
    private MutableLiveData<List<User_Model>> mEventAcceptedList = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mEventInvitedList = new MutableLiveData<>();

    private Core_FireBaseRepo mRepository;
    public EventDetail_ViewModel(){
        mRepository = new Core_FireBaseRepo();
    }

    public void loadEventUsers(final String eventName){
        mRepository.getEventAttending(eventName)
                .addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<User_Model>> task) {
                        if(task.isSuccessful()){
                            mEventAcceptedList.setValue(task.getResult());
                        }
                    }
                });

        mRepository.getEventInvited(eventName)
                .addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<User_Model>> task) {
                        if(task.isSuccessful()){
                            mEventInvitedList.setValue(task.getResult());
                        }
                    }
                });
    }

    public MutableLiveData<List<User_Model>> getEventAccepted(){
        return mEventAcceptedList;
    }

    public MutableLiveData<List<User_Model>> getEventInvited(){
        return mEventInvitedList;
    }

    public Task<Void> addUserConnection(String userID,User_Model user){
        return mRepository.addConnection(userID,user);
    }

}
