/*
 * Creating a FireBase Repository allows for better separation of concerns as recommended in the
 * MVVM principles.
 *
 * We are not implementing a FireBase DAO, not sure if it is required.
 */

package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.List;

import javax.annotation.Nullable;

public class FirebaseRepository {

    private static final String TAG = "GroupRepostiry";
    private static final String GROUP_COLLECTION = "Groups";
    private static final String USER_COLLECTION = "Users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groupCollection = db.collection(GROUP_COLLECTION);
    private CollectionReference userCollection = db.collection(USER_COLLECTION);
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
        Log.i(TAG,"Creating a collection for user " + currentUser.getUid());
        return userCollection.document(currentUser.getUid()).set("test");
    }
    //https://firebase.google.com/docs/firestore/query-data/get-data
    public Task<LiveData<List<String>>> getGroups(){
        Log.i(TAG,"Attempting to retrieve a user's groups.");
        userCollection.document(currentUser.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null){
                            Log.w(TAG,"Listen failed.", e);
                            return;
                        }
                        if (snapshot != null && snapshot.exists() ){
                            Log.d(TAG,"Current data: " + snapshot.getData());
                        } else {
                            Log.d(TAG,"Current data: null");
                        }
            }
        });
        return null;
    }

}
