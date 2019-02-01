package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import apps.raymond.friendswholift.FireStoreClasses.TestFirebaseRepository;

public class GroupsViewModel extends ViewModel {
    private static final String TAG = "GroupsViewModel";

    private TestFirebaseRepository mRepository;

    public GroupsViewModel(){
        this.mRepository = new TestFirebaseRepository();
    }

    public void createGroup(String name, GroupBase groupBase){
        Log.i(TAG,"Inside createGroup method of GroupsViewModel");
        mRepository.createGroupDoc(name, groupBase); //Requires a string that will be the name of the Document
    }
}
