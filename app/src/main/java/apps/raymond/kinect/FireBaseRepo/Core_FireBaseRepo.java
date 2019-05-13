/**
 * Commenting syntax:
 * When referring to Firestore JSON type structures, comments will refer to paths typically as
 * Collection->Document->field etc.
 * --The first branch will always be the Collection and immediately followed by the Document. If
 * --the succeeding text begins with a lower case, it will indicate that it is a field of the document.
 *
 *  * In true MVVM fashion, it should go from ViewModel -> Repository -> DAO -> DataBackEnd.
 *  *
 *  * There appears to be an error where if there is no photo, the recycler view does not retrieve all the group objects.
 *  *
 *  * There appears to be an error where onSuccessListeners will trigger even if there is no collection or document of the requested name.
 *  * --NOTE: When we create a Task to retrieve documents from Firestore, if the document or the document path does not exist,
 *  * it will return a NULL document but will still SUCCEED. This is why the onSuccessListener is triggered.
 *  *
 *  * Repository modules handle data operations. They provide a clean API so that the rest of the app
 *  * can retrieve this data easily. They know where to get the data from and what API calls to make
 *  * when data is updated. You can consider repositories to be mediators between different data
 *  * sources, such as persistent models, web services, and caches
 */

package apps.raymond.kinect.FireBaseRepo;

import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
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
import apps.raymond.kinect.Groups.Group_Model;
import apps.raymond.kinect.Message_Model;
import apps.raymond.kinect.UserProfile.User_Model;

/**
 * Repository class to abstract data communication between the application and Firebase backend.
 *
 * ToDo: Check to see how many times this Repo is instantiated. We want it to be only once, when the CoreActivity loads. -> Static utility class // injection?
 */
public class Core_FireBaseRepo {
    private static final String TAG = "Core_FireBaseRepo";
    private static final String GROUPS = "Groups";
    private static final String USERS = "Users";
    private static final String EVENTS = "Events";
    private static final String MESSAGES = "Messages";
    private static final String MEMBERS = "Members";
    private static final String CONNECTIONS = "Connections";
    private static final String LOCATIONS = "Locations";
    private static final String INTERESTS = "Interests";
    private static final String INVITED = "Invited";
    private static final String ACCEPTED = "Accepted";
    private static final String DECLINED = "Declined";
    private static final String EVENT_INVITES = "EventInvites";
    private static final String GROUP_INVITES = "GroupInvites";
    private static final String EVENT_ATTEND_FIELD = "attenders";
    private static final String EVENT_INVITED_FIELD = "invited";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = mStore.collection(GROUPS);
    private CollectionReference userCollection = mStore.collection(USERS);
    private CollectionReference eventCollection = mStore.collection(EVENTS);

    private String mUserEmail;
    private User_Model curUserModel;

    /*ToDo: There should be no Java code in the Repository class.
        This method should not hold the mUserEmail or curUserMode, these should be passed as required to repository calls.
    */
    public Core_FireBaseRepo() {
        Log.w(TAG,"Creating new instance of Core_FireBaseRepo");
        if(mAuth.getCurrentUser()!=null){
            try {
                this.mUserEmail = mAuth.getCurrentUser().getEmail();
                getCurrentUser().addOnCompleteListener(new OnCompleteListener<User_Model>() {
                    @Override
                    public void onComplete(@NonNull Task<User_Model> task) {
                        curUserModel = task.getResult();
                    }
                });
            } catch (NullPointerException npe) {
                Log.w(TAG, "Error.",npe);
            }
        } else {
            getCurrentUser();
        }

    }

    //*------------------------------------------USER-------------------------------------------*//

    public Task<Void> signOut(Context context){
        Log.i(TAG,"REPOSITORY LOG OUT METHOD.");
        return AuthUI.getInstance().signOut(context);
    }

