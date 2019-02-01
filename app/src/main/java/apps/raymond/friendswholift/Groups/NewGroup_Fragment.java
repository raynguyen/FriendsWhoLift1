package apps.raymond.friendswholift.Groups;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

import apps.raymond.friendswholift.DialogFragments.YesNoDialog;
import apps.raymond.friendswholift.R;

public class NewGroup_Fragment extends Fragment implements View.OnClickListener,
        YesNoDialog.YesNoInterface {
    private static final String TAG = "NewGroup_Fragment";
    private static final String GROUP_COLLECTION = "Groups";
    private static final String USER_COLLECTION = "Users";

    public String groupName, descText;

    Spinner invite_Spinner;
    EditText desc_Txt;
    private TextInputEditText name_Txt;
    private FirebaseUser currentUser;
    private GroupsViewModel mGroupViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.group_new,container,false);

        Button discard_Btn = view.findViewById(R.id.discard_grp_btn);
        Button create_Btn = view.findViewById(R.id.create_grp_btn);
        discard_Btn.setOnClickListener(this);
        create_Btn.setOnClickListener(this);

        name_Txt = view.findViewById(R.id.group_name_txt);
        desc_Txt = view.findViewById(R.id.desc_txt);

        invite_Spinner = view.findViewById(R.id.invite_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.array_invite_authorize,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        invite_Spinner.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mGroupViewModel = new GroupsViewModel();
        return view;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i){
            case R.id.create_grp_btn:
                // Want to show a dialog with a recap of the group and get user to confirm.
                if(TextUtils.isEmpty(name_Txt.getText().toString())) {
                    name_Txt.setError("Must specify a name.");
                    return;
                }
                groupName = name_Txt.getText().toString();
                descText = desc_Txt.getText().toString();
                confirmDialog();
                break;
            case R.id.discard_grp_btn:
                break;
        }
    }

    private void confirmDialog(){
        /*
        Create the body for the dialog here as an argument and pass it over.
         */
        DialogFragment dialog = new YesNoDialog();
        dialog.setTargetFragment(this, 0);
        dialog.show(getActivity().getSupportFragmentManager(),"yesno_dialog");
    }

    @Override
    public void positiveClick() {
        Log.i(TAG, "Attempting to create a group Document of name: " + groupName);
        Toast.makeText(getContext(),"Clicked on the positive button", Toast.LENGTH_SHORT).show();
        GroupBase groupBase = new GroupBase(groupName, descText, currentUser.getUid(),"public","owner", Arrays.asList("420","sports")); //Look into this Arrays.asList thing
        mGroupViewModel.createGroup(groupName, groupBase);
        //createGroup(this.groupName);
        attachGroup(this.groupName);
    }

    @Override
    public void negativeClick() {
        Toast.makeText(getContext(),"Clicked on the negative button", Toast.LENGTH_SHORT).show();
    }

    /*
     * THIS COULD PROBABLY EXIST AS A SEPARATE METHOD! Consider future calls to save tags to users.
     * Store a user's associated groups under the database collection 'Users'.
     * The UID of each user is used to create a new document or access the existing document in
     * the Users collection.
     * Avoiding using the user email as I plan to implement new authentication tools.
     */
    public void attachGroup(@NonNull final String groupName){
        try{
            Log.i(TAG,"Attempting to attach the new group as a field in the User document.");
            FirebaseFirestore.getInstance().collection(USER_COLLECTION).
                    document(currentUser.getUid()).update(groupName, "Add authorization here");
            Log.i(TAG,"The User Document contains these tags: " +
                    FirebaseFirestore.getInstance().collection(USER_COLLECTION).
                            document(currentUser.getUid()).get());

        } catch (NullPointerException npe){
            Log.w(TAG,"Unable to attach new group to the current user.");
            Toast.makeText(getContext(),"There was an error resolving the group. Please try again shortly!",Toast.LENGTH_SHORT).show();
        }
    }
}

