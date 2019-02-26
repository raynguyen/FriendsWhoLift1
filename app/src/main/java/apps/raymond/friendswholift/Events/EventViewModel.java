/*
 * ViewModel that is used by the MainActivity and its Fragments.
 * ToDo:
 * Need to determine if there is a method of instancing one ViewModel in the Activity that the
 * Fragments call when they need to interact with FireStore.
 *
 * RENAME THIS VIEWMODEL
 */

package apps.raymond.friendswholift.Events;

import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import apps.raymond.friendswholift.TestFirebaseRepository;

public class EventViewModel extends ViewModel {
    private TestFirebaseRepository mRepository;

    public EventViewModel(){
        this.mRepository = new TestFirebaseRepository();
    }

    public void createEvent(GroupEvent groupEvent){
        mRepository.createEvent(groupEvent);
    }

    public void attachAuthListener(){
        mRepository.attachAuthListener();
    }

    public Task<DocumentSnapshot> addEventToUser(String eventName){
        return mRepository.addEventToUser(eventName);
    }

    public Task<List<Task<DocumentSnapshot>>> getUsersEvents(){
        return mRepository.getUsersEvents();
    }

    public Task<DocumentSnapshot> attachListener(){
        //return mRepository.listenToUsersEvents();
        return null;
    }

    public void removeListener(){
        mRepository.removeListener();
    }
}
