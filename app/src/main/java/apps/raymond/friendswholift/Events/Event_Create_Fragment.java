/*
 * On back click, should prompt the user if they want to completely discard any progress.
 *
 * Need to decide on how to select the group to attach event to.
 *
 * One ConstraintLayout that holds the datepicker info and the timepicker info. At the start of the
 * layout, have a calender imagebtn that opens a dialog with a viewpager for the time picker and the date picker.
 * At the end of the image button will be two view layouts with the date on top and the time on the bottom.
 */

package apps.raymond.friendswholift.Events;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import apps.raymond.friendswholift.Add_Users_Adapter;
import apps.raymond.friendswholift.Core_Activity;
import apps.raymond.friendswholift.DialogFragments.YesNoDialog;
import apps.raymond.friendswholift.Interfaces.BackPressListener;
import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.UserProfile.UserModel;

public class Event_Create_Fragment extends Fragment implements View.OnClickListener,
        DatePickerDialog.FetchDate, RadioGroup.OnCheckedChangeListener, Add_Users_Adapter.CheckProfileInterface,
        BackPressListener {
    public static final String TAG = "Event_Create_Fragment";
    private static final int DIALOG_REQUEST_CODE = 21;

    private AddEvent addEventToRecycler;
    public interface AddEvent{
        void addToEventRecycler(GroupEvent groupEvent);
    }

    FragmentManager fm;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            addEventToRecycler = (AddEvent) context;
        }catch (ClassCastException e){
            Log.i(TAG,"Unable to attach AddEvent interface to activity.");
        }

        fm = getActivity().getSupportFragmentManager();
    }

    EventViewModel eventViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        eventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        fetchUsersList();
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

    String startMonth, startDay;
    EditText nameTxt, descTxt;
    TextView startTxt, endTxt;
    RadioGroup privacyGroup;
    TextView tagsContainer;
    EditText tagsTxt;
    String privacy;
    ProgressBar progressBar;
    Add_Users_Adapter userAdapter;
    List<UserModel> usersList, inviteUsersList;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameTxt = view.findViewById(R.id.event_name_txt);
        descTxt = view.findViewById(R.id.event_desc_txt);
        startTxt = view.findViewById(R.id.event_start);
        endTxt = view.findViewById(R.id.event_end);

        ImageButton dateBtn = view.findViewById(R.id.date_btn);
        dateBtn.setOnClickListener(this);

        Button saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);

        progressBar = view.findViewById(R.id.create_progress_bar);

        privacyGroup = view.findViewById(R.id.privacy_buttons);
        privacyGroup.clearCheck();
        privacyGroup.setOnCheckedChangeListener(this);

        ImageButton addTagsBtn = view.findViewById(R.id.event_tag_add_btn);
        addTagsBtn.setOnClickListener(this);

        tagsTxt = view.findViewById(R.id.event_tags_txt);
        tagsContainer = view.findViewById(R.id.tags_container_txt);

        testList = new ArrayList<>();
        tagsList = new ArrayList<>();

        usersList = new ArrayList<>();
        inviteUsersList = new ArrayList<>();
        RecyclerView usersRecycler = view.findViewById(R.id.add_users_recycler);
        usersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new Add_Users_Adapter(usersList, this);
        usersRecycler.setAdapter(userAdapter);
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
                progressBar.setVisibility(View.VISIBLE);
                createEvent();
                break;
            case R.id.cancel_btn:
                getActivity().onBackPressed();
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
                DialogFragment newFragment = new DatePickerDialog();
                newFragment.setTargetFragment(Event_Create_Fragment.this, DIALOG_REQUEST_CODE);
                newFragment.show(fm, "datePicker");
        }
    }

    @Override
    public void fetchDate(int year, int month, int day) {
        Log.i(TAG,"YEAR MONTH DAY = "+year+month+day);
        startDay = String.format(Locale.getDefault(),"%s",day);
        startMonth = new DateFormatSymbols().getMonths()[month];
        String startString = startMonth + ", "+startDay;
        startTxt.setText(startString);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton checkedBtn = group.findViewById(checkedId);
        privacy = checkedBtn.getText().toString();
    }

    private void fetchUsersList(){
        eventViewModel.fetchUsers().addOnCompleteListener(new OnCompleteListener<List<UserModel>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserModel>> task) {
                if(task.isSuccessful()){
                    usersList.addAll(task.getResult());
                    userAdapter.notifyDataSetChanged();
                } else {
                    Log.i(TAG,"Error retrieving suggested inviteUsersList list. " + task.getException());
                    Toast.makeText(getContext(),"Error retrieving suggested invitees.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void addToCheckedList(UserModel clickedUser) {
        Log.i(TAG,"Adding user to list to invite: "+clickedUser.getEmail());
        inviteUsersList.add(clickedUser);
        for(UserModel user : inviteUsersList){
            Log.i(TAG,"Inviting: "+user.getEmail());
        }

    }

    @Override
    public void removeFromCheckedList(UserModel clickedUser) {
        Log.i(TAG,"Removing user from list to invite: "+clickedUser.getEmail());
        inviteUsersList.remove(clickedUser);
        for(UserModel user : inviteUsersList){
            Log.i(TAG,"Inviting: "+user.getEmail());
        }
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

    private void createEvent(){
        final GroupEvent newEvent = new GroupEvent(
                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                nameTxt.getText().toString(),
                descTxt.getText().toString(),
                startMonth,
                startDay,
                privacy,
                tagsList);

        Log.i(TAG,"Created new GroupEvent of name: "+ newEvent.getOriginalName());

        eventViewModel.createEvent(newEvent,inviteUsersList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    Log.i(TAG,"Successfully created event.");
                    eventViewModel.sendInvites(newEvent,inviteUsersList);
                    addEventToRecycler.addToEventRecycler(newEvent);
                    fm.popBackStack();
                } else {
                    Log.w(TAG,"Error creating event. " + task.getException().toString());
                    Toast.makeText(getContext(),"Error creating event.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.group_edit_toolbar,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_profile).setVisible(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case DIALOG_REQUEST_CODE:
                if(resultCode == YesNoDialog.POS_RESULT){
                    fm.popBackStack();
                } else {
                    Log.i(TAG,"Resuming event creation.");
                }
                break;
        }
    }

    @Override
    public void onBackPress() {
        YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
        yesNoDialog.setCancelable(false);
        yesNoDialog.setTargetFragment(this, Core_Activity.YESNO_REQUEST);
        yesNoDialog.show(fm,null);
    }

}
