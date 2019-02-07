/*
 *Repository modules handle data operations. They provide a clean API so that the rest of the app
 * can retrieve this data easily. They know where to get the data from and what API calls to make
 * when data is updated. You can consider repositories to be mediators between different data
 * sources, such as persistent models, web services, and caches
*/

package apps.raymond.friendswholift.FireStoreClasses;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.Groups.GroupBase;

public class TestFirebaseRepository {

    private static final String TAG = "TestFirebaseRepository";
    private static final String GROUP_COLLECTION = "Groups";
    private static final String USER_COLLECTION = "Users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = db.collection(GROUP_COLLECTION);
    private CollectionReference userCollection = db.collection(USER_COLLECTION);
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private MutableLiveData<Set<String>> groupKeys;

    /*
     * Method call to create a Document under the 'Users' Collection. This collection will contain
     * details about the user as Fields in their respective document. The Document name is currently
     * created under the user's email.
     */
    public Task<Void> createUserDoc(String name){
        Log.i(TAG,"Creating a user Document under id: TESTID");
        Map<String,String> testMap = new HashMap<>();
        testMap.put("hello","test"); // ToDo need to figure out how to set a null field when creating a Document.
        return userCollection.document(name).set(testMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "User document successfully created.");
                        } else {
                            Log.w(TAG, "Failed to create document.");
                        }
                    }
                });
    }

    /*
     * Creates a Document under the 'Groups' Collection. The fields of this document are saved as a
     * GroupBase POJO.
     */
    public Task<Void> createGroupDoc(String name, GroupBase groupBase){
        Log.i(TAG,"Creating Group Document: " + name);
        return groupCollection.document(name).set(groupBase)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "User document successfully created.");
                        } else {
                            Log.w(TAG, "Failed to create document.");
                        }
                    }
                });
    }


    /*
     * Collects the fields of a User document and returns them as a Set<String>.
     * The actual method call returns a Task to gather the fields of our Document. Once this task is
     * complete, we can utilise the data as needed (refer to the calling Context for
     * onCompleteListener).
     * Current version of the database only stores the User's attached groups in the form of a
     * <String> GroupName : <String> Authorization pair.
     */
    public Task<Set<String>> getGroupTags(){
        return userCollection.document(currentUser.getEmail()).get()
                .continueWith(new Continuation<DocumentSnapshot, Set<String>>() {
                    @Override
                    public Set<String> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            Log.i(TAG,"Successfully retrieved User document.");
                            return document.getData().keySet();
                        } else {
                            Log.w(TAG, "Error when retrieving the Document.");
                            return null;
                        }
                    }
                });
    }


    //this one works.
    public Task<List<Task<DocumentSnapshot>>> testMethod1(){
        //First thing to do is retrieve the Group tags from current user.
        return userCollection.document(currentUser.getEmail()).get()
                .continueWith(new Continuation<DocumentSnapshot, Set<String>>() {
                    @Override
                    public Set<String> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                        Log.i(TAG,"Fetching the Set containing our Group tags");
                        DocumentSnapshot document = task.getResult();
                        if(document!=null){
                            Log.i(TAG,"Successfully retrieved Group key set.");
                            return task.getResult().getData().keySet();
                        } else {
                            Log.i(TAG,"Document for user does not exist.");
                            return null;
                        }
                    }
                })
                //Next, using the key set, we want to retrieve the groups from the Group collection.
                .continueWith(new Continuation<Set<String>, List<Task<DocumentSnapshot>>>() {
                    @Override
                    public List<Task<DocumentSnapshot>> then(@NonNull Task<Set<String>> task) throws Exception {
                        List<Task<DocumentSnapshot>> fetchGroupsTaskList = new ArrayList<>();
                        Log.i(TAG,"Fetching the Group Documents for these groups: " + task.getResult().toString());
                        List<String> keyList = new ArrayList<>(task.getResult());
                        for(String key : keyList){
                            Log.i(TAG,"Creating Task to fetch Group Document: "+ key);
                            fetchGroupsTaskList.add(groupCollection.document(key).get());
                        }
                        return fetchGroupsTaskList;
                    }
                });

    }

    /*
     * Query Firestore against a List of names. For each name in the list, retrieve the POJO of that
     * document and append it to a List that will be returned to the context Caller.
     */
    public List<Task<DocumentSnapshot>> getGroupsOld(List<String> myGroupTags){
        // Consider moving the following List<String> manipulation to the Fragment or??
        List<Task<DocumentSnapshot>> myTasks = new ArrayList<>();
        for(String name : myGroupTags){
            // We don't need OnCompleteListener because it will be implemented in the calling context.
            Task<DocumentSnapshot> snapshot = groupCollection.document(name).get();
            myTasks.add(snapshot);
        }
        return myTasks;
    }

    public List<Task<byte[]>> getPhotos(List<String> myGroupTags){
        List<Task<byte[]>> myPhotos = new ArrayList<>();
        for(String name : myGroupTags){
            // We don't need OnCompleteListener because it will be implemented in the calling context.
            Task<byte[]> photo = storageRef.child("TestGroup1/"+name+".jpg").getBytes(1024*1024);
            myPhotos.add(photo);
        }
        return myPhotos;
    }

    public void addEventToGroup(String groupName, GroupEvent groupEvent){
        groupCollection.document(groupName).set(groupEvent, SetOptions.merge())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG,"Event added to Group Document.");
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error adding event to Group Document.",e);
            }
        });
    }
}
