/*
 * ToDo: Determine what we want to show when viewing a user's profile.
 *  Events they recently joined? Connections they've created? Common connections, Locaitons, EventS?
 * Fragment class that holds the details for a user's profile.
 */
package apps.raymond.kinect.UserProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.raymond.kinect.R;

public class ViewProfile_Fragment extends Fragment {

    private User_Model mUserModel;
    public static ViewProfile_Fragment newInstance(User_Model user){
        ViewProfile_Fragment fragment = new ViewProfile_Fragment();
        Bundle args = new Bundle();
        args.putParcelable("user",user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserModel = getArguments().getParcelable("user");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_profile,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
