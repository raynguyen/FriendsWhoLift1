package apps.raymond.kinect;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.Groups.Group_Model;
import apps.raymond.kinect.UserProfile.User_Model;

public class Core_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepository;
    private MutableLiveData<List<Event_Model>> mEventInvitations = new MutableLiveData<>();
    private MutableLiveData<List<Group_Model>> mGroupInvitations = new MutableLiveData<>();
    private MutableLiveData<List<Message_Model>> mEventMessages = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mAcceptedEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> publicEvents = new MutableLiveData<>();
    private MutableLiveData<List<Group_Model>> mUsersGroups = new MutableLiveData<>();
    public Core_ViewModel(){
        this.mRepository = new Core_FireBaseRepo();
        mRepository.getAcceptedEvents().addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<Event_Model>> task) {
                if(task.isSuccessful()&& task.getResult()!=null){
                    mAcceptedEvents.setValue(task.getResult());
                }
            }
        });
        mRepository.getUsersGroups().addOnCompleteListener(new OnCompleteListener<List<Task<Group_Model>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<Group_Model>>> task) {
                if(task.isSuccessful()&& task.getResult()!=null){
                    //Have to change the repository method to return a List<Group_Model>;
                }
            }
        });
        loadPublicEvents();
        listenForInvitations();
        /*
        When creating instance of ViewModel, retrieve the current user from FirebaseAuth by attaching
        a listener that updates everytime there is a change in the currentuser.
        */
    }

    //*-------------------------------------------USER-------------------------------------------*//
    public Task<AuthResult> emailSignIn(String email, String password){
        return mRepository.emailSignIn(email,password);
    }
    public Task<Void> signOut(Context context){
        return mRepository.signOut(context);
    }
    public Task<User_Model> getCurrentUser(){
        return mRepository.getCurrentUser();
    }
    public Task<List<User_Model>> fetchUsers(){
        return mRepository.fetchUsers();
    }
    public Task<List<User_Model>> getConnections(){
        return mRepository.getConnections();
    }
    public Task<Void> addUserConnection(User_Model user){
        return mRepository.addConnection(user);
    }

    //Fetch event invitations (implement observer once I can figure out how to) and set as LiveData object here.
    private void listenForInvitations(){
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
    }
    public MutableLiveData<List<Event_Model>> getEventInvitations(){
        return mEventInvitations;
    }

    public MutableLiveData<List<Group_Model>> getGroupInvitations(){
        return mGroupInvitations;
    }
    //*------------------------------------------EVENTS------------------------------------------*//
    public MutableLiveData<List<Event_Model>> getAcceptedEvents(){
        return mAcceptedEvents;
    }

    public MutableLiveData<List<Message_Model>> getMessages(Event_Model event){
        mRepository.getMessages(event).addOnCompleteListener(new OnCompleteListener<List<Message_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<Message_Model>> task) {
                if(task.isSuccessful() && task.getResult()!=null){
                    mEventMessages.setValue(task.getResult());
                }
            }
        });
        return mEventMessages;
    }
    public Task<Void> postNewMessage(Event_Model event, Message_Model message){
        return mRepository.postMessage(event,message);
    }
    private void loadPublicEvents(){
        mRepository.getPublicEvents().addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<Event_Model>> task) {
                Log.w("RepositoryMethods","Task to fetch public events from store completed.");
                if(task.isSuccessful()){
                    Log.w("RepositoryModel","Getting public events complete with list size = " +task.getResult().size());
                    publicEvents.setValue(task.getResult());
                }
            }
        });
    }
    public MutableLiveData<List<Event_Model>> getPublicEvents(){
        return publicEvents;
    }

    public Task<Void> createEvent(Event_Model groupEvent){
        return mRepository.createEvent(groupEvent);
    }
    public void sendEventInvites(Event_Model groupEvent, List<User_Model> inviteList){
        mRepository.sendEventInvite(groupEvent,inviteList);
    }

    //ToDo: Currently checking the difference between addEventToUser and AccepteDeventInvitation
    public Task<Void> attendEvent(Event_Model event){
        return mRepository.addEventToUser(event);
    }

    public Task<Void> acceptEventInvitation(Event_Model event){
        return mRepository.acceptEventInvitation(event);
    }

    public Task<List<User_Model>> getEventResponses(Event_Model event, String status){
        return mRepository.getEventResponses(event, status);
    }
    public Task<List<User_Model>> getEventInvitees(Event_Model event){
        return mRepository.getEventInvitees(event);
    }
    //*------------------------------------------GROUPS------------------------------------------*//
    public void sendGroupInvites(Group_Model groupBase, List<User_Model> inviteList){
        mRepository.sendGroupInvites(groupBase,inviteList);
    }
    public Task<List<Group_Model>> fetchGroupInvites(){
        return mRepository.getGroupInvitations();
    }
    public Task<Void> createGroup(Group_Model groupBase, List<User_Model> inviteList){
        return mRepository.createGroup(groupBase, inviteList);
    }
    public Task<Group_Model> getGroup(){
        return mRepository.getGroup();
    }
    public Task<List<Task<Group_Model>>> getUsersGroups(){
        return mRepository.getUsersGroups();
    }
    public Task<Void> addUserToGroup(Group_Model group){
        return mRepository.addUserToGroup(group);
    }
    public Task<List<User_Model>> fetchGroupMembers(Group_Model group){
        return mRepository.fetchGroupMembers(group);
    }
    public void updateGroup(Group_Model groupBase){
        mRepository.editGroup(groupBase);
    }
    public Task<Void> declineGroupInvite(Group_Model group){
        return mRepository.declineGroupInvite(group);
    }
    //*-------------------------------------------ETC--------------------------------------------*//
    public Task<Void> addLocation(Address address,String addressName){
        return mRepository.addLocation(address,addressName);
    }
    public Task<byte[]> getImage(String uri){
        return mRepository.getImage(uri);
    }
    public Task<Uri> uploadImage(Uri uri, String name){
        return mRepository.uploadImage(uri, name);
    }
}

