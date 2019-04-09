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
import android.support.v7.widget.SearchView;
import android.text.style.ClickableSpan;
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
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;
import apps.raymond.kinect.UserProfile.UserModel;

public class Event_Create_Fragment extends Fragment implements View.OnClickListener,
        DatePickerDialog.FetchDate, Add_Users_Adapter.CheckProfileInterface, BackPressListener,
        Spinner.OnItemSelectedListener{

    public static final String TAG = "Event_Create_Fragment";
    private static final String DATE_PICKER_FRAG = "DatePicker";
    private static final int DIALOG_REQUEST_CODE = 21;


    private AddEvent addEventToRecycler;
    public interface AddEvent{
        void addToEventRecycler(Event_Model groupEvent);
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


    AutoCompleteTextView locationTxt;
    ImageButton locationOptionsBtn;
    SearchView toolbarSearch;
    EditText nameTxt, descTxt;
    TextView startMonthTxt, startDayTxt;
    TextView tagsContainer;
    EditText tagsTxt;
    String privacy;
    Spinner visibilitySpinner;
    ProgressBar progressBar;
    Add_Users_Adapter userAdapter;
    List<UserModel> usersList, inviteUsersList;
    ArrayList<String> tagsList;
    LinearLayout eventExtrasLayout;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbarSearch = getActivity().findViewById(R.id.toolbar_search);
        toolbarSearch.setVisibility(View.GONE);

        nameTxt = view.findViewById(R.id.event_name_txt);
        nameTxt.setHint(R.string.event_name);
        descTxt = view.findViewById(R.id.event_desc_txt);

        startMonthTxt = view.findViewById(R.id.start_month);
        startDayTxt = view.findViewById(R.id.start_day);

        Button startBtn = view.findViewById(R.id.start_btn);
        startBtn.setOnClickListener(this);
        Button endBtn = view.findViewById(R.id.end_btn);
        endBtn.setOnClickListener(this);

        visibilitySpinner = view.findViewById(R.id.visibility_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(requireContext(),R.array.visibility_options,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visibilitySpinner.setAdapter(adapter);
        visibilitySpinner.setOnItemSelectedListener(this);

        eventExtrasLayout = view.findViewById(R.id.event_extras_layout);

        locationTxt = view.findViewById(R.id.location_txt);
        locationOptionsBtn = view.findViewById(R.id.expand_locations_btn);
        initializeLocations();


        progressBar = view.findViewById(R.id.create_progress_bar);

        ImageButton addTagsBtn = view.findViewById(R.id.event_tag_add_btn);
        addTagsBtn.setOnClickListener(this);

        tagsList = new ArrayList<>();
        tagsTxt = view.findViewById(R.id.event_tags_txt);
        tagsContainer = view.findViewById(R.id.tags_container_txt);
        tagsContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.i(TAG,"bottom = "+bottom + ". Old bottom = "+oldBottom);
                if(tagsContainer.getVisibility()!=View.GONE){
                    if(oldBottom==0){
                        int i = v.getHeight() - v.getPaddingBottom();
                        //Log.i(TAG,"Tagscontainer was prevously invisible. Shifting recycler down by: "+i);
                        eventExtrasLayout.animate()
                                .setDuration(300)
                                .translationY(v.getHeight()-v.getPaddingBottom());
                    } else {
                        int i = bottom-oldBottom;
                        //Log.i(TAG,"Translating recyclerview down by : "+i);
                        eventExtrasLayout.animate()
                                .setDuration(300)
                                .translationY(bottom - oldBottom);
                    }
                }
            }
        });


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
                Log.i(TAG,"Adding tag to event.");
                // When this button is pressed, add the String to a container below the TextView that displays all the tags that have been added
                // Research a way of getting the individual strings as clickable objects.
                if(tagsContainer.getVisibility() == View.GONE){
                    tagsContainer.setAlpha(0.0f);
                    tagsContainer.setVisibility(View.VISIBLE);
                    tagsContainer.animate().alpha(1.0f).setDuration(300);
                    eventExtrasLayout.animate()
                            .setDuration(300)
                            .translationY(tagsContainer.getHeight());

                }

                tagsList.add(tagsTxt.getText().toString());
                tagsContainer.setText(tagsList.toString());
                tagsTxt.getText().clear();
                break;
            case R.id.expand_users_list:
                break;
            case R.id.date_btn:
                datePickerDialog();
                break;
            case R.id.start_btn:
                datePickerDialog();
                break;
            case R.id.end_btn:
                datePickerDialog();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //ToDo: Set the visibility option for the event here.
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void datePickerDialog(){
        DialogFragment newFragment = new DatePickerDialog();
        newFragment.setTargetFragment(Event_Create_Fragment.this, DIALOG_REQUEST_CODE);
        newFragment.show(fm, DATE_PICKER_FRAG);
    }

    String startMonth, startDay;
    @Override
    public void fetchDate(int year, int month, int day) {
        startDay = String.format(Locale.getDefault(),"%s",day);
        startMonth = new DateFormatSymbols().getMonths()[month];
        startMonthTxt.setText(startMonth);
        startDayTxt.setText(startDay);
    }


    private void initializeLocations(){
        ArrayAdapter<String> locationsAdapter = new ArrayAdapter<>(requireContext(),android.R.layout.simple_dropdown_item_1line);
        locationTxt.setAdapter(locationsAdapter);
    }






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
                nameTxt.getText().toString(),
                descTxt.getText().toString(),
                startMonth,
                startDay,
                privacy,
                tagsList);

        viewModel.createEvent(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    Log.i(TAG,"Successfully created event.");
                    Toast.makeText(getContext(),"Created event " + newEvent.getName(),Toast.LENGTH_SHORT).show();
                    viewModel.sendEventInvites(newEvent,inviteUsersList);
                    addEventToRecycler.addToEventRecycler(newEvent);
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
}
