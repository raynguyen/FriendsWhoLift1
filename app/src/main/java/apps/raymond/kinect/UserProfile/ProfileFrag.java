package apps.raymond.kinect.UserProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;

public class ProfileFrag extends Fragment implements View.OnClickListener{
    private final static String TAG = "ProfileFragment";

    DestroyProfileFrag destroyInterface;
    public interface DestroyProfileFrag {
        void destroyProfileFrag();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Core_Activity){
            try {
                destroyInterface = (DestroyProfileFrag) context;
            } catch (ClassCastException e){
                Log.i(TAG,"Core_Group_Fragment does not implement UpdateGroupRecycler interface.");
            }
        }
    }

    Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment,container,false);
    }

    TextView connectionsTxt, interestsTxt;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton closeBtn = view.findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(this);
        ImageButton logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        Button connectionsBtn = view.findViewById(R.id.connections_btn);
        connectionsBtn.setOnClickListener(this);
        Button interestsBtn = view.findViewById(R.id.interests_btn);
        interestsBtn.setOnClickListener(this);

        connectionsTxt = view.findViewById(R.id.connections_text);
        interestsTxt = view.findViewById(R.id.interests_text);

        fetchUserInfo();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.close_btn:
                Log.i(TAG,"hello?");
                destroyInterface.destroyProfileFrag();
                break;
            case R.id.logout_btn:
                logout();
                break;
        }
    }

    public void logout(){
        Log.d(TAG,"Logging out user:" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        AuthUI.getInstance().signOut(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"shit is about to die");
    }

    List<UserModel> connectionsList = new ArrayList<>();
    List<String> interestsList = new ArrayList<>();
    private void fetchUserInfo(){
        viewModel.fetchConnections().addOnCompleteListener(new OnCompleteListener<List<UserModel>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserModel>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        connectionsList.addAll(task.getResult());
                    }
                    connectionsTxt.setText(String.valueOf(connectionsList.size()));
                } else {
                    Toast.makeText(getContext(),"Error retrieving connections.",Toast.LENGTH_SHORT).show();
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