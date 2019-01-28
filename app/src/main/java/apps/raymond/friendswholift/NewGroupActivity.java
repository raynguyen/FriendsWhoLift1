/*
 * When the back button is pressed, we want to save the current state of the activity. Upon resume,
 * should load the previous state of the activity prior to the back press.
 */
package apps.raymond.friendswholift;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewGroupActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String TAG = "NewGroupActivity";
    private static final String GROUP_COLLECTION = "Groups";

    private Spinner invite_Spinner;
    private Button new_group_Btn;
    private TextInputEditText groupName_Txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        groupName_Txt = findViewById(R.id.group_name_txt);

        new_group_Btn = findViewById(R.id.create_grp_btn);
        new_group_Btn.setOnClickListener(this);

        invite_Spinner = findViewById(R.id.invite_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(NewGroupActivity.this, R.array.array_invite_authorize,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        invite_Spinner.setAdapter(adapter);
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
                // Code to create new Collection in Cloud FireStore.
                if (groupName_Txt.getText() != null){
                    String grpName = groupName_Txt.getText().toString();
                    createGroup(grpName);
                } else {
                    groupName_Txt.setError("What is the name of this group?");
                    Log.e(TAG, "Null result when trying to reference the Group Name.");
                }
                break;
        }
    }

/*
 * Whenever a new group is created, the group automatically assigns the current FirebaseUser as
 * the owner for the group.
 */
    private void createGroup(@NonNull final String name){
        Map<String, String > group = new HashMap<>();
        try {
            Log.d(TAG, "Attempting to create a new collection in cloud firestore.");
            group.put(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "owner");

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
}
