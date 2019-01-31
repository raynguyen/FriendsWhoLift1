package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.Set;

public class FirebaseUserViewModel extends ViewModel {
    private static final String TAG = "FirebaseUserViewModel";



    public FirebaseUserViewModel(){
        this.mRepository = new FirebaseRepository();
    }
    private FirebaseRepository mRepository;
    public void getGroups(TestGroupGetFragment.FirestoreCallBack firestoreCallBack){
        Log.i(TAG,"Inside getGroups method of FirebaseUserViewModel");
        mRepository.getGroups(firestoreCallBack);
    }
}