    public Task<AuthResult> emailSignIn(final String name, String password) {
        return mAuth.signInWithEmailAndPassword(name, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mUserEmail = name;
                        }
                    }
                });
    }

    public Task<User_Model> getCurrentUser(){
        try{
            DocumentReference userDoc = userCollection.document(mUserEmail);
            return userDoc.get().continueWith(new Continuation<DocumentSnapshot, User_Model>() {
                @Override
                public User_Model then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                    if(task.isSuccessful()){
                        if(task.getResult()!=null){
                            return task.getResult().toObject(User_Model.class);
                        }
                    }
                    Log.w(TAG,"ERROR RETRIEVING CURRENT USER.", task.getException());
                    return null;
                }
            });
        } catch (Exception e){
            Log.w(TAG,"Exception occured:",e);
            return null;
        }
    }

    public Task<Void> createUserByEmail(final User_Model userModel, final String password) {
        final String name = userModel.getEmail();
        Log.i(TAG, "Creating new user " + name);
        return mAuth.createUserWithEmailAndPassword(name, password)
                .continueWithTask(new Continuation<AuthResult, Task<AuthResult>>() {
                    @Override
                    public Task<AuthResult> then(@NonNull Task<AuthResult> task) throws Exception {
                        return emailSignIn(name, password);
                    }
                }).continueWithTask(new Continuation<AuthResult, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<AuthResult> task) throws Exception {
                        return createUserDoc(userModel);
                    }
                });
    }

    private Task<Void> createUserDoc(User_Model userModel) {
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

    public Task<List<Event_Model>> getEventInvitations() {
        CollectionReference eventMessages = userCollection.document(mUserEmail).collection(EVENT_INVITES);
        return eventMessages.get().continueWith(new Continuation<QuerySnapshot, List<Event_Model>>() {
            @Override
            public List<Event_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<Event_Model> eventInvites = new ArrayList<>();
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        eventInvites.add(document.toObject(Event_Model.class));
                    }
                }
                return eventInvites;
            }
        });
    }

    public Task<List<Group_Model>> getGroupInvitations() {
        CollectionReference usersGroups = userCollection.document(mUserEmail)
                .collection(GROUP_INVITES);

        return usersGroups.get().continueWith(new Continuation<QuerySnapshot, List<Group_Model>>() {
            @Override
            public List<Group_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<Group_Model> result = new ArrayList<>();
                if (task.isSuccessful() && task.getResult()!=null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        result.add(document.toObject(Group_Model.class));
                    }
                }
                return result;
            }
        });
    }

    public Task<Void> addConnection(final User_Model user){
        CollectionReference userConnections = userCollection.document(mUserEmail).collection(CONNECTIONS);
        return userConnections.document(user.getEmail()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"Successfully created a connection to user: " + user.getEmail());
                } else {
                    Log.w(TAG,"Error adding new connection to user.",task.getException());
                }
            }
        });
    }

    public Task<List<User_Model>> getConnections(){
        CollectionReference userConnections = userCollection.document(mUserEmail).collection(CONNECTIONS);
        final List<User_Model> connections = new ArrayList<>();
        return userConnections.get().continueWith(new Continuation<QuerySnapshot, List<User_Model>>() {
            @Override
            public List<User_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if(task.isSuccessful()){
                    if(task.getResult() !=null){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            connections.add(document.toObject(User_Model.class));
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

    public Task<Void> addLocation(Address address, String addressName){
        CollectionReference locationCol = userCollection.document(mUserEmail).collection(LOCATIONS);
        GeoPoint geoPoint = new GeoPoint(address.getLatitude(),address.getLongitude());
        return locationCol.document(addressName).set(geoPoint);
    }

    //*------------------------------------------EVENTS------------------------------------------*//

    /**
     * When a user registers to or creates an event, the following is the task priority in order to
     * minimize errors in the corresponding Firestore collections.
     *
     * If the user is creating an Event:
     * 0. Add the Event POJO to the Events collection, then
     *
     * If user opts to attending an event:
     * 1. Add the Event POJO to the User->Events Collection.
     * 2. Add the User POJO to the Events->User Collection
     * 3a. Update the Event Document inside the Events Collection with the modified attendees count.
     * 3b. Remove event invitations if the User opts to attend via an Event Invitation.
     * 3c. Update the Event Document to reflect changes in invitation count is applicable.
     *
     */

    /**
     * Creates a document in Events using the event POJO.
     * @param event The created Event POJO.
     * @return Task
     */
    public Task<Void> createEvent(final Event_Model event) {
        DocumentReference eventRef = eventCollection.document(event.getOriginalName());
        //ToDo: We currently overwrite any events with the same name. Need to determine an id method.
        return eventRef.set(event);
    }

    /**
     * Create the event document in Users->Events.
     * @param event The event to add to the current user.
     */
    public Task<Void> addEventToUser(Event_Model event) {
        CollectionReference usersEvents = userCollection.document(mUserEmail).collection(EVENTS);
        return usersEvents.document(event.getOriginalName()).set(event);
    }

    /**
     * Creates a user document in Events->Accepted.
     * @param event The event the user is attending.
     */
    public Task<Void> addUserToEvent(Event_Model event){
        CollectionReference eventsAccepted = eventCollection.document(mUserEmail).collection(ACCEPTED);
        return eventsAccepted.document(mUserEmail).set(curUserModel);
    }

    /**
     * Update the value of Events->Event->attending.
     * @param event The event being modified.
     * @param i The value to increment/decrement the field.
     */
    public Task<Void> incrementEventAttending(Event_Model event, int i){
        DocumentReference eventsAccepted = eventCollection.document(event.getOriginalName());
        return eventsAccepted.update("attending", FieldValue.increment(i));
    }

    /**
     * Update the value of Events->Event->invited.
     * @param event The event being modified.
     * @param i The value to increment/decrement the field.
     */
    public Task<Void> incrementEventInvited(Event_Model event, int i){
        DocumentReference eventsAccepted = eventCollection.document(event.getOriginalName());
        return eventsAccepted.update("attending", FieldValue.increment(i));
    }

    /**
     * Delete the event invitation from User->EventInvites.
     * @param event The event of which the user is invited.
     */
    public Task<Void> removeEventInvite(Event_Model event){
        DocumentReference docRef = userCollection.document(mUserEmail)
                .collection(EVENT_INVITES).document(event.getOriginalName());
        return docRef.delete();
    }

    //Todo: Revisit this method to invite users to an event.
    public void sendEventInvite(final Event_Model groupEvent, final List<User_Model> userList) {
        final DocumentReference eventDoc = eventCollection.document(groupEvent.getOriginalName());
        final CollectionReference eventInvitedCol = eventCollection.document(groupEvent.getOriginalName()).collection(INVITED);
        final WriteBatch inviteBatch = mStore.batch();
        final List<Task<Void>> sendInvites = new ArrayList<>();

        for (final User_Model user : userList) {
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

    public Task<List<Event_Model>> getAcceptedEvents(){
        CollectionReference eventsRef = mStore.collection(USERS)
                .document(mUserEmail).collection(EVENTS);

        return eventsRef.get().continueWith(new Continuation<QuerySnapshot, List<Event_Model>>() {
            @Override
            public List<Event_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<Event_Model> result = new ArrayList<>();
                if(task.isSuccessful() && task.getResult()!=null){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        result.add(document.toObject(Event_Model.class));
                    }
                }
                return result;
            }
        });
    }

    public Task<List<User_Model>> getEventInvitees(final Event_Model event) {
        CollectionReference eventInvitesRef = mStore.collection(EVENTS)
                .document(event.getOriginalName()).collection(INVITED);
        final List<User_Model> userList = new ArrayList<>();
        return eventInvitesRef.get().continueWith(new Continuation<QuerySnapshot, List<User_Model>>() {
            @Override
            public List<User_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userList.add(document.toObject(User_Model.class));
                        }
                    }
                }
                return userList;
            }
        });
    }

    public Task<List<User_Model>> getEventResponses(final Event_Model event, final String status) {
        CollectionReference responseCollection = eventCollection.document(event.getOriginalName()).collection(status);

        return responseCollection.get().continueWith(new Continuation<QuerySnapshot, List<User_Model>>() {
            @Override
            public List<User_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<User_Model> userModels = new ArrayList<>();
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userModels.add(document.toObject(User_Model.class));
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

    public Task<Void> postMessage(final Event_Model event,final Message_Model message){
        CollectionReference eventMessagesRef = mStore.collection(EVENTS)
                .document(event.getOriginalName())
                .collection(MESSAGES);

        return eventMessagesRef.document().set(message);
    }

    public Task<List<Message_Model>> getMessages(Event_Model event){
        CollectionReference eventMessagesRef = mStore.collection(EVENTS)
                .document(event.getOriginalName())
                .collection(MESSAGES);

        return eventMessagesRef.get().continueWith(new Continuation<QuerySnapshot, List<Message_Model>>() {
            @Override
            public List<Message_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<Message_Model> result = new ArrayList<>();
                if(task.isSuccessful() && task.getResult()!=null){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        result.add(document.toObject(Message_Model.class));
                    }
                }
                return result;
            }
        });
    }

    //ToDo: this has to be refactored to accept a location object input and return a list of events within some radius of the latlng.
    public Task<List<Event_Model>> getPublicEvents(){
        CollectionReference eventsRef = mStore.collection(EVENTS);

        return eventsRef.whereEqualTo("privacy",0).get()
                .continueWith(new Continuation<QuerySnapshot, List<Event_Model>>() {
                    @Override
                    public List<Event_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        if(task.isSuccessful()){
                            List<Event_Model> publicEvents = new ArrayList<>();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Event_Model event = document.toObject(Event_Model.class);
                                publicEvents.add(document.toObject(Event_Model.class));
                            }
                            return publicEvents;
                        }
                        return null;
                    }
                });
    }
    //*------------------------------------------GROUPS------------------------------------------*//


    // Info below may be outdated.
    // Steps are to get the keySet corresponding to what groups the user is registered with
    // Then we want to retrieve the Document snapshots of the groups named in the KeySet.
    // Then we read the imageURI field to get the photo storage reference and download the photo
    // Finally we want to use the document snapshot and the retrieved byte[] to create a group base object.
    public Task<List<Task<Group_Model>>> getUsersGroups() {
        final List<String> groupList = new ArrayList<>();
        //First thing to do is retrieve the Group tags from current user.

        return userCollection.document(mUserEmail).collection("Groups").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                groupList.add(document.getId());
                            }
                        }
                    }
                }).continueWith(new Continuation<QuerySnapshot, List<Task<Group_Model>>>() {
                    @Override
                    public List<Task<Group_Model>> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        List<Task<Group_Model>> taskList = new ArrayList<>();
                        for (final String group : groupList) {
                            taskList.add(groupCollection.document(group).get().continueWith(new Continuation<DocumentSnapshot, Group_Model>() {
                                @Override
                                public Group_Model then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                                    return task.getResult().toObject(Group_Model.class);
                                }
                            }));
                        }
                        return taskList;
                    }
                });
    }

    /*
     * Call when creating a new Group_Model object and store the details on FireStore. When storing
     * the object for the first time, we attach a tag to User's document. This tag is used to read
     * the groups connected to the user.
     * Logic:
     * Create the Group Document first and if successful, move to adding the tag to the User.
     */
    public Task<Void> createGroup(final Group_Model groupBase, final List<User_Model> inviteList) {
        final DocumentReference groupDoc = groupCollection.document(groupBase.getOriginalName());
        final Map<String, String> holderMap = new HashMap<>();
        holderMap.put("Access", "Owner");
        // Create a document from the Group_Model object under the creation name.
        return groupDoc.set(groupBase)
                .continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                        return groupDoc.collection(MEMBERS).document(mUserEmail).set(curUserModel);
                    }
                })
                .continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(@NonNull Task<Void> task) throws Exception {
                        if (task.isSuccessful()) {
                            for (User_Model user : inviteList) {
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
                            return userCollection.document(mUserEmail).collection(GROUPS)
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
                        return userCollection.document(mUserEmail).collection("Groups")
                                .document(groupBase.getOriginalName()).set(holderMap);//Change the .set of this line to the POJO if we want to store the POJO here instead of using tag reference.
                    }
                });

    }

    public void sendGroupInvites(final Group_Model groupBase, final List<User_Model> userList) {
        final CollectionReference groupCol = groupCollection.document(groupBase.getOriginalName()).collection(INVITED);
        final WriteBatch inviteBatch = mStore.batch();
        List<Task<Void>> sendInvites = new ArrayList<>();

        for (final User_Model user : userList) {
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

    public void editGroup(final Group_Model groupBase) {
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

    public Task<List<User_Model>> fetchGroupMembers(Group_Model group){
        CollectionReference groupMembers = groupCollection.document(group.getOriginalName()).collection(MEMBERS);

        return groupMembers.get().continueWith(new Continuation<QuerySnapshot, List<User_Model>>() {
            @Override
            public List<User_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if(task.isSuccessful()){
                    List<User_Model> memberList = new ArrayList<>();
                    if(task.getResult()!=null){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            memberList.add(document.toObject(User_Model.class));
                        }
                        return memberList;
                    }
                }
                return null;
            }
        });
    }


    public Task<Void> addUserToGroup(final Group_Model group) {
        String groupName = group.getOriginalName();
        final CollectionReference groupsMembers = groupCollection.document(groupName).collection(MEMBERS);
        final CollectionReference groupInvites = userCollection.document(mUserEmail).collection(GROUP_INVITES);
        final CollectionReference invitedMembers = groupCollection.document(groupName).collection(INVITED);

        CollectionReference myGroups = userCollection.document(mUserEmail).collection(GROUPS);
        return myGroups.document(group.getOriginalName()).set(group).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                if (task.isSuccessful()) {
                    Log.i(TAG,"Successfully added Group to user's GroupCollection");
                    return groupsMembers.document(mUserEmail).set(curUserModel);
                } else {
                    Log.i(TAG,"Error adding user to Group Members collection.", task.getException());
                    return null;
                }
            }
        }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                if (task.isSuccessful()) {
                    Log.i(TAG,"Successfully deleted invite from user group invite collection.");
                    return groupInvites.document(group.getOriginalName()).delete();
                } else {
                    Log.w(TAG,"Unable to delete: "+ group.getOriginalName()+" " +task.getException());
                    return null;
                }
            }
        }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                return invitedMembers.document(mUserEmail).delete();
            }
        });
    }

    public Task<Void> declineGroupInvite(final Group_Model group){
        final String groupName = group.getOriginalName();
        final CollectionReference invitedMembers = groupCollection.document(groupName).collection(INVITED);
        final CollectionReference declinedMembers = groupCollection.document(groupName).collection(DECLINED);
        final CollectionReference groupInvites = userCollection.document(mUserEmail).collection(GROUP_INVITES);

        return invitedMembers.document(mUserEmail).delete().continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                if(task.isSuccessful()){
                    return declinedMembers.document(mUserEmail).set(curUserModel);
                } else {
                    Log.w(TAG,"Error removing user from invited collection for "+groupName);
                    return null;
                }
            }
        }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) throws Exception {
                if(task.isSuccessful()){
                    return groupInvites.document(groupName).delete();
                } else {
                    Log.w(TAG,"Error adding user to declined collection in group "+groupName);
                    return null;
                }
            }
        });
    }

    //*-------------------------------------------ETC--------------------------------------------*//

    public Task<byte[]> getImage(String uri) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
        return imageRef.getBytes(1024 * 1024 * 3);
    }

    //Will currently return a whole list of users to populate the recyclerview for inviting users to event/groups.
    // Todo: Filter our users that have Privacy:private.
    public Task<List<User_Model>> fetchUsers() {
        return userCollection.get().continueWith(new Continuation<QuerySnapshot, List<User_Model>>() {
            @Override
            public List<User_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<User_Model> userList = new ArrayList<>();
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User_Model model = document.toObject(User_Model.class);
                        if(!model.getEmail().equals(mUserEmail)){
                            userList.add(model);
                        }
                    }
                } else if (task.getResult() == null) {
                    Log.w(TAG, "There are no users to fetch.");
                }
                return userList;
            }
        });
    }

    // Task to upload an image and on success return the downloadUri.
    public Task<Uri> uploadImage(Uri uri, String groupName) {
        final StorageReference childStorage = mStorageRef.child("images/" + groupName);
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

    public Task<Group_Model> getGroup() {
        return null;
    }

}