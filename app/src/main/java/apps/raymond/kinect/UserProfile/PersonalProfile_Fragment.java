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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import apps.raymond.kinect.R;
import apps.raymond.kinect.StartUp.Login_Activity;
import apps.raymond.kinect.ViewModels.ProfileActivity_ViewModel;

public class PersonalProfile_Fragment extends Fragment {
    private static final String USER_MODEL = "user";
    private ProfileActivity_ViewModel mActivityViewModel;

    public static PersonalProfile_Fragment newInstance(@NonNull User_Model user){
        PersonalProfile_Fragment fragment = new PersonalProfile_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_MODEL, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityViewModel = ViewModelProviders.of(requireActivity()).get(ProfileActivity_ViewModel.class);
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

        User_Model userModel = (User_Model) getArguments().get(USER_MODEL);

        //Views that display information pertaining to the profile.
        TextView txtName = view.findViewById(R.id.text_profile_name);
        TextView txtNumConnections = view.findViewById(R.id.text_connections_count);
        TextView txtNumLocations = view.findViewById(R.id.text_locations_count);
        TextView txtNumInterests = view.findViewById(R.id.text_interests_count);

        if(userModel.getName()!=null){
            String name = userModel.getName() + " " + userModel.getName2();
            txtName.setText(name);
        } else {
            txtName.setText(userModel.getEmail());
        }

        txtNumConnections.setText(String.valueOf(userModel.getNumconnections()));
        txtNumLocations.setText(String.valueOf(userModel.getNumlocations()));
        txtNumInterests.setText(String.valueOf(userModel.getNuminterests()));

        ImageButton btnReturn = view.findViewById(R.id.button_return);
        btnReturn.setOnClickListener((View v) -> requireActivity().onBackPressed());

        ImageButton btnLogout = view.findViewById(R.id.button_logout);
        btnLogout.setOnClickListener((View v)->{
            mActivityViewModel.signOut();
            Intent loginIntent = new Intent(getContext(), Login_Activity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            requireActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        });

        Button btnConnections = view.findViewById(R.id.button_connections);
        Button btnLocations = view.findViewById(R.id.button_locations);
        Button btnInterests = view.findViewById(R.id.button_interests);

        btnConnections.setOnClickListener((View v)->{
            User_Model user = mActivityViewModel.getUserModel().getValue();
            Connections_Fragment fragment = Connections_Fragment.newInstance(user);
        });
    }
}
