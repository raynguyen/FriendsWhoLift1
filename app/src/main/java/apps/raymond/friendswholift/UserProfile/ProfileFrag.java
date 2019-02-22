package apps.raymond.friendswholift.UserProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import apps.raymond.friendswholift.R;

public class ProfileFrag extends Fragment {
    private final static String TAG = "ProfileFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_fragment,container,false);
    }

    private ViewFlipper viewFlipper;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewFlipper = view.findViewById(R.id.profile_name_flipper);
        if(viewFlipper.getDisplayedChild() == 0){
            TextView nameTxt = view.findViewById(R.id.profile_name_txt);
            nameTxt.setText("hello");
        }
        Log.i(TAG,"viewflipper child is currently: "+viewFlipper.getDisplayedChild());
    }
}
