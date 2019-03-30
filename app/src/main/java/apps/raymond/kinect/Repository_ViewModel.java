package apps.raymond.kinect;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Groups.GroupBase;
import apps.raymond.kinect.UserProfile.UserModel;

public class Repository_ViewModel extends ViewModel {
    private FireBaseRepository mRepository;

    public Repository_ViewModel(){
        this.mRepository = new FireBaseRepository();
    }

    public Task<AuthResult> signIn(String email, String password){
        return mRepository.signInWithEmail(email,password);
    }

    public Task<UserModel> getCurrentUser(){
        return mRepository.getCurrentUser();
    }

    public Task<Void> addUserConnection(UserModel user){
        return mRepository.addConnection(user);
    }

    public Task<List<UserModel>> fetchConnections(){
        return mRepository.getConnections();
    }

    public Task<List<String>> fetchInterests(){
        return mRepository.getInterests();
    }

    public Task<List<Event_Model>> fetchEventInvites(){
        return mRepository.fetchEventInvites();
    }

    public Task<Void> createEvent(Event_Model groupEvent){
        return mRepository.createEvent(groupEvent);
    }

    public void sendEventInvites(Event_Model groupEvent, List<UserModel> inviteList){
        mRepository.sendEventInvite(groupEvent,inviteList);
    }

    public void sendGroupInvites(GroupBase groupBase,List<UserModel> inviteList){
        mRepository.sendGroupInvites(groupBase,inviteList);
    }

    public Task<Void> updateEvent(Event_Model groupEvent){
        return mRepository.updateEvent(groupEvent);
    }

    public Task<Void> addUserToEvent(Event_Model groupEvent){
        return mRepository.addUserToEvent(groupEvent);
    }

    Task<List<Task<DocumentSnapshot>>> getUsersEvents(){
        return mRepository.getUsersEvents();
    }

    public Task<List<UserModel>> getEventInvitees(Event_Model event){
        return mRepository.getEventInvitees(event);
    }

    public Task<List<UserModel>> getEventResponses(Event_Model event, String status){
        return mRepository.getEventResponses(event, status);
    }

    public Task<List<UserModel>> fetchUsers(){
        return mRepository.fetchUsers();
    }

    public Task<Void> createGroup(GroupBase groupBase, List<UserModel> inviteList){
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
        return mRepository.fetchGroupInvites();
    }

    public Task<Void> addUserToGroup(GroupBase group){
        return mRepository.addUserToGroup(group);
    }

    public Task<List<UserModel>> fetchGroupMembers(GroupBase group){
        return mRepository.fetchGroupMembers(group);
    }
}

