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

import apps.raymond.friendswholift.R;

public class LoginFrag extends Fragment implements View.OnClickListener{
    private static final String TAG = "LoginFrag";
    private static final int NUM_FIELDS = 2;

    Button login_Btn, signUp_Btn;
    private TextInputEditText username_Txt, password_Txt;
    private TextInputEditText[] inputFields;
    FirebaseAuth mAuth;
    private SignIn signIn;
    private LoginViewModel mLoginViewModel;

    public interface SignIn {
        void signedIn();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = new LoginViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        View v = inflater.inflate(R.layout.login_frag, container, false);
        login_Btn = v.findViewById(R.id.login_btn);
        signUp_Btn = v.findViewById(R.id.signup_btn);
        username_Txt = v.findViewById(R.id.login_txt);
        password_Txt = v.findViewById(R.id.password_txt);

        login_Btn.setOnClickListener(this);
        signUp_Btn.setOnClickListener(this);

        inputFields = new TextInputEditText[NUM_FIELDS];
        inputFields[0] = username_Txt;
        inputFields[1] = password_Txt;
        return v;
    }

    /*
    When the fragment is created, it must attach to the host activity. When this occurs, we
    specify that this host Activity implements our interface.
    */
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
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.login_btn:
                Log.i(TAG,"Attempting to log in.");
                if (validate(inputFields)) {
                    mLoginViewModel.signIn(username_Txt.getText().toString(),password_Txt.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    signIn.signedIn();
                                }
                            }
                        });
                    return;
                }
                break;
            case R.id.signup_btn:
                //Displays SignUp fragment when 'SIGNUP' is clicked.
                ViewPager viewPager = getActivity().findViewById(R.id.login_container);
                viewPager.setCurrentItem(1);
                break;
        }
    }

    /*
    The validate method checks all the EditText fields for empty inputs. Any empty inputs will
    receive the .setError with a message saying it must not be empty.
    Returns True when there are no empty fields and False otherwise.
     */
    private boolean validate(@NonNull TextInputEditText[] inputFields){
        //False means there is at least one empty field.
        int i = 0;
        boolean b = true;
        for(TextInputEditText field : inputFields) { // ToDo: Storing the fields into array is probably overkill.
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
