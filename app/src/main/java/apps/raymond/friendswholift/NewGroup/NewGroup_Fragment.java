package apps.raymond.friendswholift.NewGroup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import apps.raymond.friendswholift.DialogFragments.YesNoDialog;
import apps.raymond.friendswholift.R;

public class NewGroup_Fragment extends Fragment implements View.OnClickListener,
        YesNoDialog.YesNoInterface {

    public String groupName, descText;

    Spinner invite_Spinner;
    private EditText desc_Txt;
    private TextInputEditText name_Txt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.new_group,container,false);

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
                if(TextUtils.isEmpty(desc_Txt.getText().toString())){
                    descText = null; //ToDo: check if we need this null assignment.
                }
                groupName = name_Txt.getText().toString();
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
        Toast.makeText(getContext(),"Clicked on the positive button", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void negativeClick() {
        Toast.makeText(getContext(),"Clicked on the negative button", Toast.LENGTH_SHORT).show();
    }
}

