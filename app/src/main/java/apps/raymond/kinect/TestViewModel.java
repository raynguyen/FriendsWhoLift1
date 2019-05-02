package apps.raymond.kinect;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.FireBaseRepo.FireBase_RepositoryTest;
import apps.raymond.kinect.UserProfile.User_Model;

public class TestViewModel extends ViewModel {

    private FireBase_RepositoryTest mRepo = new FireBase_RepositoryTest();
    private LiveData<User_Model> currentUser;

    /**
     * The viewmodel should house the current user model as a User_Model object. The hosting activity
     * will observe this LiveData field and on change will trigger a sign in intent and clear the
     * activity stack.
     */
    public TestViewModel(){

    }

    public void getCurrentUser(){
        Task<DocumentSnapshot> getUserTask = mRepo.getCurrentUser();
        getUserTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                }
            }
        });
    }

    public void createEvent(Event_Model event){
        Task<Void> createEventTask = mRepo.createEvent(event);
        createEventTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void addEventToUser(Event_Model event, User_Model user){}

    public void addUserToEvent(User_Model user, Event_Model event){}

    public void removeUserFromEvent(User_Model user, Event_Model event){}

    public void editEvent(Event_Model event){}

    public void deleteEvent(Event_Model event){}



}
