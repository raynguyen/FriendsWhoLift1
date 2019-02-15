package apps.raymond.friendswholift;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import apps.raymond.friendswholift.Groups.MyGroupsFragment;

public class Core_Activity_Adapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 2;

    public Core_Activity_Adapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new Core_EventsFragment();
            case 1:
                return new MyGroupsFragment();
            default:
                return null; // Decide what the default Fragment should be.
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Events";
            case 1:
                return "Groups";
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
