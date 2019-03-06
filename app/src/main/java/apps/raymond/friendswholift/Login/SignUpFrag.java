/*
 * Consider using continueWithTask after finishing the signUp method to sign the user in!
 */

package apps.raymond.friendswholift.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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

import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.UserProfile.UserModel;

public class SignUpFrag extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignUpFrag";
    private static final int NUM_TXT = 3; //Number of TextFields
    Button register_Btn, login_Btn;
    TextInputEditText username_Txt, password_Txt, repassword_Txt;

    TextInputEditText[] inputFields;
    FirebaseAuth mAuth;

    LoginViewModel mLoginViewModel;

    SignIn signIn;
    public interface SignIn {
        void signedIn();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.signup_frag, container, false);
        mAuth = FirebaseAuth.getInstance();

        register_Btn = v.findViewById(R.id.register_btn);
        login_Btn = v.findViewById(R.id.login_btn);
        username_Txt = v.findViewById(R.id.login_txt);
        password_Txt = v.findViewById(R.id.password_txt);
        repassword_Txt = v.findViewById(R.id.repassword_txt);

        register_Btn.setOnClickListener(this);
        login_Btn.setOnClickListener(this);

        inputFields = new TextInputEditText[NUM_TXT];
        inputFields[0] = username_Txt;
        inputFields[1] = password_Txt;
        inputFields[2] = repassword_Txt;

        mLoginViewModel = new LoginViewModel();
        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            //We need to get an instance of the Class that will execute our interface method.
            signIn = (SignIn) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG,"Class cast exception." + e.getMessage());
        }
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        switch(i){
            case R.id.register_btn:
                //Attempts to register the user if the fields are not blank.
                if(!validate(inputFields)){
                    Log.d(TAG,"Empty or incorrect input in SignUp fields.");
                    return;
                }
                final String username,password;
                username = username_Txt.getText().toString();
                password = password_Txt.getText().toString();
                UserModel userModel = new UserModel(username,"invisible");
                mLoginViewModel.createUserByEmail(userModel,password)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(),"Successfully registered "+username,Toast.LENGTH_SHORT).show();
                                    signIn.signedIn();
                                } else {
                                    Toast.makeText(getContext(),"Error registering user.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case R.id.login_btn:
                //Returns to the Login fragment when 'LOGIN' is clicked.
                ViewPager viewPager = getActivity().findViewById(R.id.login_container);
                viewPager.setCurrentItem(0);
                break;
        }
    }

    private boolean validate(@NonNull TextInputEditText[] inputFields){
        //False means there is at least one empty field.
        int i = 0;
        boolean b = true;
        for(TextInputEditText field : inputFields) {
            if (TextUtils.isEmpty(field.getText().toString())) {
                field.setError("This field cannot be empty.");
                ++i;
            }
        }
        if(i != 0){
            b = false;
        }
        if(inputFields[1].getText().length() < 6){
            inputFields[1].setError("Your password must be at least 6 characters.");
            b = false;
        }
        if(!inputFields[1].getText().toString()
                .equals(inputFields[2].getText().toString())){
            inputFields[2].setError("Passwords do not match.");
            b = false;
        }
        return b;
    }

}
