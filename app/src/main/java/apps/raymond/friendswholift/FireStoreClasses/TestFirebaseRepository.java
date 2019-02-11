/*
 * There appears to be an error where if there is no photo, the recycler view does not retrieve all the group objects.
 *
 * There appears to be an error where onSuccessListeners will trigger even if there is no collection or document of the requested name.
 * --NOTE: When we create a Task to retrieve documents from Firestore, if the document or the document path does not exist,
 * it will return a NULL document but will still SUCCEED. This is why the onSuccessListener is triggered.
 *
 * Repository modules handle data operations. They provide a clean API so that the rest of the app
 * can retrieve this data easily. They know where to get the data from and what API calls to make
 * when data is updated. You can consider repositories to be mediators between different data
 * sources, such as persistent models, web services, and caches
*/

package apps.raymond.friendswholift.FireStoreClasses;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.acl.Group;
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
    private static final String EVENT_COLLECTION = "Events";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = db.collection(GROUP_COLLECTION);
    private CollectionReference userCollection = db.collection(USER_COLLECTION);
    private CollectionReference eventCollection = db.collection(EVENT_COLLECTION);
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
    /*
     * ToDo: We are moving the user's groups from Fields of the document to a Collection.
     */
    public Task<List<Task<DocumentSnapshot>>> getUsersGroups(){
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
                            Log.i(TAG,"Creating task to fetch Group Document: "+ key);
                            fetchGroupsTaskList.add(groupCollection.document(key).get());
                        }
                        return fetchGroupsTaskList;
                    }
                });

    }

    //Want to return a Task<List<GroupBase>>
    // Steps are to get the keySet corresponding to what groups the user is registered with
    // Then we want to retrieve the Documentsnapshots of the groups named in the KeySet.
    // Then we read the imageURI field to get the photo storage reference and download the photo
    // Finally we want to use the documentsnapshot and the retrieved byte[] to create a groupbase object.

    public Task<List<Task<GroupBase>>> getUsersGroupsTest(){
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
                }).continueWith(new Continuation<Set<String>, List<Task<GroupBase>>>() {
                    @Override
                    public List<Task<GroupBase>> then(@NonNull Task<Set<String>> task) throws Exception {
                        Log.i(TAG,"Fetching the Group Documents for these groups: " + task.getResult().toString());
                        List<String> keyList = new ArrayList<>(task.getResult());
                        List<Task<GroupBase>> fetchGroupBases = new ArrayList<>();
                        for(final String key : keyList){
                            Log.i(TAG,"Creating task to fetch "+ key+" and convert to GroupBase object.");
                            fetchGroupBases.add(groupCollection.document(key).get().continueWith(new Continuation<DocumentSnapshot, GroupBase>() {
                                @Override
                                public GroupBase then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                                    Log.i(TAG,"Retrieving photo via URI of snapshot.");
                                    final GroupBase groupBase = task.getResult().toObject(GroupBase.class);
                                    String imageURI = task.getResult().get("imageURI").toString();
                                    if(!imageURI.isEmpty()) {
                                        Log.i(TAG,"There is an imageURI for this document.");
                                        firebaseStorage.getReferenceFromUrl(imageURI).getBytes(1024*1024*5)
                                                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<byte[]> task) {
                                                        groupBase.setByteArray(task.getResult());
                                                    }
                                                });
                                    }
                                    Log.i(TAG,"Got to this point.");
                                    return groupBase;
                                }
                            }));
                        }
                        return fetchGroupBases;
                    }
                });
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

    /*
     * Adds a GroupEvent POJO to a Document with the POJO's name.
     */
    public void createEvent(final GroupEvent groupEvent){
        eventCollection.document(groupEvent.getName()).set(groupEvent, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.i(TAG,"Added event " + groupEvent.getName() + " to the Events.");
                        } else {
                            Log.w(TAG,"Unable to add Event to the Events collection.");
                        }
                    }
                }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                if(task.isSuccessful()){
                    addEventToUser(groupEvent.getName());
                } else {
                    Log.w(TAG,"Unable to store the Event in the Events collection. ");
                }
                return null;
            }
        });
    }

    public Task<DocumentSnapshot> addEventToUser(String eventName){
        Log.i(TAG,"Adding event to User Document.");
        final DocumentReference docRef = userCollection.document(currentUser.getEmail()).collection("Events").document(eventName);
        Map<String,String> testMap = new HashMap<>();
        testMap.put("Access","Owner");
        return docRef.set(testMap, SetOptions.merge())
                .continueWithTask(new Continuation<Void, Task<DocumentSnapshot>>() {
                @Override
                public Task<DocumentSnapshot> then(@NonNull Task<Void> task) throws Exception {
                    if(task.isSuccessful()){
                        return docRef.get();
                    }
                    return null;
                }
        });
    }

    /*
     * Called when we want to retrieve GroupEvent objects that are listed under the User's groups.
     * This method does not update the adapter when there is a change in the data.
     */
    public Task<List<Task<DocumentSnapshot>>> getUsersEvents(){
        // First task retrieves a list of the Groups from the User Document.
        return userCollection.document(currentUser.getEmail()).collection("Events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document:task.getResult()){
                            Log.i(TAG,"User's events include: "+document.getId());
                        }
                    }
                })
                // Second task will return a List of tasks to retrieve a document pertaining to each document retrieved from the first task.
                .continueWith(new Continuation<QuerySnapshot, List<Task<DocumentSnapshot>>>() {
                    @Override
                    public List<Task<DocumentSnapshot>> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        List<Task<DocumentSnapshot>> groupEvents = new ArrayList<>();
                        for(QueryDocumentSnapshot document:task.getResult()){
                            Task<DocumentSnapshot> myEvent = eventCollection.document(document.getId()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            //Convert to POJO OBJECT HERE?
                                            Log.i(TAG,"Retrieved this document from the task: "+documentSnapshot.toString());
                                        }
                                    });
                            groupEvents.add(myEvent);
                        }
                        return groupEvents;
                    }
                });
    }

    public Task<GroupBase> getGroup(){

        return null;
    }

    // Not sure how to get this to pass on an action to the fragment (i.e. onEvent trigger update the RecyclerView).
    public Task<DocumentSnapshot> listenToUsersEvents(){
        Log.i(TAG,"Adding a listener to the User's events.");
        Query eventsQuery = userCollection.document(currentUser.getEmail()).collection("Events");

        ListenerRegistration eventListener = eventsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                Log.i(TAG,"There was an event.");
                for(QueryDocumentSnapshot document:queryDocumentSnapshots){
                    Log.i(TAG,"The collection contains: " + document.getId());
                }
            }
        });
        return null;
    }

    public void removeListener(){
    }

}
