package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Set;

import apps.raymond.friendswholift.FireStoreClasses.TestFirebaseRepository;

public class GroupsViewModel extends ViewModel {
    private static final String TAG = "GroupsViewModel";

    private TestFirebaseRepository mRepository;

    public GroupsViewModel(){
        this.mRepository = new TestFirebaseRepository();
    }

    public void createGroup(String name, GroupBase groupBase){
        mRepository.createGroupDoc(name, groupBase); //Requires a string that will be the name of the Document
    }

    public Task<Set<String>> getGroupTags(){
        return mRepository.getGroupTags();
    }

    public Task<List<GroupBase>> getGroups(List<String> myGroupTags){
        return mRepository.getGroups(myGroupTags);

    }
}
