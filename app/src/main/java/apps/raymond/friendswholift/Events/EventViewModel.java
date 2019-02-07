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

    public Task<DocumentSnapshot> addEventToGroup(String groupName,GroupEvent groupEvent){
        return mRepository.addEventToGroup(groupName,groupEvent);
    }

    public Task<DocumentSnapshot> addEventToUser(String eventName){
        return mRepository.addEventToUser(eventName);
    }
    /*
    public Task<List<GroupEvent>> getEvents(){
        return mRepository.getEvents();
    }*/
}
