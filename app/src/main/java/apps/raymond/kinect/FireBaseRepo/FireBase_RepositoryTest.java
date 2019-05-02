package apps.raymond.kinect.FireBaseRepo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import apps.raymond.kinect.Events.Event_Model;

public class FireBase_RepositoryTest {
    private static final String GROUPS = "Groups";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    public Task<DocumentSnapshot> getCurrentUser(){
        //ToDo: There is currently no safety to reinforce that all users will have an attached email.
        String userEmail = mAuth.getCurrentUser().getEmail();
        return mStore.collection("Users").document(userEmail).get();
    }

    public Task<Void> createEvent(Event_Model event){
        String eventName = event.getName();
        return mStore.collection("Events").document(eventName).set(event);
    }
}
