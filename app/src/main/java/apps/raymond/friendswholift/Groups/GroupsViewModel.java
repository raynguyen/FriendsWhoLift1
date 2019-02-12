package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;

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
        return mRepository.getUsersGroupsTest();
    }

    public Task<GroupBase> getGroup(){
        return mRepository.getGroup();
    }
}
