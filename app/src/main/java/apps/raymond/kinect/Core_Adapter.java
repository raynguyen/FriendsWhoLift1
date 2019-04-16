package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Core_Events_Fragment;
import apps.raymond.kinect.Groups.Core_Groups_Fragment;

public class Core_Adapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 2;
    public static final int EVENTS_FRAGMENT = 0;
    public static final int GROUPS_FRAGMENT = 1;

    private List<Fragment> fragments;

    Core_Adapter(FragmentManager fm){
        super(fm);
        fragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,position);
        fragments.add(position,fragment);
        return fragment;
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new Core_Events_Fragment();
            case 1:
                return new Core_Groups_Fragment();
            default:
                return null;
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

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }
}