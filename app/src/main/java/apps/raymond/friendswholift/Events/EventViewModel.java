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

import apps.raymond.friendswholift.FirebaseRepository;

public class EventViewModel extends ViewModel {
    private FirebaseRepository mRepository;

    public EventViewModel(){
        this.mRepository = new FirebaseRepository();
    }

    public Task<Void> createEvent(GroupEvent groupEvent){
        return mRepository.createEvent(groupEvent);
    }

    public Task<List<Task<DocumentSnapshot>>> getUsersEvents(){
        return mRepository.getUsersEvents();
    }

    public Task<List<String>> getEventInvitees(GroupEvent event){
        return mRepository.getEventInvitees(event);
    }

    public Task<List<String>> getEventResponses(GroupEvent event, String status){
        return mRepository.getEventResponses(event, status);
    }

}
