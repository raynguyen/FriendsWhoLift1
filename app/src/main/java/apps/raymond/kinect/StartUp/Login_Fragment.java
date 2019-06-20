/*
 * ToDo:
 *  1. Ideally, we would want to remove imports requiring FirebaseAuth from this class. Not yet
 *  sure the pattern required in order to accomplish this
 *  2. Determine the error type if log-in was unsuccessful and set the appropriate error messages
 *  to the fields that must be corrected if applicable.
 */
package apps.raymond.kinect.StartUp;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import apps.raymond.kinect.R;
import apps.raymond.kinect.UIResources.VerticalTextView;

public class Login_Fragment extends Fragment implements View.OnClickListener {

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

    private TextView txtUserName, txtPassword;
    private ProgressBar progressBar;
    private Button btnLogin;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtUserName = view.findViewById(R.id.text_userid);
        txtPassword = view.findViewById(R.id.text_password1);
        progressBar = view.findViewById(R.id.progress_login);
        btnLogin = view.findViewById(R.id.button_login);
        btnLogin.setOnClickListener(this);

        VerticalTextView txtSignUp = view.findViewById(R.id.vtext_signup);
        txtSignUp.setOnClickListener(this);
    }

    //ToDo: Renable log in button if the log in fails.
    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.button_login:
                final String userName = txtUserName.getText().toString();
                String password = txtPassword.getText().toString();
                if(validateInput(userName,password)) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);
                    mViewModel.signInWithEmail(userName, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        mViewModel.loadExistingUser(userName);
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        btnLogin.setVisibility(View.VISIBLE);
                                        Log.w("LoginFragment", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(requireActivity(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
            case R.id.vtext_signup:
                ViewPager viewPager = requireActivity().findViewById(R.id.viewpager_login);
                viewPager.setCurrentItem(1);
                break;
        }
    }

    /**
     * ToDo: If the wrong credentials are entered, we need to grab the throwable and determine what
     *  errors have to be set and for which fields.
     *
     * Check to see that the required fields have been completed.
     * @param name User ID parameter converted to text
     * @param password Password parameter converted to text
     * @return True if the user form has been correctly completed.
     */
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
