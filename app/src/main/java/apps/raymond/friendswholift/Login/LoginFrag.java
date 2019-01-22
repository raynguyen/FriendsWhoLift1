package apps.raymond.friendswholift.Login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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

public class LoginFrag extends Fragment implements View.OnClickListener{

    private static final String TAG = "LoginFrag";
    Button login_Btn;
    Button signUp_Btn;
    TextInputEditText username_Txt;
    TextInputEditText password_Txt;
    FirebaseAuth mAuth;

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

        return v;
    }

    @Override
    public void onClick(View v){
        int i = v.getId();

        String username;
        String password;

        if(TextUtils.isEmpty(username_Txt.getText().toString()) ||
                TextUtils.isEmpty(password_Txt.getText().toString())){
            username_Txt.setError("This field cannot be empty.");
            return;
        } else {
            username = username_Txt.getText().toString();
            password = password_Txt.getText().toString();
            Toast.makeText(getContext(),username + password,Toast.LENGTH_LONG).show();
        }

        switch(i){
            case R.id.login_btn:
                /**/
                mAuth.createUserWithEmailAndPassword(username,password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener< AuthResult >(){
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG,"createUserWithEmail:success");
                                    Toast.makeText(getContext(),"Successfully added user.",Toast.LENGTH_LONG).show();
                                } else {
                                    Log.w(TAG,"createUserWithEmail:failure",task.getException());
                                    Toast.makeText(getContext(),"Failed to add user.",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                Toast.makeText(getContext(),"Clicked LOGIN",Toast.LENGTH_LONG).show();
                break;
            case R.id.signup_btn:
                Toast.makeText(getContext(),"Clicked SIGNUP",Toast.LENGTH_LONG).show();
                break;
        }
    }



}
