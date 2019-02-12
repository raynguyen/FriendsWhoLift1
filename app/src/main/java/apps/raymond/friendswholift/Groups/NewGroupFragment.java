/*
 * ToDo:
 * When the user hits the confirm button, we want to send the user back to the MyGroups screen with
 * an updated RecyclerView that includes the new group.
 */

package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModelProviders;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

import apps.raymond.friendswholift.DialogFragments.YesNoDialog;
import apps.raymond.friendswholift.R;

public class NewGroupFragment extends Fragment implements View.OnClickListener,
        YesNoDialog.YesNoInterface {
    private static final String TAG = "NewGroupFragment";
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
        View view = inflater.inflate(R.layout.group_new_frag,container,false);

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
        mGroupViewModel = ViewModelProviders.of(getActivity()).get(GroupsViewModel.class);
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

        DialogFragment dialog = new YesNoDialog();
        dialog.setTargetFragment(this, 0);
        dialog.show(getActivity().getSupportFragmentManager(),"yesno_dialog");
    }

    @Override
    public void positiveClick() {
        // Add option for photo, if photo is taken, create the group, add image to storage, when the
        // add image to storage is complete, take the uri and add it to the group's field.
        GroupBase groupBase = new GroupBase(groupName, descText, currentUser.getUid(),"public","owner", null);

        Log.i(TAG,"Created a new GroupBase object with name " + groupBase.getName());
        mGroupViewModel.createGroup(groupBase);
    }

    @Override
    public void negativeClick() {
        Toast.makeText(getContext(),"Clicked on the negative button", Toast.LENGTH_SHORT).show();
    }

}

