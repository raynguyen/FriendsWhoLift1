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

package apps.raymond.friendswholift;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
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
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.Groups.GroupBase;
import apps.raymond.friendswholift.UserProfile.UserModel;

public class FireBaseRepository {

    private static final String TAG = "FireBaseRepository";
    private static final String GROUP_COLLECTION = "Groups";
    private static final String USER_COLLECTION = "Users";
    private static final String EVENT_COLLECTION = "Events";
    private static final String CONNECTIONS = "Connections";
    private static final String INVITEES = "Invitees";
    private static final String EVENT_INVITES = "EventInvites";
    private static final String GROUP_INVITES = "GroupInvites";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = db.collection(GROUP_COLLECTION);
    private CollectionReference userCollection = db.collection(USER_COLLECTION);
    private CollectionReference eventCollection = db.collection(EVENT_COLLECTION);

    private String userEmail;

    // ToDo: Auth listener here to do things.
    public FireBaseRepository(){
        try{
            this.userEmail = currentUser.getEmail();
        }catch (NullPointerException npe){
            Log.w(TAG,"Error: "+ npe);
        }
    }

    // When a state change is detected, we need to launch a new activity but not sure how to do that without direct communication from Repository back to the Activity.
    public void attachAuthListener(){
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(currentUser == null){
                    Log.i(TAG,"There is no active user.");
                } else {
                    Log.i(TAG,"Current user is: "+userEmail);
                }
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

    public Task<Void> createUserByEmail(final UserModel userModel, final String password){
        final String name = userModel.getEmail();
        Log.i(TAG,"Creating new user "+ name);
        return firebaseAuth.createUserWithEmailAndPassword(name,password)
                .continueWithTask(new Continuation<AuthResult, Task<AuthResult>>() {
                    @Override
                    public Task<AuthResult> then(@NonNull Task<AuthResult> task) throws Exception {
                        return signInWithEmail(name,password);
                    }
                }).continueWithTask(new Continuation<AuthResult, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<AuthResult> task) throws Exception {
                        return createUserDoc(userModel);
                    }
                });
    }

    private Task<Void> createUserDoc(UserModel userModel){
        final String name = userModel.getEmail();
        Log.i(TAG,"Creating new user Document "+ name);
        return userCollection.document(name).set(userModel)
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
     * Called when we want to retrieve GroupEvent objects that are listed under the User's groups.
     * This method does not update the adapter when there is a change in the data.
     */
    public Task<List<Task<DocumentSnapshot>>> getUsersEvents(){
        // First task retrieves a list of the Groups from the User Document.
        return userCollection.document(userEmail).collection("Events").get()
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

    /*
     * Listens to the Invite folders for the current user. On snapshot, we want to throw a dialog up
     * for the user to accept or decline the invitation, or close it.
     */
    ListenerRegistration eventInviteListener, groupInviteListener;
    public void attachInviteListener(){
        CollectionReference eventMessages = userCollection.document(userEmail).collection(EVENT_INVITES);
        CollectionReference groupMessages = userCollection.document(userEmail).collection(GROUP_INVITES);

        eventInviteListener = eventMessages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.w(TAG,"Event listener error: "+e);
                    return;
                }
                for(DocumentChange change : queryDocumentSnapshots.getDocumentChanges()){
                    switch (change.getType()){
                        case ADDED:
                            Log.i(TAG,"Event invite received: "+change.getDocument().getId());
                            break;
                        case REMOVED:
                            break;
                        case MODIFIED:
                            break;
                    }
                }
            }
        });

        groupInviteListener = groupMessages.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.w(TAG,"Group listener error: "+e);
                    return;
                }
                for(DocumentChange change : queryDocumentSnapshots.getDocumentChanges()){
                    switch (change.getType()){
                        case ADDED:
                            Log.i(TAG,"Group invite received: "+change.getDocument().getId());
                            break;
                        case REMOVED:
                            break;
                        case MODIFIED:
                            break;
                    }
                }
            }
        });
        // Add the documents to a list that is passed back to the activity via interface?
    }

    public void removeInviteListeners(){
        eventInviteListener.remove();
        groupInviteListener.remove();
    }


    /*
     * Call when creating a new GroupBase object and store the details on FireStore. When storing
     * the object for the first time, we attach a tag to User's document. This tag is used to read
     * the groups connected to the user.
     * Logic:
     * Create the Group Document first and if successful, move to adding the tag to the User.
     */
    public Task<Void> createGroup(final GroupBase groupBase,final List<UserModel> inviteList){
        final DocumentReference groupDoc = groupCollection.document(groupBase.getOriginalName());
        final Map<String,String> holderMap = new HashMap<>();
        holderMap.put("Access","Owner");
        // Create a document from the GroupBase object under the creation name.
        return groupDoc.set(groupBase)
                .continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(@NonNull Task<Void> task) throws Exception {
                        Log.i(TAG,"Creating invitee sub collection.");
                        if(task.isSuccessful()){
                            for(UserModel user : inviteList){
                                groupDoc.collection("Invitees").document(user.getEmail()).set(user);
                            }
                        } else {
                            Log.i(TAG,"Error creating invitee sub collection.");
                            //return null;
                        }
                        return null;
                    }
                })
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if(task.isSuccessful()){
                            Log.i(TAG,"Successfully stored the Group under: "+groupBase.getName());
                            return userCollection.document(userEmail).collection(GROUP_COLLECTION)
                                    .document(groupBase.getName()).set(holderMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(TAG,"Attached "+groupBase.getName()+" to " +userEmail);
                                        }
                                    });
                        } else {
                            Log.i(TAG,"Unable to create the Group.");
                            return null;
                        }
                    }
                })
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        return userCollection.document(userEmail).collection("Groups")
                                .document(groupBase.getOriginalName()).set(holderMap)//Change the .set of this line to the POJO if we want to store the POJO here instead of using tag reference.
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.i(TAG,"Successfully attached group to user profile.");
                                        } else {
                                            Log.w(TAG,"Error attaching group to user profile.");
                                        }
                                    }
                                }) ;
                    }
                });

    }

    public void updateGroup(final GroupBase groupBase){
        groupCollection.document(groupBase.getOriginalName()).set(groupBase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG,"Successfully stored the Group under: "+groupBase.getName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Failure to update Group: " + groupBase.getName());
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
        return userCollection.document(userEmail).collection("Groups").get()
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

    public Task<byte[]> getImage(String uri){
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
        return imageRef.getBytes(1024*1024*3);
    }

    /*
     * Creates a new Document in the Events collection. The Document is created as a GroupEvent
     * object. Upon successful creation, we create a Document in the creator's Events collection via
     * the event name. The associated tags for the event are stored as a sub-collection in the event
     * document.
     */
    public Task<Void> createEvent(final GroupEvent groupEvent, final List<UserModel> inviteList){
        final DocumentReference eventRef = eventCollection.document(groupEvent.getOriginalName());
        return eventRef.set(groupEvent, SetOptions.merge());
    }

    public Task<Void> updateEvent(final GroupEvent groupEvent){
        final DocumentReference eventRef = eventCollection.document(groupEvent.getOriginalName());
        return eventRef.set(groupEvent, SetOptions.merge());
    }

    private void updateEventInvites(final GroupEvent groupEvent, final List<UserModel> userList){
        final DocumentReference eventRef = eventCollection.document(groupEvent.getOriginalName());
    }

    /*
     * Attach an event to the user's events collection.
     */
    private Task<Void> addEventToUser(GroupEvent event){
        final DocumentReference docRef = userCollection.document(userEmail).collection("Events").document(event.getOriginalName());
        final CollectionReference invitees = eventCollection.document(event.getOriginalName()).collection("Invitees");
        final Map<String,String> testMap = new HashMap<>();
        testMap.put("Access","Owner");
        return docRef.set(testMap, SetOptions.merge())
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if(task.isSuccessful()){
                            Log.i(TAG,"Successfully added event to user doc.");
                            return invitees.document(userEmail).set(testMap,SetOptions.merge());
                        } else {
                            Log.w(TAG,"Error adding event to user doc.");
                            return null;
                        }
                    }
                });
    }

    //Will currently return a whole list of users to populate the recyclerview for inviting users to event/groups.
    Task<List<UserModel>> fetchUsers(){
        return userCollection.get().continueWith(new Continuation<QuerySnapshot, List<UserModel>>() {
            @Override
            public List<UserModel> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<UserModel> userList = new ArrayList<>();
                if (task.isSuccessful() && task.getResult() !=null){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.i(TAG,"Converting user document to UserModel: "+document.getId());
                        UserModel model = document.toObject(UserModel.class);
                        userList.add(model);
                    }
                } else if(task.getResult() == null){
                    Log.w(TAG,"There are no users to fetch.");
                }
                return userList;
            }
        });
    }

    //Todo: change to accept a document title so we can call this regardless of Event or Group. Consider changing way data is stored udner the usser's collection.
    void sendEventInvite(final GroupEvent groupEvent, final List<UserModel> userList){
        final CollectionReference eventCol = eventCollection.document(groupEvent.getOriginalName()).collection(INVITEES);
        final WriteBatch inviteBatch = db.batch();
        List<Task<Void>> sendInvites = new ArrayList<>();
        for(final UserModel user : userList){
            sendInvites.add(userCollection.document(user.getEmail()).collection(EVENT_INVITES)
                    .document(groupEvent.getName()).set(groupEvent,SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG,"Successfully invited " + user.getEmail() + " to " + groupEvent.getName());
                                inviteBatch.set(eventCol.document(user.getEmail()),user);
                            }
                        }
                    }));
        }
        Tasks.whenAllSuccess(sendInvites).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                inviteBatch.commit();
            }
        });
    }

    void sendGroupInvites(final GroupBase groupBase, final List<UserModel> userList){
        final CollectionReference groupCol = groupCollection.document(groupBase.getOriginalName()).collection(INVITEES);
        final WriteBatch inviteBatch = db.batch();
        List<Task<Void>> sendInvites = new ArrayList<>();

        for(final UserModel user : userList){
            sendInvites.add(userCollection.document(user.getEmail()).collection(GROUP_INVITES)
                    .document(groupBase.getName()).set(groupBase,SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG,"Successfully invited " + user.getEmail() + " to " + groupBase.getName());
                                inviteBatch.set(groupCol.document(user.getEmail()),user);
                            }
                        }
                    }));
        }
        Tasks.whenAllSuccess(sendInvites).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                inviteBatch.commit();
            }
        });
    }

    Task<List<String>> getEventInvitees(final GroupEvent event){
        CollectionReference eventInvitees = eventCollection.document(event.getOriginalName()).collection("Invitees");
        final List<String> userList = new ArrayList<>();
        return eventInvitees.get().continueWith(new Continuation<QuerySnapshot, List<String>>() {
            @Override
            public List<String> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){
                        for(QueryDocumentSnapshot document:task.getResult()){
                            userList.add(document.getId());
                        }
                    } else {
                        Log.i(TAG,"No invited users for: "+event.getName());
                    }

                } else {
                    Log.w(TAG,"Unable to retrieve invitee list for: "+event.getOriginalName());
                    return null;
                }
                return userList;
            }
        });
    }

    Task<List<String>> getEventResponses(final GroupEvent event, final String status){
        CollectionReference invitees = eventCollection.document(event.getOriginalName())
                .collection("Invitees");
        Log.i(TAG,"starting query");
        Query query = invitees.whereEqualTo("Status",status);
        return query.get().continueWith(new Continuation<QuerySnapshot, List<String>>() {
            @Override
            public List<String> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<String> responses = new ArrayList<>();
                if(task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Log.i(TAG, snapshot.getId()+ " responded " + status + " to " +event.getName());
                            responses.add(snapshot.getId());
                        }
                    } else {
                        Log.i(TAG,"No guests have accepted event: "+event.getName());
                    }
                } else {
                    Log.i(TAG,"Error fetching list of accepted users for "+event.getName());
                }
                return responses;
            }
        });
    }

    // Task to upload an image and on success return the downloadUri.
    Task<Uri> uploadImage(Uri uri, String groupName){
        final StorageReference childStorage = storageRef.child("images/"+groupName);
        return childStorage.putFile(uri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(task.isSuccessful()){
                        Log.i(TAG,"Successfully uploaded image to storage.");
                        return childStorage.getDownloadUrl();
                    }
                    return null;
                }
            });
    }

    // Used to add users to the current user.
    // ToDo: Create a user Pojo so we can properly store them into firestore
    Task<Void> createConnection(){
        CollectionReference connections = userCollection.document(userEmail).collection(CONNECTIONS);
        Map<String,String> testMap = new HashMap<>();
        testMap.put("User email", "some email");
        return connections.document("Some new connection").set(testMap);
    }


    public Task<GroupBase> getGroup(){
        return null;
    }

}
