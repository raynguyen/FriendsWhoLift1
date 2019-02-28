/*
 * On back click, should prompt the user if they want to completely discard any progress.
 *
 * Need to decide on how to select the group to attach event to.
 */

package apps.raymond.friendswholift.Events;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.DialogFragments.YesNoDialog;
import apps.raymond.friendswholift.R;

public class Event_Create_Fragment extends Fragment implements View.OnClickListener,
        YesNoDialog.YesNoInterface, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "Event_Create_Fragment";
    EditText nameTxt;
    EditText descTxt;
    EditText dayTxt;
    EditText monthTxt;
    RadioGroup privacyGroup;
    TextView tagsContainer;
    EditText tagsTxt;
    String privacy;
    public Event_Create_Fragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public static Event_Create_Fragment newInstance(){
        return new Event_Create_Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.event_create_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameTxt = view.findViewById(R.id.event_name_txt);
        descTxt = view.findViewById(R.id.event_desc_txt);
        dayTxt = view.findViewById(R.id.event_day);
        monthTxt = view.findViewById(R.id.event_month);

        Button dateBtn = view.findViewById(R.id.date_btn);
        dateBtn.setOnClickListener(this);

        Button saveBtn = view.findViewById(R.id.save_btn);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        privacyGroup = view.findViewById(R.id.privacy_buttons);
        privacyGroup.clearCheck();
        privacyGroup.setOnCheckedChangeListener(this);

        ImageButton addTagsBtn = view.findViewById(R.id.event_tag_add_btn);
        addTagsBtn.setOnClickListener(this);

        tagsTxt = view.findViewById(R.id.event_tags_txt);
        tagsContainer = view.findViewById(R.id.tags_container_txt);

        testList = new ArrayList<>();
        tagsList = new ArrayList<>();
    }

    ArrayList<String> tagsList;
    List<ClickableSpan> testList;
    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.save_btn:
                if(checkFields()){
                    return;
                }
                GroupEvent newEvent = new GroupEvent(
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        nameTxt.getText().toString(),
                        descTxt.getText().toString(),
                        monthTxt.getText().toString(),
                        dayTxt.getText().toString(),
                        privacy,
                        tagsList);

                Log.i(TAG,"Created new GroupEvent of name: "+ newEvent.getOriginalName());

                EventViewModel eventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
                eventViewModel.createEvent(newEvent); //Adds a new Event to the Events collection.
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                break;

            case R.id.cancel_btn:
                DialogFragment dialog = new YesNoDialog();
                dialog.setTargetFragment(this, 0);
                dialog.show(getActivity().getSupportFragmentManager(),"yesno_dialog");
                break;

            case R.id.event_tag_add_btn:
                Log.i(TAG,"Adding tag to event.");
                // When this button is pressed, add the String to a container below the TextView that displays all the tags that have been added
                // Research a way of getting the individual strings as clickable objects.

                tagsList.add(tagsTxt.getText().toString());
                tagsContainer.setText(tagsList.toString());
                tagsTxt.getText().clear();

                break;

            case R.id.date_btn:
                DialogFragment newFragment = new TimePickerDialog();
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton checkedBtn = group.findViewById(checkedId);
        privacy = checkedBtn.getText().toString();
    }

    /*
     * This method is called before creating an Event instance.
     * Checks that all fields are filled and are valid inputs.
     * True if fields require attention.
     */
    private boolean checkFields(){
        //Highlight the necessary fields that are empty.
        //Display a toast.
        boolean check = false;
        if(nameTxt.getText().toString().isEmpty()){
            Log.i(TAG,"Name EditText is empty.");
            Toast.makeText(getContext(),"Finish filling out the crap wtf.",Toast.LENGTH_SHORT).show();
            check = true;
        }
        if(privacyGroup.getCheckedRadioButtonId() == -1){
            Log.i(TAG,"Privacy has not been selected.");
            check = true;
        }
        return check;
    }

    @Override
    public void positiveClick() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void negativeClick() {
        // Do nothing?
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Inflating seems to do nothing.
        Log.i(TAG,"IN THE ONCREATEOPTIONSMENU FOR FRAGMENT.");
        inflater.inflate(R.menu.group_edit_toolbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_profile).setVisible(false);
    }
}
