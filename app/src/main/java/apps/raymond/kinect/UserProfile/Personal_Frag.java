/*
ToDo:
When a mutable field is selected, compare the change to the original value and if they are different,
store the new value in the respective field. Other option is to compare the old user model object
to a new temporary one and if they are not equal in all aspects, overwrite the existing user model.
 */

package apps.raymond.kinect.UserProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.Person;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;

public class Personal_Frag extends Fragment implements View.OnClickListener{
    private final static String TAG = "ProfileFragment";
    private final static String USER = "UserModel";

    ProfileFragInt destroyInterface;
    public interface ProfileFragInt {
        void destroyProfileFrag();
        void signOut();
    }

    public static Personal_Frag newInstance(UserModel userModel){
        Personal_Frag frag = new Personal_Frag();
        Bundle args = new Bundle();
        args.putParcelable(USER,userModel);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Core_Activity){
            try {
                destroyInterface = (ProfileFragInt) context;
            } catch (ClassCastException e){
                Log.i(TAG,"Core_Group_Fragment does not implement UpdateGroupRecycler interface.");
            }
        }
    }

    Repository_ViewModel viewModel;
    UserModel userModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel = getArguments().getParcelable(USER);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.personal_fragment,container,false);
    }

    TextView nameTxt, connectionsTxt, interestsTxt;
    ImageButton socialEditLock;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton closeBtn = view.findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(this);
        ImageButton logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        nameTxt = view.findViewById(R.id.name_txt);
        nameTxt.setText(userModel.getEmail());

        Button connectionsBtn = view.findViewById(R.id.connections_btn);
        connectionsBtn.setOnClickListener(this);
        Button interestsBtn = view.findViewById(R.id.interests_btn);
        interestsBtn.setOnClickListener(this);

        connectionsTxt = view.findViewById(R.id.connections_text);
        interestsTxt = view.findViewById(R.id.interests_text);

        fetchUserInfo();

        socialEditLock = view.findViewById(R.id.social_edit_lock);
        socialEditLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.close_btn:
                destroyInterface.destroyProfileFrag();
                break;
            case R.id.logout_btn:
                destroyInterface.signOut();
                break;
            case R.id.social_edit_lock:
                editSocialSettings();
        }
    }

    /*
    Consider retrieving all data required for this fragment in the calling activity and passing as
    a bundle to a static method newInstance().
     */
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

    private void editSocialSettings(){
        socialEditLock.setImageResource(R.drawable.baseline_lock_open_black_18dp);
    }
}