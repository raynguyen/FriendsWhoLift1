package apps.raymond.kinect.DialogFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;

public class Invite_Messages_Fragment extends Fragment{
    private static final String TAG = "Invites_Dialog";

    Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.invite_dialog,container,false);
    }

    ViewPager inviteViewPager;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inviteViewPager = view.findViewById(R.id.invite_viewpager);
        InvitePagerAdapter pAdapter = new InvitePagerAdapter(getChildFragmentManager());
        inviteViewPager.setAdapter(pAdapter);
        inviteViewPager.setCurrentItem(0);

        TabLayout tabLayout = view.findViewById(R.id.invite_tabs);
        tabLayout.setupWithViewPager(inviteViewPager);

        ImageButton closeBtn = view.findViewById(R.id.messages_close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public class InvitePagerAdapter extends FragmentPagerAdapter {
        private InvitePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return new Event_Invites_Fragment();
                case 1:
                    return new GroupInviteFragment();
                default:
                    return null;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Events";
                case 1:
                    return "Groups";
                default :
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
