package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

/**
 * ViewModel class for the core application. When an instance of this ViewModel is first created, we
 * call on the Repository to fetch the active user's accepted events, groups, and mEventModel/group
 * invitations and set them respectively to their LiveData counterparts.
 *
 * This ViewModel will also be responsible for cascading data updates to the Repository.
 *
 * ToDo: Observe the required FireStore collections in the repository. On change detections,
 *  we want to emit updates upstream to the ViewModel's LiveData via Transformations (not sure how
 *  to yet).
 *
 * ToDo: When we want to add/delete a single item from a list, we want to determine the difference
 *  between the old list and the new and update the views accordingly. This way we reduce the number
 *  of recycler view items that have to be recreated.
 *
 * ToDo: The ViewModel should expose LiveData objects to it's observers, not the Mutable form.
 */
public class Core_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepository;
    private MutableLiveData<String> mUserID = new MutableLiveData<>();
    private MutableLiveData<User_Model> mUserModel = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mConnections = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mEventInvitations = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mConnectionRequests = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mAcceptedEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mPublicEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mNewEventsFeed = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mPopFriendsFeed = new MutableLiveData<>();

    public Core_ViewModel(){
        mRepository = new Core_FireBaseRepo();
    }

    public void setUserDocument(User_Model user_model){
        mUserModel.setValue(user_model);
        mUserID.setValue(user_model.getEmail());
    }

    public void loadUserDocument(String userID){
        mRepository.getUserModel(userID).addOnCompleteListener((Task<User_Model> task)->{
            if(task.isSuccessful()){
                mUserModel.setValue(task.getResult());
                mUserID.setValue(task.getResult().getEmail());
            }
        });
    }

    public MutableLiveData<User_Model> getUserModel(){
        return mUserModel;
    }

    public MutableLiveData<String> getUserID(){
        return mUserID;
    }

    public void loadUserConnections(String userID){
        mRepository.getUserConnections(userID).addOnCompleteListener((@NonNull Task<List<User_Model>> task)->{
            if(task.isSuccessful()){
                mConnections.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<User_Model>> getUserConnections(){
        return mConnections;
    }

    /**
     * Query the data base to retrieve a list of the user's invitations.
     * @param userID User for whom we are retrieving the invitation sets
     */
    public void loadEventInvitations(String userID){
        mRepository.getEventInvitations(userID).addOnCompleteListener((Task<List<Event_Model>> task)->{
            if(task.isSuccessful()){
                mEventInvitations.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<Event_Model>> getEventInvitations(){
        return mEventInvitations;
    }

    public void loadConnectionRequests(String userID){
        mRepository.getConnectionRequests(userID)
                .addOnCompleteListener((@NonNull Task<List<User_Model>> task)-> {
                    if(task.isSuccessful()){
                        mConnectionRequests.setValue(task.getResult());
                    }
                });
    }

    public MutableLiveData<List<User_Model>> getConnectionRequests(){
        return mConnectionRequests;
    }

    public void setEventInvitations(List<Event_Model> newList){
        mEventInvitations.setValue(newList);
    }

    public void updateEventInviteDeclined(String userID, String eventName, User_Model user){
        mRepository.declineEventInvitation(eventName,userID,user);
    }

    //*------------------------------------------EVENTS------------------------------------------*//
    /**
     * Fetch the user's Event collection from the database and set the result to mAcceptedEvents.
     * Ideally, we would be able to remove the onCompleteListener via Transformations in the Repo.
     * @param userID Document parameter to query to correct collection.
     */
    public void loadAcceptedEvents(String userID){
        mRepository.getAcceptedEvents(userID).addOnCompleteListener((Task<List<Event_Model>> task)->{
            if(task.isSuccessful()&& task.getResult()!=null){
                mAcceptedEvents.setValue(task.getResult());
            }
        });
    }
    public void setAcceptedEvents(List<Event_Model> newList) {
        mAcceptedEvents.setValue(newList);
    }

    public MutableLiveData<List<Event_Model>> getAcceptedEvents(){
        return mAcceptedEvents;
    }

    public Task<Void> addEventToUser(String userID, Event_Model event){
        return mRepository.addEventToUser(userID, event);
    }

    public Task<Void> addUserToEvent(String userID,User_Model user, String eventName){
        return mRepository.addUserToEvent(userID,user,eventName);
    }

    public void deleteEventInvitation(String userID, String eventName){
        mRepository.deleteEventInvitation(userID, eventName);
    }

    public void loadPublicEvents(){
        mRepository.getPublicEvents().addOnCompleteListener((@NonNull Task<List<Event_Model>> task)-> {
            if(task.isSuccessful()){
                mPublicEvents.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<Event_Model>> getPublicEvents(){
        return mPublicEvents;
    }

    //This should load all the relevant recycler data.
    public void loadNewEventsFeed(){
        mRepository.loadNewEvents().addOnCompleteListener((@NonNull Task<List<Event_Model>> task)->{
            if(task.getResult()!=null){
                mNewEventsFeed.setValue(task.getResult());
            }
        });
    }

    public void loadPopularEventsFeed(){
        /*mRepository.loadPopularEvents().addOnCompleteListener((@NonNull Task<List<Event_Model>> task)->{
            if(task.getResult()!=null){
                mPopFriendsFeed.setValue(task.getResult());
            }
        });*/
    }

    public void loadLocationEventsFeed(){

    }

    public MutableLiveData<List<Event_Model>> getNewEventsFeed(){
        return mNewEventsFeed;
    }

    public MutableLiveData<List<Event_Model>> getPopularFeed(){
        return mPopFriendsFeed;
    }

    public Task<Boolean> checkForUser(String userID, String eventName){
        return mRepository.checkForUser(userID, eventName);
    }
    //*-------------------------------------------ETC--------------------------------------------*//
    public Task<byte[]> getImage(String uri){
        return mRepository.getImage(uri);
    }
    public Task<Uri> uploadImage(Uri uri, String name){
        return mRepository.uploadImage(uri, name);
    }
}


