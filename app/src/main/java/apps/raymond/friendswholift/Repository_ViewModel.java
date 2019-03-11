package apps.raymond.friendswholift;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.Groups.GroupBase;
import apps.raymond.friendswholift.UserProfile.UserModel;

public class Repository_ViewModel extends ViewModel {
    private FireBaseRepository mRepository;

    public Repository_ViewModel(){
        this.mRepository = new FireBaseRepository();
    }

    public Task<Void> createEvent(GroupEvent groupEvent, List<UserModel> inviteList){
        return mRepository.createEvent(groupEvent, inviteList);
    }

    public void sendEventInvites(GroupEvent groupEvent,List<UserModel> inviteList){
        mRepository.sendEventInvite(groupEvent,inviteList);
    }

    public void sendGroupInvites(GroupBase groupBase,List<UserModel> inviteList){
        mRepository.sendGroupInvites(groupBase,inviteList);
    }

    public Task<Void> updateEvent(GroupEvent groupEvent){
        return mRepository.updateEvent(groupEvent);
    }

    public Task<List<Task<DocumentSnapshot>>> getUsersEvents(){
        return mRepository.getUsersEvents();
    }

    public Task<List<String>> getEventInvitees(GroupEvent event){
        return mRepository.getEventInvitees(event);
    }

    public Task<List<String>> getEventResponses(GroupEvent event, String status){
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
        mRepository.updateGroup(groupBase);
    }

}

