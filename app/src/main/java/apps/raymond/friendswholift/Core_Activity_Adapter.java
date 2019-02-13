package apps.raymond.friendswholift;

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
                return new MyEventsFragment();
            case 1:
                return new MyGroupsFragment();
            default:
                return null; // Decide what the default Fragment should be.
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
