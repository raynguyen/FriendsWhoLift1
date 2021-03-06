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
 *
 *  * When a user registers to or creates an event, the following is the task priority in order to
 *      * minimize errors in the corresponding Firestore collections.
 *      *
 *      * If the user is creating an Event:
 *      * 0. Add the Event POJO to the Events collection, then
 *      *
 *      * If user opts to attending an event:
 *      * 1. Add the Event POJO to the User->Events Collection.
 *      * 2. Add the User POJO to the Events->User Collection
 *      * 3a. Update the Event Document inside the Events Collection with the modified attendees count.
 *      * 3b. Remove event invitations if the User opts to attend via an Event Invitation.
 *      * 3c. Update the Event Document to reflect changes in invitation count is applicable.
 *
 */

package apps.raymond.kinect.Model;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.EventDetail.Message_Model;
import apps.raymond.kinect.ObjectModels.User_Model;

/**
 * Repository class to abstract data communication between the application and Firebase backend.
 *
 * ToDo: Check to see how many times this Repo is instantiated. We want it to be only once, when the CoreActivity loads. -> Static utility class // injection?
 */
public class DataModel {
    private static final String TAG = "DataModel";
    private static final String USERS = "Users";
    private static final String VISIBILITY = "visibility";
    private static final String PUBLIC = "public";
    private static final String EVENTS = "Events";
    private static final String MESSAGES = "Messages";
    private static final String ACCEPTED = "Accepted";
    private static final String INVITED = "Invited";
    private static final String DECLINED = "Declined";
    private static final String LOCATIONS = "Locations";
    private static final String INVITATIONS = "EventInvites"; //todo change to Invitations
    private static final String EVENT_INVITED_FIELD = "Invited";
    private static final String ATTENDING = "Attending";
    private static final String PENDING_REQUESTS = "PendingRequests";
    private static final String CONNECTIONS = "Connections";
    private static final String CONNECTION_REQUESTS = "ConnectionRequests";
    private static final String PRIVACY = "privacy";//todo cap
    private static final String CREATION_DATE = "create";//todo cap

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private CollectionReference userCollection = mStore.collection(USERS);
    private CollectionReference eventCollection = mStore.collection(EVENTS);

    private MutableLiveData<QuerySnapshot> mSuggestedEvents = new MutableLiveData<>();

    /*ToDo: There should be no Java code in the Repository class.
        This method should not hold the mUserEmail or curUserMode, these should be passed as required to repository calls.
    */
    public DataModel() {
    }

    //GOOD
    /**
     * Create a user document in the Event's attending collection. Upon successful write, we update
     * the ATTENDING field in the Event document which will then add the Event document to the user's
     * Events collection.
     *
     * @param eventID The event the user is attending.
     */
    public Task<Void> addUserToEvent(String userID, User_Model userModel, String eventID, Event_Model event_model) {
        DocumentReference eventDocument = eventCollection.document(eventID);
        return eventDocument.collection(ACCEPTED).document(userID).set(userModel)
                .continueWithTask((@NonNull Task<Void> task) -> {
                    return eventDocument.update(ATTENDING, FieldValue.increment(1));
                })
                .continueWithTask((@NonNull Task<Void> task) -> {
                    return addEventToUser(userID, event_model);
                });
    }
    //*------------------------------------------USER-------------------------------------------*//

    /**
     * Returns a task that will convert a Firestore document to a User_Model.class.
     * @param userID Index field used to retrieve the correct document.
     * @return User_Model POJO.
     */
    public Task<User_Model> getUserModel(String userID){
        return userCollection.document(userID).get().continueWith((Task<DocumentSnapshot> task)-> {
            if(task.getResult()!=null){
                return task.getResult().toObject(User_Model.class);
            }
            return null;
        });
    }

    //ToDo: this needs to check if the document already exists prior to attempting to create.
    public Task<Void> createNewUserDocument(User_Model userModel) {
        return userCollection.document(userModel.getEmail()).set(userModel);
    }

