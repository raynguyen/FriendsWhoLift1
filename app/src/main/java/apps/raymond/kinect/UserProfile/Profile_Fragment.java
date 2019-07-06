package apps.raymond.kinect.UserProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.raymond.kinect.ProfileRecyclerAdapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.ProfileFragment_ViewModel;

public class Profile_Fragment extends Fragment implements ProfileRecyclerAdapter.ProfileClickListener {
    private static final String USERMODEL = "user"; //Key to retrieve the User_Model for this fragment
    private ProfileFragment_ViewModel mViewModel;
    private ViewProfileInterace mInterface;

    public interface ViewProfileInterace{
        void inflateProfileFragment(User_Model profileModel);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mInterface = (ViewProfileInterace) context;
        } catch (ClassCastException e){
            //Some Error.
        }
    }

    public static Profile_Fragment newInstance(User_Model profileModel){
        Profile_Fragment fragment = new Profile_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(USERMODEL, profileModel);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileFragment_ViewModel.class);
        User_Model profileModel = (User_Model) getArguments().get(USERMODEL);
        mViewModel.setProfileModel(profileModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Calls upon the interface method implemented by the parent activity to instantiated a new
     * Profile_Fragment to view the details of a user.
     * @param userModel User_Model object of which a new Profile_Fragment is inflated for.
     */
    @Override
    public void onProfileClick(User_Model userModel) {
        mInterface.inflateProfileFragment(userModel);
    }

    @Override
    public void onProfileLongClick(User_Model userModel) {

    }
}
