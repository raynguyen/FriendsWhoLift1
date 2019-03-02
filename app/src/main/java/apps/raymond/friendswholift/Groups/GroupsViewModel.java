package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;

import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.friendswholift.FirebaseRepository;

public class GroupsViewModel extends ViewModel {

    private FirebaseRepository mRepository;

    public GroupsViewModel(){
        this.mRepository = new FirebaseRepository();
    }

    Task<Void> createGroup(GroupBase groupBase){
        return mRepository.createGroup(groupBase);
    }

    Task<List<Task<GroupBase>>> getUsersGroups(){
        return mRepository.getUsersGroups();
    }

    public Task<GroupBase> getGroup(){
        return mRepository.getGroup();
    }

    public Task<Uri> uploadImage(Uri uri,String name){
        return mRepository.uploadImage(uri, name);
    }

    public Task<byte[]> getImage(String uri){
        return mRepository.getImage(uri);
    }

    public void updateGroup(GroupBase groupBase){
        mRepository.updateGroup(groupBase);
    }
}
