package apps.raymond.kinect.UserProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import apps.raymond.kinect.R;
import apps.raymond.kinect.StartUp.Login_Activity;
import apps.raymond.kinect.ViewModels.ProfileActivity_ViewModel;

public class PersonalProfile_Fragment extends Fragment {
    private ProfileActivity_ViewModel mActivityViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityViewModel = ViewModelProviders.of(requireActivity()).get(ProfileActivity_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_personal, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //User_Model userModel = (User_Model) getArguments().get(USER_MODEL);
        User_Model userModel = mActivityViewModel.getUserModel().getValue();
        //Views that display information pertaining to the profile.
        TextView txtName = view.findViewById(R.id.text_profile_name);

        if(userModel.getName()!=null){
            String name = userModel.getName() + " " + userModel.getName2();
            txtName.setText(name);
        } else {
            txtName.setText(userModel.getEmail());
        }

        ImageButton btnReturn = view.findViewById(R.id.button_return);
        btnReturn.setOnClickListener((View v) -> requireActivity().onBackPressed());

        ImageButton btnLogout = view.findViewById(R.id.button_logout);
        btnLogout.setOnClickListener((View v)->{
            mActivityViewModel.signOut();
            Intent loginIntent = new Intent(getContext(), Login_Activity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            requireActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        });

        TabLayout tabs = view.findViewById(R.id.tab_layout_profile);
        ViewPager viewPager = view.findViewById(R.id.viewpager_profile_details);
        ProfilePersonalAdapter adapter = new ProfilePersonalAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        setTabIcons(tabs);

    }

    private void setTabIcons(TabLayout tabLayout){
        int[] test = new int[]{R.drawable.ic_settings_black_24dp, R.drawable.ic_group_black_24dp,
                R.drawable.ic_map_black_24dp, R.drawable.ic_favorite_black_24dp};

        for(int i =0; i < tabLayout.getTabCount(); i++ ){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setIcon(test[i]);
        }
    }

    private class ProfilePersonalAdapter extends FragmentStatePagerAdapter{

        private ProfilePersonalAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    ProfileSettings_Fragment settingsFragment = new ProfileSettings_Fragment();
                    return settingsFragment;
                case 1:
                    ProfileConnections_Fragment connectionsFragment = new ProfileConnections_Fragment();
                    return connectionsFragment;
                case 2:
                    ProfileLocations_Fragment mapFragment = new ProfileLocations_Fragment();
                    return mapFragment;
                case 3:
                    ProfileConnections_Fragment test2 = new ProfileConnections_Fragment();
                    return test2;
                    default:
                        return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }


    }

}
