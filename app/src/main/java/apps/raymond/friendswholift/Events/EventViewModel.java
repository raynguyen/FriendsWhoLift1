package apps.raymond.friendswholift.Events;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import apps.raymond.friendswholift.FireStoreClasses.TestFirebaseRepository;

public class EventViewModel extends ViewModel {
    private TestFirebaseRepository mRepository;

    public EventViewModel(){
        this.mRepository = new TestFirebaseRepository();
    }

    public void createEvent(GroupEvent groupEvent){
        mRepository.createEvent(groupEvent);
    }

    public Task<DocumentSnapshot> addEventToUser(String eventName){
        return mRepository.addEventToUser(eventName);
    }

    public Task<List<Task<DocumentSnapshot>>> getUsersEvents(){
        return mRepository.getUsersEvents();
    }

    public Task<DocumentSnapshot> attachListener(){
        return mRepository.listenToUsersEvents();
    }

    public void removeListener(){
        mRepository.removeListener();
    }
}
