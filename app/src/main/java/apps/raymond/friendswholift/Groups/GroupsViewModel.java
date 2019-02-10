package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import apps.raymond.friendswholift.FireStoreClasses.TestFirebaseRepository;

public class GroupsViewModel extends ViewModel {

    private TestFirebaseRepository mRepository;

    GroupsViewModel(){
        this.mRepository = new TestFirebaseRepository();
    }

    void createGroup(String name, GroupBase groupBase){
        mRepository.createGroupDoc(name, groupBase); //Requires a string that will be the name of the Document
    }

    public List<Task<byte[]>> getPhotos(List<String> myGroupTags){
        return mRepository.getPhotos(myGroupTags);
    }

    Task<List<Task<DocumentSnapshot>>> getUsersGroups(){
        return mRepository.getUsersGroups();
    }

    Task<List<Task<GroupBase>>> getUsersGroupsTest(){
        return mRepository.getUsersGroupsTest();
    }

    public Task<GroupBase> getGroup(){
        return mRepository.getGroup();
    }
}
