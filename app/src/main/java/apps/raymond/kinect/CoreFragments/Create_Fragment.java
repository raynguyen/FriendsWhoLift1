package apps.raymond.kinect.CoreFragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

import apps.raymond.kinect.Core_ViewModel;
import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;
import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.ObjectModels.User_Model;
import apps.raymond.kinect.R;

//ToDo: We do not bind user input to a ViewModel or something that observes life cycles. Consider
// implementing this in the future.
public class Create_Fragment extends Fragment implements
    AddUsers_Adapter.CheckProfileInterface, CompoundButton.OnCheckedChangeListener,
        GoogleMap.OnMarkerClickListener {
    private static final String TAG = "CreateFragment: ";
    private Core_ViewModel mViewModel;
    private EventCreate_ViewModel mCreateViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        mCreateViewModel = ViewModelProviders.of(this).get(EventCreate_ViewModel.class);
    }

    //UI animations are set in the onCreateView callback whereas input control is set in onViewCreated.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.pager_create_locations);
        CreatePagerAdapter pagerAdapter = new CreatePagerAdapter(getChildFragmentManager());
        //viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tabs_create_locations);
        TabLayoutMediator tabMediator = new TabLayoutMediator(tabLayout, viewPager, ->{

        });
        //tabLayout.setupWithViewPager(viewPager);

        Button btnCreate = view.findViewById(R.id.button_create_event);
        btnCreate.setOnClickListener((View v)-> mCreateViewModel.createEventModel());

        mCreateViewModel.getValid().observe(this, (Boolean b)->{
            if(b){
                btnCreate.setVisibility(View.VISIBLE);
            } else {
                btnCreate.setVisibility(View.GONE);
            }
        });

        CalendarView calender = view.findViewById(R.id.calendar_create_event);
        calender.setMinDate(Calendar.getInstance().getTimeInMillis());
        calender.setOnDateChangeListener((@NonNull CalendarView calendar, int year, int month,
                                          int dayOfMonth)->
                mCreateViewModel.setEventStart(calendar.getDate()));

        TextView textInviteCount = view.findViewById(R.id.text_create_invited_count);
        mCreateViewModel.getInvitedCount().observe(this, (Integer count) ->
                textInviteCount.setText(String.valueOf(count)));
        return view;
    }

    private ToggleButton tglExclusive, tglPrivate, tglPublic; //Buttons pertaining to event privacy.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText editName = view.findViewById(R.id.text_create_name);
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                mCreateViewModel.setEventName(s.toString());
            }
        });

        TextInputEditText editDescription = view.findViewById(R.id.text_create_description);
        editDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                mCreateViewModel.setEventDesc(s.toString());
            }
        });

        ToggleButton tglSports = view.findViewById(R.id.toggle_sports);
        ToggleButton tglDrinks = view.findViewById(R.id.toggle_drinks);
        ToggleButton tglFood = view.findViewById(R.id.toggle_food);
        ToggleButton tglMovie = view.findViewById(R.id.toggle_movie);
        ToggleButton tglChill = view.findViewById(R.id.toggle_chill);
        ToggleButton tglConcert = view.findViewById(R.id.toggle_concert);
        tglSports.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if(isChecked){
                mCreateViewModel.addEventPrime(Event_Model.SPORTS);
            } else {
                mCreateViewModel.removeEventPrime(Event_Model.SPORTS);
            }
        });
        tglDrinks.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if(isChecked){
                mCreateViewModel.addEventPrime(Event_Model.DRINKS);
            } else {
                mCreateViewModel.removeEventPrime(Event_Model.DRINKS);
            }
        });
        tglFood.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if(isChecked){
                mCreateViewModel.addEventPrime(Event_Model.FOOD);
            } else {
                mCreateViewModel.removeEventPrime(Event_Model.FOOD);
            }
        });
        tglMovie.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if(isChecked){
                mCreateViewModel.addEventPrime(Event_Model.MOVIE);
            } else {
                mCreateViewModel.removeEventPrime(Event_Model.MOVIE);
            }
        });
        tglChill.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if(isChecked){
                mCreateViewModel.addEventPrime(Event_Model.CHILL);
            } else {
                mCreateViewModel.removeEventPrime(Event_Model.CHILL);
            }
        });
        tglConcert.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if(isChecked){
                mCreateViewModel.addEventPrime(Event_Model.CONCERT);
            } else {
                mCreateViewModel.removeEventPrime(Event_Model.CONCERT);
            }
        });

        tglExclusive = view.findViewById(R.id.toggle_exclusive);
        tglPrivate = view.findViewById(R.id.toggle_private);
        tglPublic = view.findViewById(R.id.toggle_public);
        tglExclusive.setOnCheckedChangeListener(this);
        tglPrivate.setOnCheckedChangeListener(this);
        tglPublic.setOnCheckedChangeListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_create_invite);
        AddUsers_Adapter adapter = new AddUsers_Adapter(this);
        recyclerView.setAdapter(adapter);
        mViewModel.getPublicUsers().observe(requireActivity(), (List<User_Model> publicUsers) -> {
            if(publicUsers != null){
                adapter.setData(publicUsers);
            }
        });

        TextView header1 = view.findViewById(R.id.text_header1);
        ViewGroup header2  = view.findViewById(R.id.layout_header2);
        ViewGroup header3 = view.findViewById(R.id.layout_header3);

        ViewGroup layoutDetails = view.findViewById(R.id.layout_create_details);
        ViewGroup layoutInvitations = view.findViewById(R.id.layout_create_invitations);
        ViewGroup layoutLocations = view.findViewById(R.id.layout_create_locations);

        LinearLayoutCompat.LayoutParams show = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
        LinearLayoutCompat.LayoutParams hide = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f);
        /*
        ToDo: There is a Fatal error when hiding the locations view that crashes the app.
         Possible to do with hiding fragments?
         */
        header1.setOnClickListener((View v)->{
            layoutDetails.setLayoutParams(show);
            layoutInvitations.setLayoutParams(hide);
            //layoutLocations.setLayoutParams(hide);
        });
        header2.setOnClickListener((View v)->{
            layoutDetails.setLayoutParams(hide);
            layoutInvitations.setLayoutParams(show);
            //layoutLocations.setLayoutParams(hide);
        });
        header3.setOnClickListener((View v)->{
            layoutDetails.setLayoutParams(hide);
            layoutInvitations.setLayoutParams(hide);
            layoutLocations.setLayoutParams(show);
        });

        mCreateViewModel.checkCreated().observe(this, (Boolean b)->{
            if(b) {
                Toast.makeText(getContext(), "Created new event", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void addToCheckedList(User_Model clickedUser) {
        mCreateViewModel.addInvitedUser(clickedUser);
    }

    @Override
    public void removeFromCheckedList(User_Model clickedUser) {
        mCreateViewModel.removeInvitedUser(clickedUser);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Set the privacy for the event held by the view model.
        if(isChecked){
            switch (buttonView.getId()){
                case R.id.toggle_exclusive:
                    tglPrivate.setChecked(false);
                    tglPublic.setChecked(false);
                    mCreateViewModel.setEventPrivacy(Event_Model.EXCLUSIVE);
                    return;
                case R.id.toggle_private:
                    tglExclusive.setChecked(false);
                    tglPublic.setChecked(false);
                    mCreateViewModel.setEventPrivacy(Event_Model.PRIVATE);
                    return;
                case R.id.toggle_public:
                    tglExclusive.setChecked(false);
                    tglPrivate.setChecked(false);
                    mCreateViewModel.setEventPrivacy(Event_Model.PUBLIC);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng latLng = marker.getPosition();
        mCreateViewModel.setEventLat(latLng.latitude);
        mCreateViewModel.setEventLng(latLng.longitude);
        return true;
    }

    private class CreatePagerAdapter extends FragmentPagerAdapter {

        CreatePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return new BaseMap_Fragment();
                case 1:
                    return new CreateLocations_Fragment();
            }
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Map";
                case 1:
                    return "Saved Locations";
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}