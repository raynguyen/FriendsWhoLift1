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
import apps.raymond.kinect.Groups.Group_Model;
import apps.raymond.kinect.Message_Model;
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
    private MutableLiveData<User_Model> mUserModel = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mEventInvitations = new MutableLiveData<>();
    private MutableLiveData<List<Group_Model>> mGroupInvitations = new MutableLiveData<>();
    private MutableLiveData<List<Message_Model>> mEventMessages = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mAcceptedEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mPublicEvents = new MutableLiveData<>();
    private MutableLiveData<List<Group_Model>> mUsersGroups = new MutableLiveData<>();

    public Core_ViewModel(){
        mRepository = new Core_FireBaseRepo();
    }

    public void loadUserDocument(String userID){
        mRepository.getUserDocument(userID)
                .addOnCompleteListener(new OnCompleteListener<User_Model>() {
                    @Override
                    public void onComplete(@NonNull Task<User_Model> task) {
                        if(task.isSuccessful()){
                            mUserModel.setValue(task.getResult());
                        }
                    }
                }
        );
    }

    public void setUserDocument(User_Model user_model){
        mUserModel.setValue(user_model);
    }

    public MutableLiveData<User_Model> getUserModel(){
        return mUserModel;
    }

    /**
     * Query the data base to retrieve a list of the user's invitations.
     * @param userID User for whom we are retrieving the invitation sets
     */
    public void loadUserInvitations(String userID){
        mRepository.getEventInvitations(userID)
                .addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Event_Model>> task) {
                        if(task.isSuccessful()){
                            mEventInvitations.setValue(task.getResult());
                        }
                    }
                });

        mRepository.getGroupInvitations(userID)
                .addOnCompleteListener(new OnCompleteListener<List<Group_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Group_Model>> task) {
                        if(task.isSuccessful()){
                            mGroupInvitations.setValue(task.getResult());
                        }
                    }
                });
    }

    public MutableLiveData<List<Event_Model>> getEventInvitations(){
        return mEventInvitations;
    }

    public void setEventInvitations(List<Event_Model> newEventInvitations){
        mEventInvitations.setValue(newEventInvitations);
    }

    public MutableLiveData<List<Group_Model>> getGroupInvitations(){
        return mGroupInvitations;
    }

    public void declineEventInvitation(String eventName, String userID, User_Model user){
        mRepository.declineEventInvitation(eventName,userID,user);
    }
    //*------------------------------------------EVENTS------------------------------------------*//
    /**
     * Fetch the user's Event collection from the database and set the result to mAcceptedEvents.
     * Ideally, we would be able to remove the onCompleteListener via Transformations in the Repo.
     * @param userID Document parameter to query to correct collection.
     */
    public void loadAcceptedEvents(String userID){
        mRepository.getAcceptedEvents(userID)
                .addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Event_Model>> task) {
                        if(task.isSuccessful()&& task.getResult()!=null){
                            mAcceptedEvents.setValue(task.getResult());
                        }
                    }
                });
    }
    public void setAcceptedEvents(List<Event_Model> newList){
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

    public MutableLiveData<List<Message_Model>> getEventMessages(Event_Model event){
        mRepository.getMessages(event)
                .addOnCompleteListener(new OnCompleteListener<List<Message_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Message_Model>> task) {
                        if(task.isSuccessful() && task.getResult()!=null){
                            mEventMessages.setValue(task.getResult());
                        }
                    }
                });
                return mEventMessages;
    }

    public void deleteEventInvitation(String userID, String eventName){
        mRepository.deleteEventInvitation(userID, eventName);
    }

    public Task<Void> postNewMessage(Event_Model event, Message_Model message){
        return mRepository.postMessage(event,message);
    }

    public void loadPublicEvents(){
        mRepository.getPublicEvents().addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<Event_Model>> task) {
                if(task.isSuccessful()){
                    mPublicEvents.setValue(task.getResult());
                }
            }
        });
    }

    public MutableLiveData<List<Event_Model>> getPublicEvents(){
        return mPublicEvents;
    }
    //*------------------------------------------GROUPS------------------------------------------*//

    /**
     * Fetch the user's Group collection from the database and set the result to mUsersGroups.
     * Ideally, we would be able to remove the onCompleteListener via Transformations in the Repo.
     * @param userID Document parameter to query to correct collection.
     */
    public void loadUserGroups(String userID){
        mRepository.getUsersGroups(userID)
                .addOnCompleteListener(new OnCompleteListener<List<Group_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Group_Model>> task) {
                        if(task.isSuccessful()){
                            mUsersGroups.setValue(task.getResult());
                        }
                    }
                });
    }

    public void loadAcceptedGroups(String userID){
        mRepository.getUsersGroups(userID);
    }
    public MutableLiveData<List<Group_Model>> getUsersGroups(){
        return mUsersGroups;
    }

    public void sendGroupInvites(Group_Model groupBase, List<User_Model> inviteList){
        //mRepository.sendGroupInvites(groupBase,inviteList);
    }
    public Task<List<Group_Model>> fetchGroupInvites(String userID){
        return mRepository.getGroupInvitations(userID);
    }

    public Task<Void> createGroup(String userID, User_Model user, Group_Model groupBase, List<User_Model> inviteList){
        return null;
    }
    public Task<Group_Model> getGroup(){
        return null;
    }
    public Task<List<Group_Model>> getUsersGroups(String userID){
        return mRepository.getUsersGroups(userID);
    }

    public Task<Void> addUserToGroup(String userID,User_Model userModel, Group_Model group){
        return null;
    }
    public Task<List<User_Model>> fetchGroupMembers(Group_Model group){
        return null;
    }
    public void updateGroup(Group_Model groupBase){
        //mRepository.editGroup(groupBase);
    }
    public Task<Void> declineGroupInvite(String userID, User_Model userModel, String groupName){
        return null;
    }
    //*-------------------------------------------ETC--------------------------------------------*//

    public Task<byte[]> getImage(String uri){
        return mRepository.getImage(uri);
    }
    public Task<Uri> uploadImage(Uri uri, String name){
        return mRepository.uploadImage(uri, name);
    }

    //*-------------------------------------------USER-------------------------------------------*//
    public Task<List<User_Model>> fetchUsers(String userID){
        return mRepository.getAllUsers(userID);
    }

    //Fetch mEventModel invitations (implement observer once I can figure out how to) and set as LiveData object here.
    private void listenForInvitations(){
        /*
        mRepository.getEventInvitations()
                .addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Event_Model>> task) {
                        if(task.isSuccessful() && task.getResult()!=null){
                            mEventInvitations.setValue(task.getResult());
                        }
                    }
                });
        mRepository.getGroupInvitations()
                .addOnCompleteListener(new OnCompleteListener<List<Group_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Group_Model>> task) {
                        if(task.isSuccessful()){
                            mGroupInvitations.setValue(task.getResult());
                        }
                    }
                });
               */
    }


}


