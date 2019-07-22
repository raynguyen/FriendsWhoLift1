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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;
import apps.raymond.kinect.ViewModels.EventCreate_ViewModel;

//ToDo: We do not bind user input to a ViewModel or something that observes life cycles. Consider
// implementing this in the future.
public class Create_Fragment extends Fragment implements
    AddUsers_Adapter.CheckProfileInterface, CompoundButton.OnCheckedChangeListener {
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

        NestedScrollView scrollContainer = view.findViewById(R.id.scroll_create_container);

        TextView header1 = view.findViewById(R.id.text_header1);
        ViewGroup header2  = view.findViewById(R.id.layout_header2);
        ViewGroup header3 = view.findViewById(R.id.layout_header3);

        ViewGroup layoutDetails = view.findViewById(R.id.layout_create_details);
        ViewGroup layoutInvitations = view.findViewById(R.id.layout_create_invitations);
        ViewGroup layoutLocations = view.findViewById(R.id.layout_create_locations);

        header1.setOnClickListener((View v)->{
            scrollContainer.post(() -> scrollContainer.smoothScrollTo(0, header1.getTop()));
            layoutDetails.setVisibility(View.VISIBLE);
            layoutInvitations.setVisibility(View.GONE);
            layoutLocations.setVisibility(View.GONE);
        });
        header2.setOnClickListener((View v)->{
            scrollContainer.post(() -> scrollContainer.smoothScrollTo(0, header2.getTop()));
            layoutDetails.setVisibility(View.GONE);
            layoutInvitations.setVisibility(View.VISIBLE);
            layoutLocations.setVisibility(View.GONE);
        });
        header3.setOnClickListener((View v)->{
            scrollContainer.post(() -> scrollContainer.smoothScrollTo(0, header3.getTop()));
            layoutDetails.setVisibility(View.GONE);
            layoutInvitations.setVisibility(View.GONE);
            layoutLocations.setVisibility(View.VISIBLE);
        });
        return view;
    }

    ToggleButton tglExclusive, tglPrivate, tglPublic;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_create_invite);
        AddUsers_Adapter adapter = new AddUsers_Adapter(this);
        recyclerView.setAdapter(adapter);

        mViewModel.getPublicUsers().observe(requireActivity(), (List<User_Model> publicUsers) -> {
            if(publicUsers != null){
                adapter.setData(publicUsers);
            }
        });

        TextInputEditText editName = view.findViewById(R.id.text_create_name);
        TextInputEditText editDescription = view.findViewById(R.id.text_create_description);
        ToggleButton tglSports = view.findViewById(R.id.toggle_sports);
        ToggleButton tglDrinks = view.findViewById(R.id.toggle_drinks);
        ToggleButton tglFood = view.findViewById(R.id.toggle_food);
        ToggleButton tglMovie = view.findViewById(R.id.toggle_movie);
        ToggleButton tglChill = view.findViewById(R.id.toggle_chill);

        tglExclusive = view.findViewById(R.id.toggle_exclusive);
        tglPrivate = view.findViewById(R.id.toggle_private);
        tglPublic = view.findViewById(R.id.toggle_public);
        tglExclusive.setOnCheckedChangeListener(this);
        tglPrivate.setOnCheckedChangeListener(this);
        tglPublic.setOnCheckedChangeListener(this);
    }

    @Override
    public void addToCheckedList(User_Model clickedUser) {
        Log.w(TAG,"ADD THE USER TO THE LIST TO INVITE..");
    }

    @Override
    public void removeFromCheckedList(User_Model clickedUser) {
        Log.w(TAG,"REMOVE USER FROM INVITE LIST..");
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