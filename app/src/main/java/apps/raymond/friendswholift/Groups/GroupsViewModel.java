package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import apps.raymond.friendswholift.FireStoreClasses.FirebaseRepository;
import apps.raymond.friendswholift.Groups.MyGroupsFragment;

public class GroupsViewModel extends ViewModel {
    private static final String TAG = "GroupsViewModel";

    private FirebaseRepository mRepository;

    public GroupsViewModel(){
        this.mRepository = new FirebaseRepository();
    }

    public void getGroups(MyGroupsFragment.FireStoreCallBack fireStoreCallBack){
        Log.i(TAG,"Inside getGroups method of GroupsViewModel");
        mRepository.getGroups(fireStoreCallBack);
    }
}
