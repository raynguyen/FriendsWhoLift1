package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

public class FirebaseUserViewModel extends ViewModel {
    private static final String TAG = "FirebaseUserViewModel";

    private FirebaseRepository mRepository;

    public FirebaseUserViewModel(){
        this.mRepository = new FirebaseRepository();
    }

    public void getGroups(MyGroupsFragment.FireStoreCallBack fireStoreCallBack){
        Log.i(TAG,"Inside getGroups method of FirebaseUserViewModel");
        mRepository.getGroups(fireStoreCallBack);
    }
}
