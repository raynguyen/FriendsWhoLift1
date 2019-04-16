/*
 * On back click, should prompt the user if they want to completely discard any progress.
 *
 * Need to decide on how to select the group to attach event to.
 *
 * One ConstraintLayout that holds the datepicker info and the timepicker info. At the start of the
 * layout, have a calender imagebtn that opens a dialog with a viewpager for the time picker and the date picker.
 * At the end of the image button will be two view layouts with the date on top and the time on the bottom.
 */

package apps.raymond.kinect.Events;

import android.animation.LayoutTransition;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.Add_Users_Adapter;
import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.Maps_Activity;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;
import apps.raymond.kinect.UserProfile.UserModel;

public class Event_Create_Fragment extends Fragment implements View.OnClickListener,
        DatePickerDialog.FetchDate, Add_Users_Adapter.CheckProfileInterface, BackPressListener,
        Spinner.OnItemSelectedListener{

    public static final String TAG = "Event_Create_Fragment";
    private static final String DATE_PICKER_FRAG = "DatePicker";
    private static final int DIALOG_REQUEST_CODE = 21;


    private EventCreatedListener eventCreatedListener;
    public interface EventCreatedListener {
        void notifyEventCreated(Event_Model groupEvent);
    }

    FragmentManager fm;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            eventCreatedListener = (EventCreatedListener) context;
        }catch (ClassCastException e){
            Log.i(TAG,"Unable to attach EventCreatedListener interface to activity.");
        }
        fm = requireActivity().getSupportFragmentManager();
    }

    private Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(getActivity()).get(Repository_ViewModel.class);
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

    int privacy;
    AutoCompleteTextView locationTxt; //ToDo: Bind the user's locations to this autocomplete.
    ImageButton locationOptionsBtn;
    SearchView toolbarSearch;
    EditText nameTxt, descTxt, tagsTxt;
    TextView month1_txt, day1_txt, month2_txt, day2_txt,tagsContainer;
    Spinner visibilitySpinner;
    ProgressBar progressBar;
    Add_Users_Adapter userAdapter;
    List<UserModel> usersList, inviteUsersList;
    ArrayList<String> tagsList;
    LinearLayout eventExtrasLayout;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout layout = view.findViewById(R.id.constraint_layout);
        animateLayoutChanges(layout);

        toolbarSearch = getActivity().findViewById(R.id.toolbar_search);
        toolbarSearch.setVisibility(View.GONE);

        nameTxt = view.findViewById(R.id.event_name_txt);
        descTxt = view.findViewById(R.id.event_desc_txt);

        month1_txt = view.findViewById(R.id.month1_txt);
        day1_txt = view.findViewById(R.id.day1_txt);
        month2_txt = view.findViewById(R.id.month2_txt);
        day2_txt = view.findViewById(R.id.day2_txt);

        Button startBtn = view.findViewById(R.id.start_btn);
        startBtn.setOnClickListener(this);
        Button endBtn = view.findViewById(R.id.end_btn);
        endBtn.setOnClickListener(this);

        visibilitySpinner = view.findViewById(R.id.privacy_spinner);
        ArrayAdapter<String> vAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item_layout, getResources().getStringArray(R.array.visibility_options) ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(R.id.text1)).setText(null);
                    ((TextView)v.findViewById(R.id.text1)).setHint(getItem(getCount()));
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount()-1; //don't display last item. It is used as hint.
            }
        };
        visibilitySpinner.setAdapter(vAdapter);
        visibilitySpinner.setSelection(3);
        visibilitySpinner.setOnItemSelectedListener(this);

        eventExtrasLayout = view.findViewById(R.id.event_extras_layout);

        locationTxt = view.findViewById(R.id.location_txt);
        locationOptionsBtn = view.findViewById(R.id.expand_locations_btn);
        locationOptionsBtn.setOnClickListener(this);
        initializeLocations();

        progressBar = view.findViewById(R.id.create_progress_bar);

        ImageButton addTagsBtn = view.findViewById(R.id.event_tag_add_btn);
        addTagsBtn.setOnClickListener(this);

        tagsList = new ArrayList<>();
        tagsTxt = view.findViewById(R.id.event_tags_txt);
        tagsContainer = view.findViewById(R.id.tags_container_txt);

        usersList = new ArrayList<>();

        inviteUsersList = new ArrayList<>();
        RecyclerView usersRecycler = view.findViewById(R.id.add_users_recycler);
        usersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new Add_Users_Adapter(usersList, this);
        usersRecycler.setAdapter(userAdapter);

        ImageButton expandRecycler = view.findViewById(R.id.expand_users_list);
        expandRecycler.setOnClickListener(this);

        Button saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);
    }

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
                onBackPress();
                break;
            case R.id.event_tag_add_btn:
                checkTagSyntax();
                break;
            case R.id.expand_users_list:
                break;
            case R.id.start_btn:
                datePickerDialog(DatePickerDialog.SEQUENCE_START);
                break;
            case R.id.end_btn:
                datePickerDialog(DatePickerDialog.SEQUENCE_END);
                break;
            case R.id.expand_locations_btn:
                Intent mapIntent = new Intent(getActivity(), Maps_Activity.class);
                startActivity(mapIntent);
                break;

        }
    }

    /*
    * When an option for a spinner is selected, we want to an appropriate value corresponding to
    * the selection.
    */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.privacy_spinner){
            privacy =  position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void datePickerDialog(String s){
        DialogFragment datePicker = DatePickerDialog.newInstance(s);
        datePicker.setTargetFragment(Event_Create_Fragment.this, DIALOG_REQUEST_CODE);
        datePicker.show(fm, DATE_PICKER_FRAG);
    }

    String month1, day1, month2, day2;
    @Override
    public void returnStartDate(int year, int month, int day) {
        day1 = String.format(Locale.getDefault(),"%s",day);
        month1 = new DateFormatSymbols().getMonths()[month];
        month1_txt.setText(month1);
        day1_txt.setText(day1);
    }

    @Override
    public void returnEndDate(int year, int month, int day) {
        day2 = String.format(Locale.getDefault(),"%s",day);
        month2 = new DateFormatSymbols().getMonths()[month];
        month2_txt.setText(month2);
        day2_txt.setText(day2);
    }

    /**
     * Populate a drop-down list for the auto complete text when user attempts to type in a location
     * to set the event. The adapter will hold a list of the user's stored locations from FireStore.
     */
    private void initializeLocations(){
        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<>(requireContext(),android.R.layout.simple_dropdown_item_1line);
        locationTxt.setAdapter(locationsAdapter);
    }

    /**
     * Checks the tag against Java regex for only alphanumerics. If the match returns false and the
     * length is >0 we add the tag to the list of tags.
     *
     * The tags container is set to View.GONE while the tags list is empty. Once the first tag is
     * added, we set the container view to View.VISIBLE and animate the views below it downwards.
     *
     * ToDo: The tags container should house clickable tag icons that are removed upon click.
     */
    private void checkTagSyntax(){
        String checkTag = tagsTxt.getText().toString();
        if(!checkTag.equals("") && checkTag.matches("^[a-zA-Z0-9]+$")){
            Toast.makeText(getContext(),"THIS TAG CAN BE ADDED!",Toast.LENGTH_LONG).show();

            if(tagsContainer.getVisibility() == View.GONE){
                tagsContainer.setVisibility(View.VISIBLE);
            }

            tagsList.add(tagsTxt.getText().toString());
            tagsContainer.setText(tagsList.toString());
            tagsTxt.getText().clear();

        }
    }

    /**
     * Calls upon the FireBaseRepository to return a query of all the user documents in FireStore.
     * This needs to be changed so that we only query an appropriate set of users that are set and
     * eligible for event invitations.
     */
    private void fetchUsersList(){
        viewModel.fetchUsers().addOnCompleteListener(new OnCompleteListener<List<UserModel>>() {
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
        return check;
    }

    private void createEvent(){
        final Event_Model newEvent = new Event_Model(
                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                nameTxt.getText().toString(),descTxt.getText().toString(), month1, day1, month2,
                day2, privacy, tagsList);

        viewModel.createEvent(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    Log.i(TAG,"Successfully created event.");
                    Toast.makeText(getContext(),"Created event " + newEvent.getName(),Toast.LENGTH_SHORT).show();
                    viewModel.sendEventInvites(newEvent,inviteUsersList);
                    eventCreatedListener.notifyEventCreated(newEvent);
                    fm.popBackStack();
                } else {
                    Log.w(TAG,"Error creating event. " + task.getException());
                    Toast.makeText(getContext(),"Error creating event.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onBackPress() {
        YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
        yesNoDialog.setCancelable(false);
        yesNoDialog.setTargetFragment(this, Core_Activity.YESNO_REQUEST);
        yesNoDialog.show(fm,null);
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
    public void onDestroyView() {
        super.onDestroyView();
        toolbarSearch.setVisibility(View.VISIBLE);
    }

    /**
     * A helper method that enables animation automation through Android.
     *
     * @param container The view of which we are enabling automatic transitions for.
     */
    public static void animateLayoutChanges(ViewGroup container) {
        final LayoutTransition transition = new LayoutTransition();
        transition.setDuration(300);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.enableTransitionType(LayoutTransition.CHANGING);
            transition.enableTransitionType(LayoutTransition.APPEARING);
            transition.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
            transition.enableTransitionType(LayoutTransition.DISAPPEARING);
            transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        }

        container.setLayoutTransition(transition);
    }
}
