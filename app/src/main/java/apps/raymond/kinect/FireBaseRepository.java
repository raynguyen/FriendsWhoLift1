/*
 * In true MVVM fashion, it should go from ViewModel -> Repository -> DAO -> DataBackEnd.
 *
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

// LOCAL DATABASE CAHCE WITH REST API

package apps.raymond.kinect;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Groups.GroupBase;
import apps.raymond.kinect.UserProfile.UserModel;

public class FireBaseRepository {
    private static final String TAG = "FireBaseRepository";
    private static final String GROUPS = "Groups";
    private static final String USERS = "Users";
    private static final String EVENTS = "Events";
    private static final String MEMBERS = "Members";
    private static final String CONNECTIONS = "Connections";
    private static final String INTERESTS = "Interests";
    private static final String INVITED = "Invited";
    private static final String ACCEPTED = "Accepted";
    private static final String DECLINED = "Declined";
    private static final String EVENT_INVITES = "EventInvites";
    private static final String GROUP_INVITES = "GroupInvites";
    private static final String EVENT_ATTEND_FIELD = "attenders";
    private static final String EVENT_INVITED_FIELD = "invited";

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = db.collection(GROUPS);
    private CollectionReference userCollection = db.collection(USERS);
    private CollectionReference eventCollection = db.collection(EVENTS);

    private String userEmail;
    private UserModel curUserModel;

    public FireBaseRepository() {
        try {
            this.userEmail = currentUser.getEmail();

        } catch (NullPointerException npe) {
            Log.w(TAG, "Error: " + npe);
        }
    }

    //*------------------------------------------USER-------------------------------------------*//
    public Task<AuthResult> signInWithEmail(final String name, String password) {
        Log.i(TAG,"THIS SHOULD BE FIRST: "+userEmail);
        return firebaseAuth.signInWithEmailAndPassword(name, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.i(TAG,"THIS SHOULD BE SECOND!");
                            userEmail = name;
                        }
                    }
                });
    }

    //WE ARE TRYING TO RETRIEVE INFO REGARDING FIREBASEUSER BEFORE IT CAN BE COMPLETED BY SERVERS.
    //TO GET THE USER DOCUMENT, WE SHOULD USE QUERY THAT WE CONSTRUCT VIA A STRING.
    public Task<UserModel> getCurrentUser(){
        Log.i(TAG,"THIS SHOULD BE 5TH");
        DocumentReference userDoc = userCollection.document(currentUser.getEmail());

        return userDoc.get().continueWith(new Continuation<DocumentSnapshot, UserModel>() {
            @Override
            public UserModel then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        return task.getResult().toObject(UserModel.class);
                    }
                }
                Log.w(TAG,"ERROR RETRIEVING CURRENT USER.");
                return null;
            }
        });
    }

    public Task<Void> createUserByEmail(final UserModel userModel, final String password) {
        final String name = userModel.getEmail();
        Log.i(TAG, "Creating new user " + name);
        return firebaseAuth.createUserWithEmailAndPassword(name, password)
                .continueWithTask(new Continuation<AuthResult, Task<AuthResult>>() {
                    @Override
                    public Task<AuthResult> then(@NonNull Task<AuthResult> task) throws Exception {
                        return signInWithEmail(name, password);
                    }
                }).continueWithTask(new Continuation<AuthResult, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<AuthResult> task) throws Exception {
                        return createUserDoc(userModel);
                    }
                });
    }

    private Task<Void> createUserDoc(UserModel userModel) {
        final String name = userModel.getEmail();
        Log.i(TAG, "Creating new user Document " + name);
        return userCollection.document(name).set(userModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Created document for user " + name);
                        } else {
                            Log.w(TAG, "Failed to create document for " + name);
                        }
                    }
                });
    }

    Task<List<Event_Model>> fetchEventInvites() {
        CollectionReference eventMessages = userCollection.document(userEmail).collection(EVENT_INVITES);
        final List<Event_Model> eventInvites = new ArrayList<>();
        return eventMessages.get().continueWith(new Continuation<QuerySnapshot, List<Event_Model>>() {
            @Override
            public List<Event_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        eventInvites.add(document.toObject(Event_Model.class));
                    }
                    return eventInvites;
                } else if (task.isSuccessful() && task.getResult() == null) {
                    Log.i(TAG, "No event invites.");
                }
                return null;
            }
        });
    }

    Task<List<GroupBase>> fetchGroupInvites() {
        CollectionReference usersGroups = userCollection.document(userEmail).collection(GROUP_INVITES);
        final List<GroupBase> groupInvites = new ArrayList<>();

        return usersGroups.get().continueWith(new Continuation<QuerySnapshot, List<GroupBase>>() {
            @Override
            public List<GroupBase> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            groupInvites.add(document.toObject(GroupBase.class));
                        }
                        return groupInvites;
                    }
                }
                return null;
            }
        });
    }

    Task<Void> addConnection(final UserModel user){
        CollectionReference userConnections = userCollection.document(userEmail).collection(CONNECTIONS);
        return userConnections.document(user.getEmail()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"Successfully created a connection to user: " + user.getEmail());
                }
            }
        });
    }

    Task<List<UserModel>> getConnections(){
        CollectionReference userConnections = userCollection.document(userEmail).collection(CONNECTIONS);
        final List<UserModel> connections = new ArrayList<>();
        return userConnections.get().continueWith(new Continuation<QuerySnapshot, List<UserModel>>() {
            @Override
            public List<UserModel> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if(task.isSuccessful()){
                    if(task.getResult() !=null){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            connections.add(document.toObject(UserModel.class));
                        }
                    } else {
                        Log.w(TAG,"User has no connections.");
                    }
                    return connections;
                } else {
                    Log.w(TAG,"Error retrieving connections.");
                    return null;
                }
            }
        });
    }

    Task<List<String>> getInterests(){
        CollectionReference userConnections = userCollection.document(userEmail).collection(INTERESTS);
        final List<String> interests = new ArrayList<>();
        return null;
    }
    //*------------------------------------------EVENTS------------------------------------------*//
    /*
     * Creates a new Document in the Events collection. The Document is created as a Event_Model
     * object. Upon successful creation, we create a Document in the creator's Events collection via
     * the event name. The associated tags for the event are stored as a sub-collection in the event
     * document.
     */
    Task<Void> createEvent(final Event_Model groupEvent) {
        final DocumentReference eventRef = eventCollection.document(groupEvent.getOriginalName());
        return eventRef.set(groupEvent, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                addUserToEvent(groupEvent);
            }
        });
    }

    Task<Void> updateEvent(final Event_Model groupEvent) {
        final DocumentReference eventRef = eventCollection.document(groupEvent.getOriginalName());
        return eventRef.set(groupEvent, SetOptions.merge());
    }

    //Todo: change to accept a document title so we can call this regardless of Event or Group. Consider changing way data is stored under the user's collection.
    void sendEventInvite(final Event_Model groupEvent, final List<UserModel> userList) {
        final DocumentReference eventDoc = eventCollection.document(groupEvent.getOriginalName());
        final CollectionReference eventInvitedCol = eventCollection.document(groupEvent.getOriginalName()).collection(INVITED);
        final WriteBatch inviteBatch = db.batch();
        final List<Task<Void>> sendInvites = new ArrayList<>();

        for (final UserModel user : userList) {
            sendInvites.add(userCollection.document(user.getEmail()).collection(EVENT_INVITES)
                    .document(groupEvent.getName()).set(groupEvent, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                inviteBatch.set(eventInvitedCol.document(user.getEmail()), user);
                            }
                        }
                    }));
        }
        Tasks.whenAllSuccess(sendInvites).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                inviteBatch.commit();
                eventDoc.update(EVENT_INVITED_FIELD,sendInvites.size());
            }
        });
    }

    Task<Void> addUserToEvent(final Event_Model event) {
        final CollectionReference usersEvents = userCollection.document(userEmail).collection(EVENTS);
        final CollectionReference usersEventInvites = userCollection.document(userEmail).collection(EVENT_INVITES);
        final CollectionReference acceptedUsers = eventCollection.document(event.getOriginalName()).collection(ACCEPTED);
        final CollectionReference invitedUsers = eventCollection.document(event.getOriginalName()).collection(INVITED);

        return usersEvents.document(event.getOriginalName()).set(event)
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if (task.isSuccessful()) {
                            return acceptedUsers.document(userEmail).set(curUserModel);
                        }
                        return null;
                    }
                }).continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if (task.isSuccessful()) {
                            return usersEventInvites.document(event.getOriginalName()).delete();
                        }
                        return null;
                    }
                }).continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if (task.isSuccessful()) {
                            return invitedUsers.document(userEmail).delete();
                        }
                        return null;
                    }
                });
    }

    Task<List<Task<DocumentSnapshot>>> getUsersEvents() {
        // First task retrieves a list of the Groups from the User Document.
        return userCollection.document(userEmail).collection("Events").get()
                // Second task will return a List of tasks to retrieve a document pertaining to each document retrieved from the first task.
                .continueWith(new Continuation<QuerySnapshot, List<Task<DocumentSnapshot>>>() {
                    @Override
                    public List<Task<DocumentSnapshot>> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        List<Task<DocumentSnapshot>> groupEvents = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Task<DocumentSnapshot> myEvent = eventCollection.document(document.getId()).get();
                            groupEvents.add(myEvent);
                        }
                        return groupEvents;
                    }
                });
    }

    Task<List<UserModel>> getEventInvitees(final Event_Model event) {
        CollectionReference eventInvitees = eventCollection.document(event.getOriginalName()).collection(INVITED);
        final List<UserModel> userList = new ArrayList<>();
        return eventInvitees.get().continueWith(new Continuation<QuerySnapshot, List<UserModel>>() {
            @Override
            public List<UserModel> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userList.add(document.toObject(UserModel.class));
                        }
                    } else {
                        Log.i(TAG, "No invited users for: " + event.getName());
                    }

                } else {
                    Log.w(TAG, "Unable to retrieve invitee list for: " + event.getOriginalName());
                    return null;
                }
                return userList;
            }
        });
    }

    Task<List<UserModel>> getEventResponses(final Event_Model event, final String status) {
        CollectionReference responseCollection = eventCollection.document(event.getOriginalName()).collection(status);

        return responseCollection.get().continueWith(new Continuation<QuerySnapshot, List<UserModel>>() {
            @Override
            public List<UserModel> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<UserModel> userModels = new ArrayList<>();
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userModels.add(document.toObject(UserModel.class));
                        }
                    } else {
                        Log.i(TAG, "No guests have accepted event: " + event.getName());
                    }
                } else {
                    Log.i(TAG, "Error fetching list of accepted users for " + event.getName());
                }
                return userModels;
            }
        });
    }

    //*------------------------------------------GROUPS------------------------------------------*//
    /*
     * Call when creating a new GroupBase object and store the details on FireStore. When storing
     * the object for the first time, we attach a tag to User's document. This tag is used to read
     * the groups connected to the user.
     * Logic:
     * Create the Group Document first and if successful, move to adding the tag to the User.
     */
    Task<Void> createGroup(final GroupBase groupBase, final List<UserModel> inviteList) {
        final DocumentReference groupDoc = groupCollection.document(groupBase.getOriginalName());
        final Map<String, String> holderMap = new HashMap<>();
        holderMap.put("Access", "Owner");
        // Create a document from the GroupBase object under the creation name.
        return groupDoc.set(groupBase)
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        return groupDoc.collection(MEMBERS).document(userEmail).set(curUserModel);
                    }
                })
                .continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(@NonNull Task<Void> task) throws Exception {
                        if (task.isSuccessful()) {
                            for (UserModel user : inviteList) {
                                groupDoc.collection(INVITED).document(user.getEmail()).set(user);
                            }
                        } else {
                            Log.i(TAG, "Error creating invitee sub collection.");
                        }
                        return null;
                    }
                })
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if (task.isSuccessful()) {
                            return userCollection.document(userEmail).collection(GROUPS)
                                    .document(groupBase.getName()).set(holderMap);
                        } else {
                            Log.i(TAG, "Unable to create the Group.");
                            return null;
                        }
                    }
                })
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        return userCollection.document(userEmail).collection("Groups")
                                .document(groupBase.getOriginalName()).set(holderMap);//Change the .set of this line to the POJO if we want to store the POJO here instead of using tag reference.
                    }
                });

    }

    void sendGroupInvites(final GroupBase groupBase, final List<UserModel> userList) {
        final CollectionReference groupCol = groupCollection.document(groupBase.getOriginalName()).collection(INVITED);
        final WriteBatch inviteBatch = db.batch();
        List<Task<Void>> sendInvites = new ArrayList<>();

        for (final UserModel user : userList) {
            sendInvites.add(userCollection.document(user.getEmail()).collection(GROUP_INVITES)
                    .document(groupBase.getName()).set(groupBase, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                inviteBatch.set(groupCol.document(user.getEmail()), user);
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

    void editGroup(final GroupBase groupBase) {
        groupCollection.document(groupBase.getOriginalName()).set(groupBase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully stored the Group under: " + groupBase.getName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Failure to update Group: " + groupBase.getName());
            }
        });
    }

    // Info below may be outdated.
    // Steps are to get the keySet corresponding to what groups the user is registered with
    // Then we want to retrieve the Documentsnapshots of the groups named in the KeySet.
    // Then we read the imageURI field to get the photo storage reference and download the photo
    // Finally we want to use the documentsnapshot and the retrieved byte[] to create a groupbase object.
    Task<List<Task<GroupBase>>> getUsersGroups() {
        final List<String> groupList = new ArrayList<>();
        //First thing to do is retrieve the Group tags from current user.
        return userCollection.document(userEmail).collection("Groups").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                groupList.add(document.getId());
                            }
                        }
                    }
                }).continueWith(new Continuation<QuerySnapshot, List<Task<GroupBase>>>() {
                    @Override
                    public List<Task<GroupBase>> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        List<Task<GroupBase>> taskList = new ArrayList<>();
                        for (final String group : groupList) {
                            taskList.add(groupCollection.document(group).get().continueWith(new Continuation<DocumentSnapshot, GroupBase>() {
                                @Override
                                public GroupBase then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                                    return task.getResult().toObject(GroupBase.class);
                                }
                            }));
                        }
                        return taskList;
                    }
                });
    }

    Task<Void> addUserToGroup(final GroupBase group) {
        CollectionReference usersGroups = userCollection.document(userEmail).collection(GROUPS);
        final CollectionReference usersGroupInvites = userCollection.document(userEmail).collection(GROUP_INVITES);
        final CollectionReference groupMembers = groupCollection.document(group.getOriginalName()).collection(MEMBERS);

        return usersGroups.document(group.getOriginalName()).set(group).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                if (task.isSuccessful()) {
                    return groupMembers.document(userEmail).set(curUserModel);
                } else {
                    return null;
                }

            }
        })
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        if (task.isSuccessful()) {
                            return usersGroupInvites.document(group.getOriginalName()).delete();
                        }
                        return null;
                    }
                });
    }

    Task<List<UserModel>> fetchGroupMembers(GroupBase group){
        CollectionReference groupMembers = groupCollection.document(group.getOriginalName()).collection(MEMBERS);

        return groupMembers.get().continueWith(new Continuation<QuerySnapshot, List<UserModel>>() {
            @Override
            public List<UserModel> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if(task.isSuccessful()){
                    List<UserModel> memberList = new ArrayList<>();
                    if(task.getResult()!=null){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            memberList.add(document.toObject(UserModel.class));
                        }
                        return memberList;
                    }
                }
                return null;
            }
        });
    }
    //*-------------------------------------------ETC--------------------------------------------*//


    private void removeInviteListeners() {
        //eventInviteListener.remove();
        //groupInviteListener.remove();
    }

    Task<byte[]> getImage(String uri) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
        return imageRef.getBytes(1024 * 1024 * 3);
    }

    //Will currently return a whole list of users to populate the recyclerview for inviting users to event/groups.
    // Todo: Filter our users that have Privacy:private.
    Task<List<UserModel>> fetchUsers() {
        Log.i(TAG,"FETCHING USERS TO POPULATE INVITE LIST.");
        return userCollection.get().continueWith(new Continuation<QuerySnapshot, List<UserModel>>() {
            @Override
            public List<UserModel> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<UserModel> userList = new ArrayList<>();
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserModel model = document.toObject(UserModel.class);
                        if(!model.getEmail().equals(userEmail)){
                            userList.add(model);
                        }
                    }
                } else if (task.getResult() == null) {
                    Log.w(TAG, "There are no users to fetch.");
                }
                Log.i(TAG,"COMPLETED FETCHING USERS.");
                return userList;
            }
        });
    }

    // Task to upload an image and on success return the downloadUri.
    Task<Uri> uploadImage(Uri uri, String groupName) {
        final StorageReference childStorage = storageRef.child("images/" + groupName);
        return childStorage.putFile(uri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Successfully uploaded image to storage.");
                            return childStorage.getDownloadUrl();
                        }
                        return null;
                    }
                });
    }

    public Task<GroupBase> getGroup() {
        return null;
    }

}