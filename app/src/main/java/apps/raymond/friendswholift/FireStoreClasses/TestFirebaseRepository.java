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

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.Groups.GroupBase;

public class TestFirebaseRepository {

    private static final String TAG = "TestFirebaseRepository";
    private static final String GROUP_COLLECTION = "Groups";
    private static final String USER_COLLECTION = "Users";
    private static final String EVENT_COLLECTION = "Events";

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = db.collection(GROUP_COLLECTION);
    private CollectionReference userCollection = db.collection(USER_COLLECTION);
    private CollectionReference eventCollection = db.collection(EVENT_COLLECTION);

    // When a state change is detected, we need to launch a new activity but not sure how to do that without direct communication from Repository back to the Activity.
    public void attachAuthListener(){
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(currentUser == null){
                    Log.i(TAG,"There is no active user.");
                } else {
                    Log.i(TAG,"Current user is: "+currentUser.getEmail());
                }
            }
        });
    }

    public Task<Void> createUser(final Context context, final String name, final String password){
        Log.i(TAG,"Creating new user "+ name);
        return firebaseAuth.createUserWithEmailAndPassword(name,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.i(TAG,"Successfully registered user "+ name);
                        Toast.makeText(context,"Successfully registered "+name,Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Failed to register "+ name,e);
                        Toast.makeText(context,"Error registering "+name,Toast.LENGTH_SHORT).show();
                    }
                }
        ).continueWithTask(new Continuation<AuthResult, Task<AuthResult>>() {
            @Override
            public Task<AuthResult> then(@NonNull Task<AuthResult> task) throws Exception {
                return signInWithEmail(name,password);
            }
        }).continueWithTask(new Continuation<AuthResult, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<AuthResult> task) throws Exception {
                return createUserDoc(name);
            }
        });
    }

    public Task<AuthResult> signInWithEmail(final String name, String password){
        return firebaseAuth.signInWithEmailAndPassword(name,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.i(TAG,"Signed in as "+name);
                        }
                    }
                });
    }

    /*
     * Method call to create a Document under the 'Users' Collection. This collection will contain
     * details about the user as Fields in their respective document. The Document name is currently
     * created under the user's email.
     *
     * Need to check if the Document exists when trying to query its contents.
     */
    public Task<Void> createUserDoc(final String name){
        Log.i(TAG,"Creating new user Document "+ name);
        Map<String,String> testMap = new HashMap<>();
        testMap.put("hello","test"); // ToDo need to figure out how to set a null field when creating a Document.
        return userCollection.document(name).set(testMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i(TAG, "Created document for user "+name);
                        } else {
                            Log.w(TAG, "Failed to create document for "+name);
                        }
                    }
                });
    }

    /*
     * Call when creating a new GroupBase object and store the details on FireStore. When storing
     * the object for the first time, we attach a tag to User's document. This tag is used to read
     * the groups connected to the user.
     * Logic:
     * Create the Group Document first and if successful, move to adding the tag to the User.
     */
    public void createGroup(final GroupBase groupBase){
        final Map<String,String> holderMap = new HashMap<>();
        holderMap.put("Access","Owner");

        groupCollection.document(groupBase.getName()).set(groupBase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG,"Successfully stored the Group under: "+groupBase.getName());
                    }
                }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                if(task.isSuccessful()){
                    return userCollection.document(currentUser.getEmail()).collection(GROUP_COLLECTION)
                            .document(groupBase.getName()).set(holderMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG,"Attached "+groupBase.getName()+" to " +currentUser.getEmail());
                                }
                            });
                } else {
                    Log.i(TAG,"Unable to create the Group.");
                    return null;
                }
            }
        });
        userCollection.document(currentUser.getEmail()).collection("Groups")
                .document(groupBase.getName()).set(holderMap) //Change the .set of this line to the POJO if we want to store the POJO here instead of using tag reference.
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void task) {
                        Log.i(TAG,"New Group Document in user's Group sub-collection: " + groupBase.getName());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Error creating document in Groups sub-collection.",e);
                    }
                })
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        return groupCollection.document(groupBase.getName()).set(groupBase)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void task) {
                                        Log.i(TAG,"Created group document "+groupBase.getName());
                                    }
                                });
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

    // Info below may be outdated.
    // Steps are to get the keySet corresponding to what groups the user is registered with
    // Then we want to retrieve the Documentsnapshots of the groups named in the KeySet.
    // Then we read the imageURI field to get the photo storage reference and download the photo
    // Finally we want to use the documentsnapshot and the retrieved byte[] to create a groupbase object.
    public Task<List<Task<GroupBase>>> getUsersGroups(){
        final List<String> groupList = new ArrayList<>();
        //First thing to do is retrieve the Group tags from current user.
        return userCollection.document(currentUser.getEmail()).collection("Groups").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           if (task.isSuccessful()) {
                               Log.i(TAG,"The user is a part of "+task.getResult().toString());
                               for (QueryDocumentSnapshot document : task.getResult()) {
                                   Log.i(TAG, "Adding to GroupBase query list: " + document.getId());
                                   groupList.add(document.getId());
                               }
                           }
                       }
                }).continueWith(new Continuation<QuerySnapshot, List<Task<GroupBase>>>() {
                    @Override
                    public List<Task<GroupBase>> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        List<Task<GroupBase>> taskList = new ArrayList<>();
                        for(final String group:groupList){
                            Log.i(TAG,"Fetching document "+group);
                            taskList.add(groupCollection.document(group).get().continueWith(new Continuation<DocumentSnapshot, GroupBase>() {
                                @Override
                                public GroupBase then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                                    Log.i(TAG,"Converting document to GroupBase: "+group);
                                    return task.getResult().toObject(GroupBase.class);
                                }
                            }));
                        }
                        return taskList;
                    }
                });
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

    public void removeListener(){
    }

}
