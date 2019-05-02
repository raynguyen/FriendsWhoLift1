package apps.raymond.kinect;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.location.Address;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.FireBaseRepo.FireBase_Repository;
import apps.raymond.kinect.Groups.GroupBase;
import apps.raymond.kinect.UserProfile.User_Model;

public class Repository_ViewModel extends ViewModel {
    private FireBase_Repository mRepository;

    public Repository_ViewModel(){
        this.mRepository = new FireBase_Repository();
    }

    public String getUserEmail(){
        return mRepository.getUserEmail();
    }

    public Task<AuthResult> emailSignIn(String email, String password){
        return mRepository.emailSignIn(email,password);
    }

    public Task<Void> signOut(Context context){
        return mRepository.signOut(context);
    }

    public Task<User_Model> getCurrentUser(){
        return mRepository.getCurrentUser();
    }

    public Task<Void> addUserConnection(User_Model user){
        return mRepository.addConnection(user);
    }

    public Task<List<User_Model>> getConnections(){
        return mRepository.getConnections();
    }

    public Task<List<String>> fetchInterests(){
        return mRepository.getInterests();
    }

    public Task<List<Event_Model>> fetchEventInvites(){
        return mRepository.getEventInvites();
    }

    public Task<Void> createEvent(Event_Model groupEvent){
        return mRepository.createEvent(groupEvent);
    }

    public void sendEventInvites(Event_Model groupEvent, List<User_Model> inviteList){
        mRepository.sendEventInvite(groupEvent,inviteList);
    }

    public void sendGroupInvites(GroupBase groupBase,List<User_Model> inviteList){
        mRepository.sendGroupInvites(groupBase,inviteList);
    }

    public Task<Void> updateEvent(Event_Model groupEvent){
        return mRepository.updateEvent(groupEvent);
    }

    public Task<Void> addUserToEvent(Event_Model groupEvent){
        return mRepository.addUserToEvent(groupEvent);
    }

    public Task<List<Task<DocumentSnapshot>>> getUsersEvents(){
        return mRepository.getUsersEvents();
    }

    public Task<List<User_Model>> getEventInvitees(Event_Model event){
        return mRepository.getEventInvitees(event);
    }

    public Task<List<User_Model>> getEventResponses(Event_Model event, String status){
        return mRepository.getEventResponses(event, status);
    }

    public Task<List<User_Model>> fetchUsers(){
        return mRepository.fetchUsers();
    }

    public Task<Void> createGroup(GroupBase groupBase, List<User_Model> inviteList){
        return mRepository.createGroup(groupBase, inviteList);
    }

    public Task<List<Task<GroupBase>>> getUsersGroups(){
        return mRepository.getUsersGroups();
    }

    public Task<GroupBase> getGroup(){
        return mRepository.getGroup();
    }

    public Task<Uri> uploadImage(Uri uri, String name){
        return mRepository.uploadImage(uri, name);
    }

    public Task<byte[]> getImage(String uri){
        return mRepository.getImage(uri);
    }

    public void updateGroup(GroupBase groupBase){
        mRepository.editGroup(groupBase);
    }

    public Task<List<GroupBase>> fetchGroupInvites(){
        return mRepository.getGroupInvites();
    }

    public Task<Void> addUserToGroup(GroupBase group){
        return mRepository.addUserToGroup(group);
    }

    public Task<List<User_Model>> fetchGroupMembers(GroupBase group){
        return mRepository.fetchGroupMembers(group);
    }

    public Task<Void> declineGroupInvite(GroupBase group){
        return mRepository.declineGroupInvite(group);
    }

    public Task<Void> addLocation(Address address,String addressName){
        return mRepository.addLocation(address,addressName);
    }

}

