package apps.raymond.kinect;

import com.google.android.gms.tasks.Task;

public class User_ViewModel {

    private FireBaseRepository mRepository;

    public User_ViewModel(){
        this.mRepository = new FireBaseRepository();
    }

    public Task<Void> createConnection(){
        return mRepository.createConnection();
    }
}
