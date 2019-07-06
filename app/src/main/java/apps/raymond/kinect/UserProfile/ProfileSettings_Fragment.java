package apps.raymond.kinect.UserProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import apps.raymond.kinect.R;
import apps.raymond.kinect.StartUp.Login_Activity;
import apps.raymond.kinect.ViewModels.ProfileActivity_ViewModel;
import apps.raymond.kinect.ViewModels.ProfileFragment_ViewModel;

public class ProfileSettings_Fragment extends Fragment {
    private ProfileActivity_ViewModel mViewModel;

    public static ProfileSettings_Fragment newInstance(User_Model user){
        ProfileSettings_Fragment fragment = new ProfileSettings_Fragment();
        Bundle args = new Bundle();
        args.putParcelable("user",user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User_Model userModel = getArguments().getParcelable("user");
        mViewModel = ViewModelProviders.of(requireActivity()).get(ProfileActivity_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_personal, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnReturn = view.findViewById(R.id.button_return);
        btnReturn.setOnClickListener((View v) -> requireActivity().onBackPressed());

        ImageButton btnLogout = view.findViewById(R.id.button_logout);
        btnLogout.setVisibility(View.VISIBLE);
        btnLogout.setOnClickListener((View v)->{
            mViewModel.signOut();
            Intent loginIntent = new Intent(getContext(), Login_Activity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            requireActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        });
    }
}
