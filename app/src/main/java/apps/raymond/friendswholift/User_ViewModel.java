package apps.raymond.friendswholift;

import com.google.android.gms.tasks.Task;

public class User_ViewModel {

    private FirebaseRepository mRepository;

    public User_ViewModel(){
        this.mRepository = new FirebaseRepository();
    }

    public Task<Void> createConnection(){
        return mRepository.createConnection();
    }
}
