package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ObjectModels.User_Model;
import apps.raymond.kinect.Core_ViewModel;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        ViewPager viewPager = view.findViewById(R.id.pager_create_locations);
        CreateFragmentAdapter pagerAdapter = new CreateFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tabs_create_locations);
        tabLayout.setupWithViewPager(viewPager);

        TextView header1 = view.findViewById(R.id.text_header1);
        ViewGroup header2  = view.findViewById(R.id.layout_header2);
        ViewGroup header3 = view.findViewById(R.id.layout_header3);

        ViewGroup layoutDetails = view.findViewById(R.id.layout_create_details);
        ViewGroup layoutInvitations = view.findViewById(R.id.layout_create_invitations);
        ViewGroup layoutLocations = view.findViewById(R.id.layout_create_locations);

        header1.setOnClickListener((View v)->{
            if(layoutDetails.getVisibility() == View.VISIBLE){
                layoutDetails.setVisibility(View.GONE);
                header1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                layoutDetails.setVisibility(View.VISIBLE);
                layoutInvitations.setVisibility(View.GONE);
                layoutLocations.setVisibility(View.GONE);
                header1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up_black_24dp, 0);
            }
        });
        header2.setOnClickListener((View v)->{
            layoutDetails.setVisibility(View.GONE);
            layoutInvitations.setVisibility(View.VISIBLE);
            layoutLocations.setVisibility(View.GONE);
        });
        header3.setOnClickListener((View v)->{
            layoutDetails.setVisibility(View.GONE);
            layoutInvitations.setVisibility(View.GONE);
            layoutLocations.setVisibility(View.VISIBLE);
        });

        Button btnCreate = view.findViewById(R.id.button_create_event);
        btnCreate.setOnClickListener((View v)->{
            mCreateViewModel.createEventModel();
        });

        mCreateViewModel.getValid().observe(this, (Boolean b)->{
            if(b){
                btnCreate.setVisibility(View.VISIBLE);
            } else {
                btnCreate.setVisibility(View.GONE);
            }
        });

        CalendarView calender = view.findViewById(R.id.calendar_create_event);
        calender.setOnDateChangeListener((@NonNull CalendarView calendar, int year, int month,
                                          int dayOfMonth)->
                mCreateViewModel.setEventStart(calendar.getDate()));
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


    }

    @Override
    public void addToCheckedList(User_Model clickedUser) {
        Log.w(TAG,"ADD THE USER TO THE LIST TO INVITE..");
        mCreateViewModel.addInvitedUser(clickedUser);
    }

    @Override
    public void removeFromCheckedList(User_Model clickedUser) {
        Log.w(TAG,"REMOVE USER FROM INVITE LIST..");
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
        Log.w(TAG,"Clicked on marker!");
        LatLng latLng = marker.getPosition();
        mCreateViewModel.setEventLat(latLng.latitude);
        mCreateViewModel.setEventLng(latLng.longitude);
        return true;
    }

    private class CreateFragmentAdapter extends FragmentPagerAdapter{

        CreateFragmentAdapter(FragmentManager fm){
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