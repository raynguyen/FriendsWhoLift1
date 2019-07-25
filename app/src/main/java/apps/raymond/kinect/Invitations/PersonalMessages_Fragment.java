package apps.raymond.kinect.Invitations;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;

import apps.raymond.kinect.R;

public class PersonalMessages_Fragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invitations,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabs = view.findViewById(R.id.tabs_invitations);
        ViewPager viewPager = view.findViewById(R.id.viewpager_invitations);
        tabs.setupWithViewPager(viewPager);

        PersonalMessages_Adapter adapter = new PersonalMessages_Adapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        ImageButton btnClose = view.findViewById(R.id.button_close_invitations);
        btnClose.setOnClickListener((View v)->{ getFragmentManager().popBackStack();
        });
    }

    private class PersonalMessages_Adapter extends FragmentPagerAdapter {

        private PersonalMessages_Adapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return new EventInvitations_Fragment();
                case 1:
                    return new ConnectionRequests_Fragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getResources().getString(R.string.event_invitations);
                case 1:
                    return getResources().getString(R.string.connection_requests);
                default:
                    return null;
            }
        }
    }


}
