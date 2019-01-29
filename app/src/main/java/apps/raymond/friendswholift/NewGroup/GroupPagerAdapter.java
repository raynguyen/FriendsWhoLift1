/*
 * Potentially combine both the GroupPager and the LoginPager?
 */
package apps.raymond.friendswholift.NewGroup;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class GroupPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 2;

    public GroupPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new NewGroup_Fragment();
            case 1:
                return new NewGroup_Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "My Groups";
            case 1:
                return "Create New Group";
        }
        return null;
    }
}
