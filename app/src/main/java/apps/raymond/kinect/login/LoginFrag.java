package apps.raymond.kinect.login;

import android.arch.lifecycle.ViewModelProviders;
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

import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;
import apps.raymond.kinect.UIResources.VerticalTextView;

public class LoginFrag extends Fragment implements View.OnClickListener{
    private static final String TAG = "LoginFrag";
    private static final int NUM_FIELDS = 2;

    private SignIn signIn;
    public interface SignIn {
        void signedIn();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            signIn = (SignIn) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG,"Class cast exception." + e.getMessage());
        }
    }

    private Repository_ViewModel viewModel;
    FirebaseAuth mAuth;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.login_frag, container, false);
    }

    Button login_Btn;
    private TextInputEditText username_Txt, password_Txt;
    private TextInputEditText[] inputFields;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login_Btn = view.findViewById(R.id.login_btn);
        username_Txt = view.findViewById(R.id.login_txt);
        password_Txt = view.findViewById(R.id.password_txt);

        login_Btn.setOnClickListener(this);

        inputFields = new TextInputEditText[NUM_FIELDS];
        inputFields[0] = username_Txt;
        inputFields[1] = password_Txt;

        VerticalTextView signupTxt = view.findViewById(R.id.signup_vtxt);
        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Swap views to login");
                ViewPager viewPager = getActivity().findViewById(R.id.login_container);
                viewPager.setCurrentItem(1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.login_btn:
                if (validate(inputFields)) {
                    viewModel.signIn(username_Txt.getText().toString(),password_Txt.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.i(TAG,"THIS SHOULD BE THIRD");
                                    signIn.signedIn();
                                } else if (task.getException()!=null){
                                    Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    return;
                }
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
