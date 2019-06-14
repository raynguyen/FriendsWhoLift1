/*
 * ToDo:
 *  Ideally, we would want to remove imports requiring FirebaseAuth from this class. Not yet
 *  sure the pattern required in order to accomplish this
 */

package apps.raymond.kinect.Login;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import apps.raymond.kinect.R;
import apps.raymond.kinect.UIResources.VerticalTextView;
import apps.raymond.kinect.UserProfile.User_Model;

/*
ToDo: If the user email already exists, the app will continue to stay on the pending screen.
 */
public class SignUp_Fragment extends Fragment implements View.OnClickListener{
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
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    private TextView txtUserID, txtPassword1, txtPassword2;
    private ProgressBar progressBar;
    private Button btnSignUp;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtUserID = view.findViewById(R.id.text_userid);
        txtPassword1 = view.findViewById(R.id.text_password1);
        txtPassword2 = view.findViewById(R.id.text_password2);
        progressBar = view.findViewById(R.id.progress_signup);
        btnSignUp = view.findViewById(R.id.button_signup);
        btnSignUp.setOnClickListener(this);

        VerticalTextView loginTxt = view.findViewById(R.id.vtext_login);
        loginTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.button_signup:
                progressBar.setVisibility(View.VISIBLE);
                btnSignUp.setVisibility(View.GONE);
                String userID = txtUserID.getText().toString();
                String p1 = txtPassword1.getText().toString();
                String p2 = txtPassword2.getText().toString();
                if(validateInput(userID, p1, p2)){
                    mViewModel.registerWithEmail(userID,p1, new User_Model(userID));
                }
                break;
            case R.id.vtext_login:
                ViewPager viewPager = requireActivity().findViewById(R.id.viewpager_login);
                viewPager.setCurrentItem(0);
                break;
        }
    }

    private boolean validateInput(String userID, String p1, String p2){
        boolean b = true;
        if(userID.length()==0){
            txtUserID.setError("This must not be empty!");
            b=false;
        }
        if(p1.length()<6){
            txtPassword1.setError("Your password does not meet the required length!");
            b=false;
        }
        if(!p1.equals(p2)){
            txtPassword2.setError("Your password fields do not match!");
            b=false;
        }
        return b;
    }

}
