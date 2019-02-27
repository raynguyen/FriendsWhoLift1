package apps.raymond.friendswholift.UserProfile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class EventInvite_Recycler_Adapter extends FragmentPagerAdapter {
    private static final String TAG = "Profile Recycler Adapter";
    private static final int NUM_PAGES = 3;

    public EventInvite_Recycler_Adapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
