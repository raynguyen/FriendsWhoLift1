/*
 * On back click, should prompt the user if they want to completely discard any progress.
 *
 * Need to decide on how to select the group to attach event to.
 *
 * One ConstraintLayout that holds the datepicker info and the timepicker info. At the start of the
 * layout, have a calender imagebtn that opens a dialog with a viewpager for the time picker and the date picker.
 * At the end of the image button will be two view layouts with the date on top and the time on the bottom.
 */

package apps.raymond.kinect.EventCreate;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import apps.raymond.kinect.AddUsers_Adapter;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventCreate_Details_Fragment extends Fragment implements View.OnClickListener,
        AddUsers_Adapter.CheckProfileInterface, Spinner.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {
    public static final String TAG = "EventCreate_Details_Fragment";

    public static EventCreate_Details_Fragment newInstance(User_Model userModel){
        EventCreate_Details_Fragment fragment = new EventCreate_Details_Fragment();
        Bundle args = new Bundle();
        args.putParcelable("user",userModel);
        fragment.setArguments(args);
        return fragment;
    }

    private User_Model mUserModel;
    private String mUserID;
    private List<String> mPrimesList = new ArrayList<>();
    private List<String> mTagsList = new ArrayList<>();
    private List<User_Model> mInviteList = new ArrayList<>();
    private EventCreate_ViewModel mViewModel; //Need to get users to invite
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(EventCreate_ViewModel.class);
        mUserModel = getArguments().getParcelable("user");
        mUserID = mUserModel.getEmail();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_event_create_details,container,false);
    }

    int privacy;
    EditText txtEventName,txtEventDesc, txtEventTag;
    TextView txtEventTagsContainer;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtEventName = view.findViewById(R.id.text_event_create_name);
        txtEventDesc = view.findViewById(R.id.text_event_create_desc);
        ToggleButton tglSports = view.findViewById(R.id.toggle_sports);
        tglSports.setOnCheckedChangeListener(this);
        ToggleButton tglFood = view.findViewById(R.id.toggle_food);
        tglFood.setOnCheckedChangeListener(this);
        ToggleButton tglDrinks = view.findViewById(R.id.toggle_drinks);
        tglDrinks.setOnCheckedChangeListener(this);
        ToggleButton tglMovie = view.findViewById(R.id.toggle_movie);
        tglMovie.setOnCheckedChangeListener(this);
        ToggleButton tglChill = view.findViewById(R.id.toggle_chill);
        tglChill.setOnCheckedChangeListener(this);

        Spinner visibilitySpinner = view.findViewById(R.id.spinner_event_privacy);
        ArrayAdapter<String> mVisibilityAdapter = new ArrayAdapter<String>(requireContext(),
                R.layout.spinner_item_layout,
                getResources().getStringArray(R.array.visibility_options) ) {
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
                return super.getCount()-1;
            }
        };
        visibilitySpinner.setAdapter(mVisibilityAdapter);
        visibilitySpinner.setSelection(3);
        visibilitySpinner.setOnItemSelectedListener(this);

        ImageButton addTagsBtn = view.findViewById(R.id.event_tag_add_btn);
        addTagsBtn.setOnClickListener(this);

        txtEventTag = view.findViewById(R.id.text_event_tags);
        txtEventTagsContainer = view.findViewById(R.id.text_tags_container);

        RecyclerView usersRecycler = view.findViewById(R.id.add_users_recycler);
        usersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        final AddUsers_Adapter mUserAdapter = new AddUsers_Adapter(this);
        usersRecycler.setAdapter(mUserAdapter);

        mViewModel.getInvitableUsers(mUserID)
                .addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<User_Model>> task) {
                        if(task.isSuccessful()){
                            mUserAdapter.setData(task.getResult());
                        }
                    }
                });
}

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.event_tag_add_btn:
                checkTagSyntax();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        switch (i){
            case R.id.toggle_sports:
                break;
            case R.id.toggle_food:
                break;
            case R.id.toggle_drinks:
                break;
            case R.id.toggle_movie:
                break;
            case R.id.toggle_chill:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.spinner_event_privacy){
            privacy = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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
        String checkTag = txtEventTag.getText().toString();
        if(!checkTag.equals("") && checkTag.matches("^[a-zA-Z0-9]+$")){
            if(txtEventTagsContainer.getVisibility() == View.GONE){
                txtEventTagsContainer.setVisibility(View.VISIBLE);
            }
            mTagsList.add(txtEventTag.getText().toString());
            txtEventTagsContainer.setText(txtEventTag.toString());
            txtEventTag.getText().clear();
        }
    }

    @Override
    public void addToCheckedList(User_Model clickedUser) {
        mInviteList.add(clickedUser);
    }

    @Override
    public void removeFromCheckedList(User_Model clickedUser) {
        mInviteList.remove(clickedUser);
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

    /**
     * This method is called before creating an Event instance.
     * Checks that all fields are filled and are valid inputs.
     * True if fields require attention.
     */
    private boolean checkFields(){
        //Highlight the necessary fields that are empty.
        //Display a toast.
        boolean check = false;
        if(txtEventName.getText().toString().isEmpty()){
            Log.i(TAG,"Name EditText is empty.");
            Toast.makeText(getContext(),"Finish filling out the crap wtf.",Toast.LENGTH_SHORT).show();
            check = true;
        }
        return check;
    }

    int startDay, startMonth, startYear, endDay, endMonth, endYear;
    Long startDateLong, endDateLong;
    long startDateTimeLong, endDateTimeLong;
    Address address = null;
    String startTime, endTime;
    private void createEvent(){
        String addressLine = null;
        initDates();
        int invitedSize = mInviteList.size();
        Event_Model event;
        double addressLat = 0;
        double addressLng = 0;
        /*if(address !=null){
            addressLine = address.getFeatureName()+ " " + address.getThoroughfare() + ", " + address.getLocality() +", "+ address.getAdminArea();
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
        }*/


        /*mViewModel.createEvent(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Created event " + event.getName(),
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    mViewModel.addEventToUser(userID,event);
                    mViewModel.addUserToEvent(userID,mUser,event.getOriginalName());
                    mViewModel.sendEventInvites(event, inviteUsersList);
                    fm.popBackStack();


                    List<Event_Model> eventsList = mViewModel.getAcceptedEvents().getValue();
                    eventsList.add(event);
                    mViewModel.setAcceptedEvents(eventsList);
                }
            }
        });*/
    }
}

/*
* private void datePickerDialog(String s){
        switch (s){
            case SEQUENCE_START:
                DatePicker_Fragment datePickerFragment = new DatePicker_Fragment();
                datePickerFragment.setTargetFragment(EventCreate_Details_Fragment.this,START_DATE_REQUEST);
                datePickerFragment.show(getFragmentManager(),DATE_PICKER_FRAG);
                break;
            case SEQUENCE_END:
                DatePicker_Fragment datePicker = DatePicker_Fragment.init(startDateLong);
                datePicker.setTargetFragment(EventCreate_Details_Fragment.this, END_DATE_REQUEST);
                datePicker.show(getFragmentManager(), DATE_PICKER_FRAG);
                break;
        }
    }*/