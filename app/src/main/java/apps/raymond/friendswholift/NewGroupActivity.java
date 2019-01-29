/*
 * When the back button is pressed, we want to save the current state of the activity. Upon resume,
 * should load the previous state of the activity prior to the back press.
 * Once a group is completed, an activity to shown the group page should be launched.
 *
 */
package apps.raymond.friendswholift;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import apps.raymond.friendswholift.DialogFragments.YesNoDialog;

public class NewGroupActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener, YesNoDialog.YesNoInterface {
    private static final String TAG = "NewGroupActivity";
    private static final String GROUP_COLLECTION = "Groups";
    private static final String USER_COLLECTION = "Users";

    public String groupName, desc;

    Spinner invite_Spinner;
    Button new_group_Btn;
    private TextInputEditText groupName_Txt;
    TextView desc_Txt;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        groupName_Txt = findViewById(R.id.group_name_txt);
        desc_Txt = findViewById(R.id.desc_txt);

        new_group_Btn = findViewById(R.id.create_grp_btn);
        new_group_Btn.setOnClickListener(this);

        invite_Spinner = findViewById(R.id.invite_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(NewGroupActivity.this, R.array.array_invite_authorize,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        invite_Spinner.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG,"Clicked on a spinner item.");

        Toast.makeText(this,"Clicked on a spinner item",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.create_grp_btn:
                if(TextUtils.isEmpty(groupName_Txt.getText().toString())) {
                    groupName_Txt.setError("Must specify a name.");
                    return;
                }
                if(TextUtils.isEmpty(desc_Txt.getText().toString())){
                    desc = null;
                }
                groupName = groupName_Txt.getText().toString();
                confirmDialog();
                break;
        }
    }

    private void confirmDialog(){
        /*
        Create the body for the dialog here as an argument and pass it over.
         */
        DialogFragment dialog = new YesNoDialog();
        dialog.show(getSupportFragmentManager(),"YesNoDialog");
    }

    /*
     * Attach the description to the group field after calling the createGroup method.
     */
    @Override
    public void positiveClick() {
        //On save, create the group and open go to the group's card in the recyclerview fragment.
        Toast.makeText(this,"shitty positive click",Toast.LENGTH_SHORT).show();
        createGroup(this.groupName);
        attachGroup(this.groupName);
    }

    @Override
    public void negativeClick() {
        /*
        On discard, should clear all contents in the editables and shift the fragment back to the
        recycler view of cards.
         */
        Toast.makeText(this,"NICE NEGGY click",Toast.LENGTH_SHORT).show();
    }

    /*
     * Code to create new Collection in Cloud FireStore.
     * Whenever a new group is created, the group automatically assigns the current FireBaseUser as
     * the owner for the group.
     */
    private void createGroup(@NonNull final String name){
        Map<String, String > group = new HashMap<>();
        try {
            Log.i(TAG, "Attempting to create a new Group document.");
            group.put(currentUser.getUid(), "owner");
            group.put("description",desc);
            group.put("visibility","private");
            group.put("invite","owner");

            FirebaseFirestore.getInstance().collection(GROUP_COLLECTION).document(name).set(group)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG,"Successfully added Document: " + name);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG,"Failed to create group: " + name);
                        }
                    });
        } catch (NullPointerException npe) {
            Log.e(TAG, "NULL user when defining owner of group.");
        }
    }

    /*
     * Store a user's associated groups under the database collection 'Users'.
     * The UID of each user is used to create a new document or access the existing document in
     * the Users collection.
     * Avoiding using the user email as I plan to implement new authentication tools.
     */
    public void attachGroup(@NonNull final String name){
        try{
            Log.i(TAG,"Attempting to attach the new group as a field in the User document.");
            FirebaseFirestore.getInstance().collection(USER_COLLECTION).
                    document(currentUser.getUid()).update(name, "Add authorization here");
        } catch (NullPointerException npe){
            Log.w(TAG,"Unable to attach new group to the current user.");
            Toast.makeText(this,"There was an error resolving the group. Please try again shortly!",Toast.LENGTH_SHORT).show();
        }
    }




}
