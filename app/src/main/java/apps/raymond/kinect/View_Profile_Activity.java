package apps.raymond.kinect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import apps.raymond.kinect.UserProfile.UserModel;

public class View_Profile_Activity extends FragmentActivity {
    private static final String TAG ="ViewProfile";
    public static final String USER = "UserModel";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile_activity);
        Bundle args = getIntent().getExtras();

        UserModel userModel = args.getParcelable(USER);

        Log.i(TAG,"Starting view profile activity for user: " + userModel.getEmail());
    }

}
