package apps.raymond.friendswholift.Login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.friendswholift.R;

public class SignUpFrag extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignUpFrag";
    private static final int NUM_TXT = 3; //Number of TextFields
    Button register_Btn, login_Btn;
    TextInputEditText username_Txt, password_Txt, repassword_Txt;

    TextInputEditText[] inputFields;
    FirebaseAuth mAuth;

    SignIn signIn;
    public interface SignIn {
        void emailSignIn(final String userName, final String password);
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
                String username,password;
                username = username_Txt.getText().toString();
                password = password_Txt.getText().toString();
                signUp(username, password);
                signIn.emailSignIn(username,password);
                break;
            case R.id.login_btn:
                //Returns to the Login fragment when 'LOGIN' is clicked.
                ViewPager viewPager = getActivity().findViewById(R.id.login_container);
                viewPager.setCurrentItem(0);
                break;
        }


    } //bottom of onClick()

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
        if(!inputFields[1].getText().toString()
                .equals(inputFields[2].getText().toString())){
            inputFields[2].setError("Passwords do not match.");
            b = false;
        }
        return b;
    }

    private void signUp(final String username, final String password){
        mAuth.createUserWithEmailAndPassword(username,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"createUserWithEmail:success");
                            Toast.makeText(getContext(),"Successfully registered user.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.w(TAG,"createUserWithEmail:failure",
                                    task.getException());
                            Toast.makeText(getContext(),"Failed to add user.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
