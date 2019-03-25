package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import apps.raymond.kinect.Groups.Core_Group_Fragment;

public class Core_Activity_Adapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 2;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    public Core_Activity_Adapter(FragmentManager fm){
        super(fm);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,position);
        registeredFragments.put(position,fragment);
        return fragment;
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new Core_Events_Fragment();
            case 1:
                return new Core_Group_Fragment();
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
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}