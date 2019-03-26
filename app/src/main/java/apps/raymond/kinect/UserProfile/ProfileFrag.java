package apps.raymond.kinect.UserProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.R;

public class ProfileFrag extends Fragment implements View.OnClickListener{
    private final static String TAG = "ProfileFragment";

    DestroyProfileFrag destroyInterface;
    public interface DestroyProfileFrag {
        void destroyProfileFrag();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Core_Activity){
            try {
                Log.i(TAG,"We now have a desotryInterface");
                destroyInterface = (DestroyProfileFrag) context;
            } catch (ClassCastException e){
                Log.i(TAG,"Core_Group_Fragment does not implement UpdateGroupRecycler interface.");
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton closeBtn = view.findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(this);

        ImageButton logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.close_btn:
                Log.i(TAG,"hello?");
                destroyInterface.destroyProfileFrag();
                break;
            case R.id.logout_btn:
                logout();
                break;
        }
    }

    public void logout(){
        Log.d(TAG,"Logging out user:" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        AuthUI.getInstance().signOut(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"shit is about to die");
    }
}