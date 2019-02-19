package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import apps.raymond.friendswholift.FireStoreClasses.TestFirebaseRepository;

public class GroupsViewModel extends ViewModel {

    private TestFirebaseRepository mRepository;

    public GroupsViewModel(){
        this.mRepository = new TestFirebaseRepository();
    }

    void createGroup(GroupBase groupBase){
        mRepository.createGroup(groupBase);
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
}
