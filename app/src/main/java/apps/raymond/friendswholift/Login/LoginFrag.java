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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import apps.raymond.friendswholift.R;

public class LoginFrag extends Fragment implements View.OnClickListener{
    private static final String TAG = "LoginFrag";
    private static final int NUM_FIELDS = 2;

    Button login_Btn, signUp_Btn;
    TextInputEditText username_Txt, password_Txt;
    TextInputEditText[] inputFields;
    FirebaseAuth mAuth;

    public SignedIn signedIn;

    public interface SignedIn {
        void authorized();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        View v = inflater.inflate(R.layout.login_frag, container, false);
        login_Btn = (Button) v.findViewById(R.id.login_btn);
        signUp_Btn = (Button) v.findViewById(R.id.signup_btn);
        username_Txt = (TextInputEditText) v.findViewById(R.id.login_txt);
        password_Txt = (TextInputEditText) v.findViewById(R.id.password_txt);

        login_Btn.setOnClickListener(this);
        signUp_Btn.setOnClickListener(this);

        inputFields = new TextInputEditText[NUM_FIELDS];
        inputFields[0] = username_Txt;
        inputFields[1] = password_Txt;
        return v;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            //We need to get an instance of the Class that will execute our interface method.
            signedIn = (SignedIn) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG,"Class cast exception." + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        String username;
        String password;

        switch (i) {
            case R.id.login_btn:

                if (!validate(inputFields)) {
                    Log.d(TAG, "One or more input parameters empty.");
                    return;
                }
                username = username_Txt.getText().toString();
                password = password_Txt.getText().toString();
                mAuth.signInWithEmailAndPassword(username,password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>(){
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task){
                                if(task.isSuccessful()){
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.d(TAG,"Successfully logged in " + user);

                                    //ToDo: Stop LoginAct and start the MainAct.
                                    signedIn.authorized();
                                } else {
                                    Log.e(TAG, "Failed to log in user." +
                                            task.getException().getMessage());
                                    Toast.makeText(getContext(),"Authentication failed.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                Log.d(TAG, "Successfully authorized user.");
                break;
            case R.id.signup_btn:
                //Displays SignUp fragment when 'SIGNUP' is clicked.
                ViewPager viewPager = getActivity().findViewById(R.id.login_container);
                viewPager.setCurrentItem(1);
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
        return b;
    }


}
