package apps.raymond.kinect.Login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import apps.raymond.kinect.R;
import apps.raymond.kinect.Core_ViewModel;
import apps.raymond.kinect.UIResources.VerticalTextView;

public class Login_Fragment extends Fragment {

    private Login_ViewModel mViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Login_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private TextInputEditText txtUserName, txtPassword;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtUserName = view.findViewById(R.id.text_username);
        txtPassword = view.findViewById(R.id.text_login_password);

        Button btnLogin = view.findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = txtUserName.getText().toString();
                String password = txtPassword.getText().toString();
                if(validateInput(userName,password)) {
                    mViewModel.signInWithEmail(userName, password);
                }
            }
        });

        VerticalTextView txtSignUp = view.findViewById(R.id.vtext_signup);
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager viewPager = requireActivity().findViewById(R.id.viewpager_login);
                viewPager.setCurrentItem(1);
            }
        });
    }

    private boolean validateInput(String name,String password){
        boolean b = false;
        if(name.length()!=0 && password.length()!=0){
            b=true;
        }
        if(name.length()==0){
            txtUserName.setError("This field must not empty.");
        }
        if(password.length()==0){
            txtPassword.setError("This field must not empty.");
        }
        return b;
    }
}
