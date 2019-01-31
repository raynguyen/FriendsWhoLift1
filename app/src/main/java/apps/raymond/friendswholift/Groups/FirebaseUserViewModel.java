package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModel;

import java.util.Set;

public class FirebaseUserViewModel extends ViewModel {
    private static final String TAG = "FirebaseUserViewModel";

    private FirebaseRepository mRepository;

    public FirebaseUserViewModel(){
        this.mRepository = new FirebaseRepository();
    }

    public Set<String> getGroups(){
        return mRepository.getGroups();
    }
}
