package apps.raymond.kinect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import apps.raymond.kinect.Events.EventInvitations_Fragment;
import apps.raymond.kinect.DialogFragments.GroupInvite_Fragment;

//ToDo: Add a tab for invited/declined invitations. Allow for swipe left to decline or swipe right to accept!
public class ViewInvitations_Fragment extends Fragment{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    /**
     * Adapter class responsible for building the fragments for our ViewPager.
     */
    public class InvitePagerAdapter extends FragmentPagerAdapter {
        private InvitePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return new EventInvitations_Fragment();
                case 1:
                    return new GroupInvite_Fragment();
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
