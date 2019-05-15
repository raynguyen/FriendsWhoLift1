/*
 * Consider using continueWithTask after finishing the signUp method to sign the user in!
 */

package apps.raymond.kinect.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.kinect.R;
import apps.raymond.kinect.UIResources.VerticalTextView;
import apps.raymond.kinect.UserProfile.User_Model;

public class SignUp_Fragment extends SignInCallback_Fragment implements View.OnClickListener{
    private static final String TAG = "SignUp_Fragment";
    private SignInCallback signInCallback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            signInCallback = (SignInCallback) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG,"Class cast exception." + e.getMessage());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG,"CREATING ISNTANCE OF SIGNUP FRAGMENT");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VerticalTextView loginTxt = view.findViewById(R.id.vtext_login);
        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Swap views to login");
                ViewPager viewPager = requireActivity().findViewById(R.id.viewpager_login);
                viewPager.setCurrentItem(0);
            }
        });
    }

    /*username = txtUserName.getText().toString();
            password = txtPassword1.getText().toString();
            User_Model userModel = new User_Model(username,"invisible");
            mLoginViewModel.createUserByEmail(userModel,password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(activity,"Successfully registered "+username,Toast.LENGTH_SHORT).show();
                                signInCallback.signIn();
                            } else {
                                Toast.makeText(activity,"Error registering user.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/

    private boolean validate(){
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.vtext_login:
                ViewPager viewPager = requireActivity().findViewById(R.id.viewpager_login);
                viewPager.setCurrentItem(0);
                break;
            default:
                break;
        }
    }
}
