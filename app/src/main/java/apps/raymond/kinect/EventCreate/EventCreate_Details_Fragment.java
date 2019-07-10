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

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.AddUsers_Adapter;
import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventCreate_Details_Fragment extends Fragment implements
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

    private SimpleDateFormat _12HrSDF = new SimpleDateFormat("hh:mm a");
    private SimpleDateFormat _24HrSDF = new SimpleDateFormat("HH:mm");

    private String mUserID;
    private List<String> mTagsList = new ArrayList<>();
    private EventCreate_ViewModel mViewModel;
    private Calendar mGregCalendar = Calendar.getInstance();
    private long mTempLong;
    private final java.text.DateFormat mDateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(EventCreate_ViewModel.class);
        User_Model mUserModel = getArguments().getParcelable("user");
        mUserID = mUserModel.getEmail();
        mTempLong = mGregCalendar.getTimeInMillis();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_create_details,container,false);
    }

    EditText txtEventTag;
    TextView txtEventTagsContainer;
    RecyclerView recyclerUsers;
    private ViewFlipper viewFlipper;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtYear = view.findViewById(R.id.text_year);
        TextView txtDateStart = view.findViewById(R.id.text_date_start);
        TextView txtTimeStart = view.findViewById(R.id.text_time_start);

        viewFlipper = view.findViewById(R.id.viewflipper_start);
        ImageButton btnCloseFlipper = view.findViewById(R.id.button_close_flipper);
        btnCloseFlipper.setOnClickListener((View v)->{
            if(viewFlipper.getVisibility() == View.VISIBLE){
                viewFlipper.setVisibility(View.GONE);
                btnCloseFlipper.setVisibility(View.GONE);
            }
        });

        txtYear.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        txtDateStart.setOnClickListener((View v)-> {
            if(viewFlipper.getVisibility() == View.GONE){
                viewFlipper.setVisibility(View.VISIBLE);
                btnCloseFlipper.setVisibility(View.VISIBLE);
            }
            viewFlipper.setDisplayedChild(0);
        });
        txtDateStart.setText(mDateFormat.format(mGregCalendar.getTime()));

        CalendarView calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener((CalendarView cView, int year, int month, int monthDay)-> {
            txtDateStart.setText(mDateFormat.format(mGregCalendar.getTime()));
            mGregCalendar.set(year,month,monthDay);
            mViewModel.setEventStart(mGregCalendar.getTimeInMillis());
        });

        txtTimeStart.setOnClickListener((View v)->{
            if(viewFlipper.getVisibility() == View.GONE){
                viewFlipper.setVisibility(View.VISIBLE);
                btnCloseFlipper.setVisibility(View.VISIBLE);
            }
            viewFlipper.setDisplayedChild(1);
        });

        TimePicker mTimePicker = view.findViewById(R.id.time_picker);
        mTimePicker.setHour(8);
        mTimePicker.setMinute(0);
        mTimePicker.setOnTimeChangedListener((TimePicker picker, int hourOfDay, int minute)->{
            try{
                Date date = _24HrSDF.parse(hourOfDay+":"+minute);
                txtTimeStart.setText(_12HrSDF.format(date));
                mGregCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                mGregCalendar.set(Calendar.MINUTE,minute);
                mViewModel.setEventStart(mGregCalendar.getTimeInMillis());
            } catch (Exception e){}
        });

        mViewModel.getEventStart().observe(this, (Long aLong)-> {
            Log.w(TAG,"CHANGE IN DATE!");
            if(aLong!=null && aLong!=mTempLong){
                txtDateStart.setTextColor(ContextCompat.getColor(requireContext(),R.color.white));
                txtTimeStart.setTextColor(ContextCompat.getColor(requireContext(),R.color.white));
            } else {
                txtDateStart.setTextColor(ContextCompat.getColor(requireContext(),R.color.gray));
                txtTimeStart.setTextColor(ContextCompat.getColor(requireContext(),R.color.gray));
            }
        });

        EditText txtEventName = view.findViewById(R.id.text_event_create_name);
        EditText txtEventDesc = view.findViewById(R.id.text_event_create_desc);
        txtEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.setEventName(s.toString());
            }
        });

        txtEventDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.setEventDesc(s.toString());
            }
        });

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

        Spinner sprPrivacy = view.findViewById(R.id.spinner_event_privacy);
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

        sprPrivacy.setAdapter(mVisibilityAdapter);
        sprPrivacy.setSelection(3);
        sprPrivacy.setOnItemSelectedListener(this);

        ImageButton addTagsBtn = view.findViewById(R.id.event_tag_add_btn);
        addTagsBtn.setOnClickListener((View v)-> checkTagSyntax());

        txtEventTag = view.findViewById(R.id.text_event_tags);
        txtEventTagsContainer = view.findViewById(R.id.text_tags_container);

        recyclerUsers = view.findViewById(R.id.recycler_invite_users);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        final AddUsers_Adapter mUserAdapter = new AddUsers_Adapter(this);
        recyclerUsers.setAdapter(mUserAdapter);

        mViewModel.getUsersToInvite(mUserID)
                .addOnCompleteListener((Task<List<User_Model>> task) -> {
                    if(task.isSuccessful()){
                        mUserAdapter.setData(task.getResult());
                    }
                });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        switch (i){
            case R.id.toggle_sports:
                updatePrimesList(buttonView.isChecked(),Event_Model.SPORTS);
                break;
            case R.id.toggle_food:
                updatePrimesList(buttonView.isChecked(),Event_Model.FOOD);
                break;
            case R.id.toggle_drinks:
                updatePrimesList(buttonView.isChecked(),Event_Model.DRINKS);
                break;
            case R.id.toggle_movie:
                updatePrimesList(buttonView.isChecked(),Event_Model.MOVIE);
                break;
            case R.id.toggle_chill:
                updatePrimesList(buttonView.isChecked(),Event_Model.CHILL);
                break;
            case R.id.toggle_concert:
                updatePrimesList(buttonView.isChecked(), Event_Model.CONCERT);
                break;
        }
    }

    private void updatePrimesList(boolean checked,String s){
        if(checked){
            mViewModel.addEventPrime(s);
        } else {
            mViewModel.removeEventPrime(s);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.spinner_event_privacy){
            /*switch(position){
                case Event_Model.EXCLUSIVE:

                    Log.w(TAG,"PRIVACY IS EXCLUSIVE");
                    break;
                case Event_Model.PRIVATE:
                    Log.w(TAG,"PRIVACY IS private");
                    break;
                case Event_Model.PUBLIC:
                    Log.w(TAG,"PRIVACY IS PUBLIC");
                    break;
            }*/
            mViewModel.getEventModel().setPrivacy(position);
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
            txtEventTagsContainer.setText(mTagsList.toString());
            txtEventTag.getText().clear();
        }
    }

    @Override
    public void addToCheckedList(User_Model clickedUser) {
        mViewModel.addToInviteList(clickedUser);
    }

    @Override
    public void removeFromCheckedList(User_Model clickedUser) {
        mViewModel.removeFromInviteList(clickedUser);
    }
}