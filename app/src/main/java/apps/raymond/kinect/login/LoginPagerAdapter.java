package apps.raymond.kinect.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/*
By default, a ViewPager will create the Fragment for the visible page and the Fragment in the next
position.
*/
public class LoginPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 2;
    public LoginPagerAdapter(FragmentManager fm){
        super(fm);
    }

    //getItem() is called when the adapter needs a fragment and the fragment does not exist.
    @Override
    public Fragment getItem(int position){
        //the position parameter is used to determine which fragment the Adapter needs to create.
        switch(position){
            case 0:
                return new LoginFrag();
            case 1:
                return new SignUpFrag();
            default:
                return null;
        }
    }

    //returns the number of views available.
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
