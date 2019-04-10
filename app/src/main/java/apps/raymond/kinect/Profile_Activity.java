package apps.raymond.kinect;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.UserProfile.UserModel;

public class Profile_Activity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileActivity";

    TextView nameTxt, connectionsTxt, interestsTxt;
    ImageButton socialEditLock;
    ImageView profilePic;
    Repository_ViewModel viewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        Log.i(TAG,"Creating profile activity.");

        viewModel = ViewModelProviders.of(this).get(Repository_ViewModel.class);

        ImageButton closeBtn = findViewById(R.id.return_btn);
        closeBtn.setOnClickListener(this);
        ImageButton logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        profilePic = findViewById(R.id.profile_pic);
        profilePic.setOnClickListener(this);

        nameTxt = findViewById(R.id.name_txt);
        nameTxt.setText("ooker dooker");

        Button connectionsBtn = findViewById(R.id.connections_btn);
        connectionsBtn.setOnClickListener(this);
        Button interestsBtn = findViewById(R.id.interests_btn);
        interestsBtn.setOnClickListener(this);

        connectionsTxt = findViewById(R.id.connections_txt);
        interestsTxt = findViewById(R.id.interests_txt);

        fetchUserInfo();

        socialEditLock = findViewById(R.id.social_edit_lock);
        socialEditLock.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"Pausing profile activity.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Destroying profile activity.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.return_btn:
                onBackPressed();
                break;
            case R.id.logout_btn:
                break;
            case R.id.profile_pic:
                break;
            case R.id.connections_btn:
                break;
            case R.id.interests_btn:
                break;
            case R.id.social_edit_lock:
                break;
        }
    }

    List<UserModel> connectionsList = new ArrayList<>();
    List<String> interestsList = new ArrayList<>();
    private void fetchUserInfo(){
        //READ FROM THE SHARED PREFERENCES HERE!
        viewModel.fetchConnections().addOnCompleteListener(new OnCompleteListener<List<UserModel>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserModel>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        connectionsList.addAll(task.getResult());
                    }
                    connectionsTxt.setText(String.valueOf(connectionsList.size()));
                } else {
                    Toast.makeText(getBaseContext(),"hello",Toast.LENGTH_LONG).show();
                }
            }
        });
        //ToDo This has not been implemented. Need to structure the data in FireStore
        /*viewModel.fetchInterests().addOnCompleteListener(new OnCompleteListener<List<String>>() {
            @Override
            public void onComplete(@NonNull Task<List<String>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        interestsList.addAll(task.getResult());
                    }
                    interestsTxt.setText(String.valueOf(interestsList.size()));
                } else {
                    Toast.makeText(getContext(),"Error retrieving user interests.",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
}
