/*
 * Creating a FireBase Repository allows for better separation of concerns as recommended in the
 * MVVM principles.
 *
 * We are not implementing a FireBase DAO, not sure if it is required.
 */

package apps.raymond.friendswholift.Groups;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class FirebaseRepository {

    private static final String TAG = "GroupRepostiry";
    private static final String GROUP_COLLECTION = "Groups";
    private static final String USER_COLLECTION = "Users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = db.collection(GROUP_COLLECTION);
    private CollectionReference userCollection = db.collection(USER_COLLECTION);
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public FirebaseRepository(){

    }

    //@Override //Should we store groupBase.getName() into a variable? Is it quicker?
    // ToDo: Need to check if the GroupName is already used, if yes throw error.
    public Task<Void> createGroup(final GroupBase groupBase){
        Log.i(TAG,"Creating group " + groupBase.getName());
        return groupCollection.document(groupBase.getName()).set(groupBase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Successfully added Document: " + groupBase.getName());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Failed to create group: " + groupBase.getName());
                    }
                });
    }

    public Task<Void> addGroupToUser(final String groupName){
        Log.i(TAG,"Adding group " + groupName + " to user " + currentUser.getUid());
        return userCollection.document(currentUser.getUid()).set(groupName, SetOptions.merge());
    }

    public Task<Void> createUserDoc(){
        Log.i(TAG, "Creating a collection for user " + currentUser.getUid());
        return userCollection.document(currentUser.getUid()).set("test");
    }
}