    public Task<List<Event_Model>> getEventInvitations(String userID) {
        return mStore.collection(USERS).document(userID).collection(INVITATIONS).get()
                .continueWith((Task<QuerySnapshot> task)->{
                    List<Event_Model> eventInvites = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            eventInvites.add(document.toObject(Event_Model.class));
                        }
                    }
                    return eventInvites;
        });
    }

    public Task<List<User_Model>> getConnectionRequests(String userID){
        return userCollection.document(userID).collection(CONNECTION_REQUESTS).get()
                .continueWith((@NonNull Task<QuerySnapshot> task)-> {
                    List<User_Model> connectionRequests = new ArrayList<>();
                    if(task.isSuccessful()&& task.getResult()!=null){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            connectionRequests.add(document.toObject(User_Model.class));
                        }
                    }
                    return connectionRequests;
                });
    }

    /**
     * Check a profile if the current user has a pending connection request with the profile.
     * @param userID current user's ID.
     * @param profileID profile ID to check for a pending connection request.
     * @return true if the task is completed and the request exists, otherwise false.
     */
    public Task<Boolean> checkForPendingConnection(String userID, String profileID){
        return userCollection.document(profileID).collection(CONNECTION_REQUESTS).document(userID).get()
                .continueWith((@NonNull Task<DocumentSnapshot> task)->{
                    return task.getResult().exists();
                });
    }

    /**
     * Delete a connection request from a user's ConnectionRequest collection.
     * @param userId the ID of which we are deleting a ConnectionRequest.
     * @param profileID the ID of the user who sent the connection request.
     * @return a task that contains information regarding the CRUD operation.
     */
    public Task<Void> deleteConnectionRequest(String userId, String profileID){
        return userCollection.document(userId).collection(CONNECTION_REQUESTS).document(profileID).delete();
    }

    /**
     * Delete a pending connection document from the requester's PendingConnection collection.
     * @param userID the ID of the user of which we are deleting the pending connection request.
     * @param profileID the ID of the profile who sent the connection request.
     * @return a task containing information on the CRUD operation.
     */
    public Task<Void> deletePendingRequest(String userID, String profileID){
        return userCollection.document(profileID).collection(PENDING_REQUESTS).document(userID).delete();
    }

    /**
     * Create a document to request for a connection with a profile.
     * @param userID string identifier of user who is sending the request.
     * @param userModel User_Model POJO of the user who is sending the request.
     * @param profileID string identifier of the profile of which the user is trying to connect with.
     * @return task to determine that is used by the application context to determine if successful
     */
    public Task<Void> requestUserConnection(String userID, User_Model userModel, String profileID,
                                            User_Model profileModel){
        return userCollection.document(profileID).collection(CONNECTION_REQUESTS).document(userID)
                .set(userModel).continueWithTask((@NonNull Task<Void> task)->{
                    return userCollection.document(userID).collection(PENDING_REQUESTS).document(profileID)
                            .set(profileModel);
                });
    }

    public Task<Void> createUserConnection(final String userID, final User_Model profile){
        return userCollection.document(userID).collection(CONNECTIONS)
                .document(profile.getEmail())
                .set(profile)
                .continueWithTask((Task<Void> task)->{
                    if(task.isSuccessful()){
                        return userCollection.document(userID).update("numconnections", FieldValue.increment(1));
                    }
                    return null;
                });
    }

    public Task<Void> deleteUserConnection(final String userID, String  connectionID){
        return userCollection.document(userID).collection("Connections")
                .document(connectionID).delete()
                .continueWithTask((Task<Void> task)-> {
                    return userCollection.document(userID).update("numconnections", FieldValue.increment(-1));
                });
    }

    /**
     * Check a profile to determine if the current user is connected with the profile.
     * @param userID current user's ID.
     * @param profileID profile ID to check for a pending connection request.
     * @return true if the task is completed and a connection exists, otherwise false.
     */
    public Task<Boolean> checkForConnection(String userID, String profileID){
        return userCollection.document(userID).collection(CONNECTIONS).document(profileID).get()
                .continueWith((@NonNull Task<DocumentSnapshot> task)->{
                    return task.getResult().exists();
                });
    }

    public void declineEventInvitation(String eventName, String userID,User_Model user){
        eventCollection.document(eventName).collection(DECLINED).document(userID).set(user);
    }

    /**
     * Deletes an event invitation document from the database for a particular user and event. The
     * CRUD operations proceed in the following order:
     *  1. Delete the event invitation document from the user's Invitations collection.
     *  2. Delete the user document from the event's Invited collection.
     *  3. Decrement the Invited field from the event's document.
     * @param eventID The name of the event being modified.
     */
    public Task<Void> deleteEventInvitation(String userID, String eventID){
        DocumentReference userDocument = userCollection.document(userID);
        DocumentReference eventDocument = eventCollection.document(eventID);
        return userDocument.collection(INVITATIONS).document(eventID).delete()
                .continueWithTask((@NonNull Task<Void> task) -> {
                    return eventDocument.collection(INVITED).document(userID).delete();
                })
                .continueWithTask((@NonNull Task<Void> task) -> {
                    return eventDocument.update(INVITED, FieldValue.increment(-1));
                });
    }

    /**
     * Queries the database for a list of a user's connections.
     * @param userID The user of which we want to query a list of user connections for.
     * @return A task that returns a list of user models representing the userID's connections.
     */
    public Task<List<User_Model>> getUserConnections(String userID){
        return userCollection.document(userID).collection("Connections").get()
                .continueWith((@NonNull Task<QuerySnapshot> task)-> {
                    List<User_Model> results = new ArrayList<>();
                    if(task.isSuccessful() && task.getResult()!=null){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            results.add(document.toObject(User_Model.class));
                        }
                    }
                    return results;
                });
    }

    /**
     * Creates a document in Events using the event POJO.
     * @param event The created Event POJO.
     * @return Task
     */
    public Task<Void> createEvent(final Event_Model event) {
        DocumentReference eventRef = eventCollection.document(event.getName());
        //ToDo: We currently overwrite any events with the same name. Need to determine an id method.
        return eventRef.set(event);
    }

    /**
     * Create the event document in Users->Events.
     * @param event The event to add to the current user.
     */
    public Task<Void> addEventToUser(String userID, Event_Model event) {
        return userCollection.document(userID).collection(EVENTS)
                .document(event.getName()).set(event);
    }

    //Todo: Revisit this method to invite users to an event.
    public void sendEventInvites(final Event_Model event, final List<User_Model> userList) {
        final DocumentReference eventDoc = eventCollection.document(event.getName());
        final CollectionReference eventInvitedCol = eventCollection.document(event.getName()).collection(INVITED);
        final WriteBatch inviteBatch = mStore.batch();
        final List<Task<Void>> sendInvites = new ArrayList<>();

        for (final User_Model user : userList) {
            sendInvites.add(userCollection.document(user.getEmail()).collection(INVITATIONS)
                    .document(event.getName()).set(event, SetOptions.merge())
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

    public Task<Event_Model> getEventModel(String eventName) {
        return eventCollection.document(eventName).get().continueWith((Task<DocumentSnapshot> task) -> {
            if (task.getResult() != null) {
                return task.getResult().toObject(Event_Model.class);
            }
            return null;
        });
    }

    public Task<List<Event_Model>> getAcceptedEvents(String userID){
        return mStore.collection(USERS).document(userID).collection(EVENTS).orderBy("long1", Query.Direction.ASCENDING)
                .get().continueWith((Task<QuerySnapshot> task)->{
                    List<Event_Model> result = new ArrayList<>();
                    if(task.isSuccessful() && task.getResult()!=null){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            result.add(document.toObject(Event_Model.class));
                        }
                    }
                    return result;
                });
    }

    public Task<List<User_Model>> getEventInvited(String eventName) {
        return mStore.collection(EVENTS).document(eventName).collection(INVITED).get()
                .continueWith(new Continuation<QuerySnapshot, List<User_Model>>() {
                    @Override
                    public List<User_Model> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        List<User_Model> userList = new ArrayList<>();
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

    public Task<List<User_Model>> getEventAttending(String eventName) {
        return eventCollection.document(eventName).collection(ACCEPTED).get()
                .continueWith((@NonNull Task<QuerySnapshot> task)-> {
                    List<User_Model> results = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult()!=null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            results.add(document.toObject(User_Model.class));
                        }
                    }
                    return results;
                });
    }

    public Task<Void> postMessage(final String eventName,final Message_Model message){
        return mStore.collection(EVENTS).document(eventName).collection(MESSAGES).document()
                .set(message);
    }

    public Task<List<Message_Model>> getEventMessages(@NonNull String eventName){
        return mStore.collection(EVENTS).document(eventName).collection(MESSAGES).get()
                .continueWith((Task<QuerySnapshot> task)-> {
                    List<Message_Model> result = new ArrayList<>();
                    if(task.isSuccessful() && task.getResult()!=null){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            result.add(document.toObject(Message_Model.class));
                        }
                    }
                    return result;
                });
    }

    public void leaveEvent(String userID, String eventName){
        //TODO: Delete the user model from event attending.
        //--Decrement attending count.
        //Delete the event from user's accepted events.
    }

    //ToDo: This needs to be changed such that one completion, we set the result to a LiveData object
    // held by this class. The ViewModels will then observe this LiveData and changes to it trigger transformations.
    /**
     * Query the Events Collection for a List of Event_Models whose privacy field is public.
     * @return list of Event_Models held be a task's result.
     */
    public Task<List<Event_Model>> getPublicEvents(){
        Query query = eventCollection.whereEqualTo(PRIVACY,Event_Model.PUBLIC).orderBy(CREATION_DATE).limit(50);//.whereArrayContains("primes",Event_Model.SPORTS).limit(3);
        return query.get().continueWith((@NonNull Task<QuerySnapshot> task)-> {
            List<Event_Model> result = new ArrayList<>();
            if(task.isSuccessful() && task.getResult()!=null){
                mSuggestedEvents.setValue(task.getResult());
                for(QueryDocumentSnapshot document: task.getResult()){
                    result.add(document.toObject(Event_Model.class));
                }
            }
            return result;
        });
    }

    public Task<Boolean> checkForUser(String userID, String eventName){
        return eventCollection.document(eventName).collection(ACCEPTED).document(userID).get()
                .continueWith((@NonNull Task<DocumentSnapshot> task)->{
                    if(task.isSuccessful()){
                        return task.getResult().exists();
                    }
                    return false;
                });
    }

    //*-------------------------------------------ETC--------------------------------------------*//
    // Todo: Filter our users that have Privacy:private. Consider removing the 'code' out to viewmodel
    //  or to an application class.
    /**
     * Query FireStore for a list of all users registered to the application.
     * @param userID parameter so that we exclude the current user from the result list.
     * @return a list of all users in the application.
     */
    public Task<List<User_Model>> getAllUsers(final String userID) {
        return userCollection.get().continueWith((@NonNull Task<QuerySnapshot> task)-> {
            List<User_Model> userList = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User_Model model = document.toObject(User_Model.class);
                    if(!model.getEmail().equals(userID)){
                        userList.add(model);
                    }
                }
            }
            return userList;
        });
    }

    /**
     * Query database for a List of user's who have set their Privacy field to "PUBLIC".
     *
     * @return list of user models with public privacy fields.
     */
    public Task<List<User_Model>> getPublicUsers(String userID){
        Query publicUsersQuery = userCollection.whereEqualTo(VISIBILITY, PUBLIC);
        return publicUsersQuery.get().continueWith((@NonNull Task<QuerySnapshot> task) -> {
            List<User_Model> result = new ArrayList<>();
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot document : task.getResult()){
                    User_Model user = document.toObject(User_Model.class);
                    result.add(user);
                }
            }
            return result;
        });
    }

    // Task to upload an image and on success return the downloadUri.
    public Task<Uri> uploadImage(Uri uri, String groupName) {
        final StorageReference childStorage = mStorageRef.child("images/" + groupName);
        return childStorage.putFile(uri)
                .continueWithTask((@NonNull Task<UploadTask.TaskSnapshot> task)-> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successfully uploaded image to storage.");
                        return childStorage.getDownloadUrl();
                    }
                    return null;
                });
    }

    public Task<byte[]> getImage(String uri) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
        return imageRef.getBytes(1024 * 1024 * 3);
    }

    public Task<List<Location_Model>> getUsersLocations(String userID){
        return userCollection.document(userID).collection(LOCATIONS).get()
                .continueWith((@NonNull Task<QuerySnapshot> task)-> {
                    List<Location_Model> results = new ArrayList<>();
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            results.add(document.toObject(Location_Model.class));
                        }
                    }
                    return results;
                });
    }

    public Task<Void> addLocationToUser(final String userID, Location_Model locationModel){
        return userCollection.document(userID).collection("Locations")
                .document(locationModel.getLookup()).set(locationModel)
                .continueWithTask((Task<Void> task)->{
                    if(task.isSuccessful()){
                        return userCollection.document(userID).update("numlocations",FieldValue.increment(1));
                    }
                    return null;
                });
    }

    //REGISTRATION AND LOGIN
    public Task<Boolean> signInViaEmail(String name, String pw){
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(name, pw)
                .continueWith((@NonNull Task<AuthResult> task)-> task.isSuccessful());
    }

    public Task<Boolean> registerViaEmail(String name, String pw){
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(name, pw)
                .continueWith((@NonNull Task<AuthResult> task)-> task.isSuccessful());
    }

}
