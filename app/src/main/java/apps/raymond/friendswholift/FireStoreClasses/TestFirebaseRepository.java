package apps.raymond.friendswholift.FireStoreClasses;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                            Log.i(TAG,"Successfully retrieved the document.");
                            return document.getData().keySet();
                        } else {
                            Log.w(TAG, "Error when retrieving the Document.");
                            return null;
                        }
                    }
                });
    }

    /*
     * Query Firestore against a List of names. For each name in the list, retrieve the POJO of that
     * document and append it to a List that will be returned to the context Caller.
     */
    public List<Task<DocumentSnapshot>> getGroups(List<String> myGroupTags){
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

    // Can simply just retrieve a object and create it here. Then in the Fragment call upon each item to retrieve its photo in a separate method.
    public List<Task<GroupBase>> getGroupsTest(List<String> myGroupTags){

        final List<Task<GroupBase>> myGroupsTest = new ArrayList<>();

        for(final String name : myGroupTags){
            Log.i(TAG, "Attempting to get Group: " + name);
            // Get the Group Document first, then once the document is retrieved, get the gcsURI and use that to retrieve the photo.

            //GroupBase myGroupTask;
            Task<GroupBase> myGroupTask = groupCollection.document(name).get()
                    .continueWith(new Continuation<DocumentSnapshot, GroupBase>() {
                        @Override
                        public GroupBase then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                            Log.i(TAG,"Creating Group: " + name);
                            GroupBase myGroup = task.getResult().toObject(GroupBase.class);
                            return myGroup;
                        }
                    });
                    /*
                    .continueWithTask(new Continuation<GroupBase, Task<GroupBase>>() {
                        @Override
                        public Task<GroupBase> then(@NonNull Task<GroupBase> task) throws Exception {
                            String photoURI = task.getResult().getGcsURI();
                            Task<byte[]> getPhotoTask = firebaseStorage.getReferenceFromUrl(photoURI).getBytes(1024*1024*2);
                            return null;
                        }
                    });
                    */

            /*
            Task<byte[]> groupPhotoTask =  groupCollection.document(name).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.i(TAG,"Successfully retrieved DocumentSnapshot of: " + name);
                        }
                    })
                    .continueWithTask(new Continuation<DocumentSnapshot, Task<byte[]>>() {
                        @Override
                        public Task<byte[]> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                            Log.i(TAG,"Attempting to retrieve the photo via URI of: "+name);
                            String documentURI = task.getResult().getString("gcsURI");
                            //Task<byte[]> groupPhoto = firebaseStorage.getReferenceFromUrl(documentURI).getBytes(1024*1024*2); //This means 2MB
                            //myGroupsTest.add(groupPhoto);
                            final GroupBase myGroup = task.getResult().toObject(GroupBase.class);
                            Task<byte[]> groupPhoto = firebaseStorage.getReferenceFromUrl(documentURI).getBytes(1024*1024*2);
                            return groupPhoto;
                        }
                    }).continueWith(new Continuation<byte[], GroupBase>() {
                        @Override
                        public GroupBase then(@NonNull Task<byte[]> task) throws Exception {
                            return new byte[0];
                        }
                    });
            */

            myGroupsTest.add(myGroupTask);
        }

        return myGroupsTest;
    }

}