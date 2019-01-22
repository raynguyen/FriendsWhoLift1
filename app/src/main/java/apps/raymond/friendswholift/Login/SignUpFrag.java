package apps.raymond.friendswholift.Login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import apps.raymond.friendswholift.R;

public class SignUpFrag extends Fragment implements View.OnClickListener {
    private static final String TAG = "SignupFrag";

    Button register_Btn, login_Btn;
    TextInputEditText username_Txt, password_Txt, repassword_Txt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.signup_frag, container, false);

        register_Btn = (Button) v.findViewById(R.id.register_btn);
        login_Btn = (Button) v.findViewById(R.id.login_btn);
        username_Txt = (TextInputEditText) v.findViewById(R.id.login_txt);
        password_Txt = (TextInputEditText) v.findViewById(R.id.password_txt);
        repassword_Txt = (TextInputEditText) v.findViewById(R.id.repassword_txt);

        register_Btn.setOnClickListener(this);
        login_Btn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v){
        int i = v.getId();

        String username;
        String password;

        switch(i){
            case R.id.register_btn:
                //We should not create the setError on blank fields unless the signup button is clicked.
                if(TextUtils.isEmpty(username_Txt.getText().toString()) ||
                        TextUtils.isEmpty(password_Txt.getText().toString())){
                    username_Txt.setError("This field cannot be empty.");
                    return;
                } else {
                    username = username_Txt.getText().toString();
                    password = password_Txt.getText().toString();
                    Toast.makeText(getContext(),username + password,Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.login_btn:
                Toast.makeText(getContext(),"Setup login frag return",Toast.LENGTH_LONG).show();
                break;
        }


    }
}
