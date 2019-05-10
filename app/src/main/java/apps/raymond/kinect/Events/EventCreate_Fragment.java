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
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import apps.raymond.kinect.Add_Users_Adapter;
import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.Maps_Activity;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Core_ViewModel;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventCreate_Fragment extends EventControl_Fragment implements View.OnClickListener,
        Add_Users_Adapter.CheckProfileInterface, BackPressListener,
        Spinner.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "EventCreate_Fragment";
    private static final String DATE_PICKER_FRAG = "DatePicker";
    private static final String TIME_PICKER_FRAG = "TimePicker";
    private static final String SEQUENCE_START = "StartDate";
    private static final String SEQUENCE_END = "EndDate";
    private static final int CANCEL_REQUEST_CODE = 21;
    private static final int MAP_REQUEST_CODE = 22;
    public static final int START_DATE_REQUEST = 23;
    public static final int END_DATE_REQUEST = 24;
    private static final int START_TIME_REQUEST = 25;
    private static final int END_TIME_REQUEST = 26;
    private TestInterface testInterface;


    FragmentManager fm;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            testInterface = (TestInterface) context;
        }catch (ClassCastException e){
            Log.i(TAG,"Unable to attach EventCreatedListener interface to activity.");
        }
        fm = requireActivity().getSupportFragmentManager();
    }

    private Core_ViewModel viewModel;
    Event_Model event;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(getActivity()).get(Core_ViewModel.class);
        fetchUsersList();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public static EventCreate_Fragment newInstance(){
        return new EventCreate_Fragment();
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
    TextView startDateTxt, endDateTxt, startTimeTxt, endTimeTxt,tagsContainer;
    Spinner visibilitySpinner;
    ProgressBar progressBar;
    Add_Users_Adapter userAdapter;
    List<User_Model> usersList, inviteUsersList;
    ArrayList<String> tagsList, primesList;
    LinearLayout inviteUsersLayout;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout layout = view.findViewById(R.id.constraint_layout);
        animateLayoutChanges(layout);

        toolbarSearch = getActivity().findViewById(R.id.toolbar_search);
        toolbarSearch.setVisibility(View.GONE);

        nameTxt = view.findViewById(R.id.event_name_txt);
        descTxt = view.findViewById(R.id.event_desc_txt);
        startDateTxt = view.findViewById(R.id.start_date);
        startDateTxt.setOnClickListener(this);
        endDateTxt = view.findViewById(R.id.end_date);
        endDateTxt.setOnClickListener(this);
        startTimeTxt = view.findViewById(R.id.start_time);
        startTimeTxt.setOnClickListener(this);
        endTimeTxt = view.findViewById(R.id.end_time);
        endTimeTxt.setOnClickListener(this);

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

        inviteUsersLayout = view.findViewById(R.id.invite_users_layout);

        locationTxt = view.findViewById(R.id.text_location);
        locationOptionsBtn = view.findViewById(R.id.expand_locations_btn);
        locationOptionsBtn.setOnClickListener(this);
        initializeLocations();

        primesList = new ArrayList<>();
        ToggleButton sportsTag = view.findViewById(R.id.sports_primary);
        ToggleButton foodTag = view.findViewById(R.id.food_primary);
        ToggleButton drinksTag = view.findViewById(R.id.drinks_primary);
        ToggleButton moviesTag = view.findViewById(R.id.movies_primary);
        ToggleButton chillTag = view.findViewById(R.id.chill_primary);

        sportsTag.setOnCheckedChangeListener(this);
        foodTag.setOnCheckedChangeListener(this);
        drinksTag.setOnCheckedChangeListener(this);
        moviesTag.setOnCheckedChangeListener(this);
        chillTag.setOnCheckedChangeListener(this);

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
            case R.id.expand_locations_btn:
                Intent mapIntent = new Intent(getActivity(), Maps_Activity.class);
                startActivityForResult(mapIntent,MAP_REQUEST_CODE);
                break;
            case R.id.start_date:
                datePickerDialog(SEQUENCE_START);
                break;
            case R.id.end_date:
                datePickerDialog(SEQUENCE_END);
                break;
            case R.id.start_time:
                timePickerDialog(SEQUENCE_START);
                break;
            case R.id.end_time:
                timePickerDialog(SEQUENCE_END);
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        switch (i){
            case R.id.sports_primary:
                if(isChecked){
                    primesList.add(Event_Model.SPORTS);
                } else {
                    primesList.remove(Event_Model.SPORTS);
                }
                return;
            case R.id.food_primary:
                if(isChecked){
                    primesList.add(Event_Model.FOOD);
                } else {
                    primesList.remove(Event_Model.FOOD);
                }
                return;
            case R.id.drinks_primary:
                if(isChecked){
                    primesList.add(Event_Model.DRINKS);
                } else {
                    primesList.remove(Event_Model.DRINKS);
                }
                return;
            case R.id.movies_primary:
                if(isChecked){
                    primesList.add(Event_Model.MOVIES);
                } else {
                    primesList.remove(Event_Model.MOVIES);
                }
                return;
            case R.id.chill_primary:
                if(isChecked){
                    primesList.add(Event_Model.CHILL);
                } else {
                    primesList.remove(Event_Model.CHILL);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.privacy_spinner){
            privacy = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void datePickerDialog(String s){

        switch (s){
            case SEQUENCE_START:
                DatePicker_Fragment datePickerFragment = new DatePicker_Fragment();
                datePickerFragment.setTargetFragment(EventCreate_Fragment.this,START_DATE_REQUEST);
                datePickerFragment.show(fm,DATE_PICKER_FRAG);
                break;
            case SEQUENCE_END:
                DatePicker_Fragment datePicker = DatePicker_Fragment.init(startDateLong);
                datePicker.setTargetFragment(EventCreate_Fragment.this, END_DATE_REQUEST);
                datePicker.show(fm, DATE_PICKER_FRAG);
                break;
        }

    }

    private void timePickerDialog(String s){
        DialogFragment timeFragment = new TimePicker_Fragment();
        switch(s){
            case SEQUENCE_START:
                timeFragment.setTargetFragment(EventCreate_Fragment.this,START_TIME_REQUEST);
                break;
            case SEQUENCE_END:
                timeFragment.setTargetFragment(EventCreate_Fragment.this,END_TIME_REQUEST);
                break;
        }
        timeFragment.show(fm,TIME_PICKER_FRAG);
    }

    /**
     * Populate a drop-down list for the auto complete text when user attempts to type in a location
     * to set the event. The mAdapter will hold a list of the user's stored locations from FireStore.
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
            if(tagsContainer.getVisibility() == View.GONE){
                tagsContainer.setVisibility(View.VISIBLE);
            }
            tagsList.add(tagsTxt.getText().toString());
            tagsContainer.setText(tagsList.toString());
            tagsTxt.getText().clear();
        }
    }

    /**
     * Calls upon the Core_FireBaseRepo to return a query of all the user documents in FireStore.
     * This needs to be changed so that we only query an appropriate set of users that are set and
     * eligible for event invitations.
     */
    private void fetchUsersList(){
        viewModel.fetchUsers().addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<User_Model>> task) {
                if(task.isSuccessful()){
                    usersList.addAll(task.getResult());
                    userAdapter.notifyDataSetChanged();
                    //inviteUsersLayout.setVisibility(View.VISIBLE);
                } else {
                    Log.i(TAG,"Error retrieving suggested inviteUsersList list. " + task.getException());
                    Toast.makeText(getContext(),"Error retrieving suggested invitees.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void addToCheckedList(User_Model clickedUser) {
        Log.i(TAG,"Adding user to list to invite: "+clickedUser.getEmail());
        inviteUsersList.add(clickedUser);
    }

    @Override
    public void removeFromCheckedList(User_Model clickedUser) {
        Log.i(TAG,"Removing user from list to invite: "+clickedUser.getEmail());
        inviteUsersList.remove(clickedUser);
        for(User_Model user : inviteUsersList){
            Log.i(TAG,"Inviting: "+user.getEmail());
        }
    }

    /**
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
        String addressLine = null;
        initDates();
        int invitedSize = inviteUsersList.size();
        double addressLat = 0;
        double addressLng = 0;
        if(address !=null){
            addressLine = address.getFeatureName() + address.getThoroughfare() + ", " + address.getLocality() +", "+ address.getAdminArea();
            addressLat = address.getLatitude();
            addressLng = address.getLongitude();
            event = new Event_Model(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    nameTxt.getText().toString(),descTxt.getText().toString(),privacy, tagsList,
                    primesList, invitedSize, addressLine, addressLat, addressLng, startDateTimeLong,
                    endDateTimeLong);
        } else {
            event = new Event_Model(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    nameTxt.getText().toString(),descTxt.getText().toString(),
                    privacy, tagsList, primesList, invitedSize, startDateTimeLong, endDateTimeLong);
        }

        viewModel.createEvent(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Created event " + event.getName(),Toast.LENGTH_SHORT).show();
                    testInterface.newEventCallback(event);
                    viewModel.addUserToEvent(event);
                    viewModel.sendEventInvites(event,inviteUsersList);
                    fm.popBackStack();
                } else {
                    Toast.makeText(getContext(),"Could not create event at this time.",Toast.LENGTH_SHORT).show();
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

    int startDay, startMonth, startYear, endDay, endMonth, endYear;
    Long startDateLong, endDateLong;
    long startDateTimeLong, endDateTimeLong;
    Address address = null;
    String startTime, endTime;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==Activity.RESULT_OK || resultCode==YesNoDialog.POS_RESULT){
            Bundle args = null;
            try{
                args = data.getExtras();
            } catch (NullPointerException npe){
                Log.i(TAG,"No data returned from activity.");
            }

            switch (requestCode){
                case CANCEL_REQUEST_CODE:
                    fm.popBackStack();
                    break;
                case MAP_REQUEST_CODE:
                    address = data.getParcelableExtra(Maps_Activity.ADDRESS);
                    locationTxt.setText(address.getAddressLine(0));
                    break;
                case START_DATE_REQUEST:
                    initializeDates(args, requestCode);
                    break;
                case END_DATE_REQUEST:
                    initializeDates(args, requestCode);
                    break;
                case START_TIME_REQUEST:
                    startTimeTxt.setText(args.getString(TimePicker_Fragment.TIME_12HR));
                    startTime = args.getString(TimePicker_Fragment.TIME_24HR);
                    break;
                case END_TIME_REQUEST:
                    endTimeTxt.setText(args.getString(TimePicker_Fragment.TIME_12HR));
                    endTime = args.getString(TimePicker_Fragment.TIME_24HR);
                    break;
            }
        }
    }

    private void initializeDates(Bundle args, int requestCode){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMM d, yyyy");
        switch(requestCode){
            case START_DATE_REQUEST:
                startDay = args.getInt(DatePicker_Fragment.DAY);
                startMonth = args.getInt(DatePicker_Fragment.MONTH);
                startYear = args.getInt(DatePicker_Fragment.YEAR);
                startDateLong = args.getLong(DatePicker_Fragment.DATELONG);
                startDateTxt.setText(sdf.format(new Date(startDateLong)));
                break;
            case END_DATE_REQUEST:
                endDay = args.getInt(DatePicker_Fragment.DAY);
                endMonth = args.getInt(DatePicker_Fragment.MONTH);
                endYear = args.getInt(DatePicker_Fragment.YEAR);
                endDateLong = args.getLong(DatePicker_Fragment.DATELONG);
                endDateTxt.setText(sdf.format(new Date(endDateLong)));
                break;
        }
    }

    /**
     * Method call to concatenate date and time vars into a single date-time Date.
     */
    private void initDates() {
        String startDateString = startDay + "." + startMonth + "." + startYear + "." + startTime;
        String endDateString = endDay + "." + endMonth + "." + endYear+"." + endTime;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.HH:mm");
        try {
            if(startDateLong !=null || startTime!=null){
                Date startDateTimeDate = sdf.parse(startDateString);
                startDateTimeLong = startDateTimeDate.getTime();
            }
            if(endDateLong !=null || endTime!=null){
                Date endStartDateTimeDate = sdf.parse(endDateString);
                endDateTimeLong = endStartDateTimeDate.getTime();
            }
        } catch (Exception e){
            //Throw an alert dialog of the start date is empty or returns null.
            Log.w(TAG,"Error.",e);
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