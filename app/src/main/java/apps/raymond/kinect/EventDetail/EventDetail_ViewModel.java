package apps.raymond.kinect.EventDetail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventDetail_ViewModel extends ViewModel {
    private MutableLiveData<Event_Model> mEventModel = new MutableLiveData<>();
    private MutableLiveData<List<Message_Model>> mEventMessages = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mEventAcceptedList = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mEventInvitedList = new MutableLiveData<>();

    private Core_FireBaseRepo mRepository;
    public EventDetail_ViewModel(){
        mRepository = new Core_FireBaseRepo();
    }

    public void loadEventModel(final String eventName){
        mRepository.getEventModel(eventName).addOnCompleteListener((Task<Event_Model> task)->
            mEventModel.setValue(task.getResult()));
    }

    public void loadEventMessages(final String eventName){
        mRepository.getEventMessages(eventName).addOnCompleteListener((@NonNull Task<List<Message_Model>> task)->{
            if(task.isSuccessful()){
                mEventMessages.setValue(task.getResult());
            }
        });
    }

    public void loadEventMembers(final String eventName){
        mRepository.getEventAttending(eventName).addOnCompleteListener((@NonNull Task<List<User_Model>> task)-> {
            if(task.isSuccessful()){
                mEventAcceptedList.setValue(task.getResult());
            }
        });

        mRepository.getEventInvited(eventName).addOnCompleteListener((@NonNull Task<List<User_Model>> task)-> {
            if(task.isSuccessful()){
                mEventInvitedList.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<Event_Model> getEventModel(){
        return mEventModel;
    }

    public MutableLiveData<List<User_Model>> getEventAccepted(){
        return mEventAcceptedList;
    }

    public MutableLiveData<List<User_Model>> getEventInvited(){
        return mEventInvitedList;
    }

    public MutableLiveData<List<Message_Model>> getEventMessages(){
        return mEventMessages;
    }

    public void leaveEvent(String userID, String eventName){
        mRepository.leaveEvent(userID, eventName);
    }

    public Task<Void> postNewMessage(String eventName, Message_Model message){
        return mRepository.postMessage(eventName,message);
    }

    public void setEventMessages(List<Message_Model> messages){
        mEventMessages.setValue(messages);
    }

    public Task<Void> addUserConnection(String userID,User_Model user){
        return mRepository.addConnection(userID,user);
    }

}
