package apps.raymond.kinect.UserProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apps.raymond.kinect.R;

public class Profile_Frag extends Fragment {
    private static final String USER_MODEL = "User_Model";

    public Profile_Frag newInstance(User_Model user){
        Profile_Frag frag = new Profile_Frag();
        Bundle args = new Bundle();
        args.putParcelable(USER_MODEL,user);
        frag.setArguments(args);
        return frag;
    }

    private User_Model userModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        userModel = args.getParcelable(USER_MODEL);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_,container,false);
    }

    TextView nameTxt;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameTxt = view.findViewById(R.id.name_txt);
        nameTxt.setText(userModel.getEmail());
    }
}
