package apps.raymond.friendswholift.DialogFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.EventInviteFragment;
import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.GroupInviteFragment;
import apps.raymond.friendswholift.Groups.GroupBase;
import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.Repository_ViewModel;

public class InviteDialog extends Fragment{
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
        InvitePagerAdapter pAdapter = new InvitePagerAdapter(getFragmentManager());
        inviteViewPager.setAdapter(pAdapter);
        inviteViewPager.setCurrentItem(0);

        TabLayout tabLayout = view.findViewById(R.id.invite_tabs);
        tabLayout.setupWithViewPager(inviteViewPager);
    }

    public class InvitePagerAdapter extends FragmentPagerAdapter {
        private InvitePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return new EventInviteFragment();
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
