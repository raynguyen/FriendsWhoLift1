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

import apps.raymond.friendswholift.FireBaseRepository;
import apps.raymond.friendswholift.UserProfile.UserModel;

public class EventViewModel extends ViewModel {
    private FireBaseRepository mRepository;

    public EventViewModel(){
        this.mRepository = new FireBaseRepository();
    }

    public Task<Void> createEvent(GroupEvent groupEvent, List<UserModel> inviteList){
        return mRepository.createEvent(groupEvent, inviteList);
    }

    public List<Task<Void>> sendInvites(GroupEvent groupEvent, List<UserModel> inviteList){
        return mRepository.sendEventInvites(groupEvent,inviteList);
    }

    public Task<Void> updateEvent(GroupEvent groupEvent){
        return mRepository.updateEvent(groupEvent);
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

    public Task<List<UserModel>> fetchUsers(){
        return mRepository.fetchUsers();
    }
}
