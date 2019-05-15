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

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.EventInvitations_Fragment;
import apps.raymond.kinect.DialogFragments.GroupInvite_Fragment;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Groups.Group_Model;

//ToDo: Add a tab for invited/declined invitations. Allow for swipe left to decline or swipe right to accept!
public class ViewInvitations_Fragment extends Fragment{
    private static final String GROUP_INVITATIONS = "Groups";
    private static final String EVENT_INVITATIONS = "Events";

    public static ViewInvitations_Fragment newInstance(List<Event_Model> list1,
                                                       List<Group_Model> list2){
        ArrayList<Event_Model> set1 = new ArrayList<>(list1);
        ArrayList<Group_Model> set2 = new ArrayList<>(list2);
        ViewInvitations_Fragment fragment = new ViewInvitations_Fragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EVENT_INVITATIONS, set1);
        args.putParcelableArrayList(GROUP_INVITATIONS,set2);
        fragment.setArguments(args);
        return fragment;
    }

    ArrayList<Event_Model> mEventInvitations;
    ArrayList<Group_Model> mGroupInvitations;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            mEventInvitations = getArguments().getParcelableArrayList(EVENT_INVITATIONS);
            mGroupInvitations = getArguments().getParcelableArrayList(GROUP_INVITATIONS);
        }
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

    public class InvitePagerAdapter extends FragmentPagerAdapter {
        private InvitePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return EventInvitations_Fragment.newInstance(mEventInvitations);
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
