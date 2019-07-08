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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.ProfileRecyclerAdapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.ProfileActivity_ViewModel;
import apps.raymond.kinect.ViewModels.ProfileFragment_ViewModel;

public class Profile_Fragment extends Fragment implements
        ProfileRecyclerAdapter.ProfileClickListener, YesNoDialog.YesNoCallback{
    private static final String PROFILE_MODEL = "user"; //Key to retrieve the User_Model for this fragment
    private static final String PROFILE_ID = "profileID";
    private static final String YESNO_DIALOG = "dialog";
    private ProfileFragment_ViewModel mFragViewModel;
    private ProfileActivity_ViewModel mActivityViewModel;
    private ViewProfileInterface mInterface;

    public interface ViewProfileInterface {
        void inflateProfileFragment(User_Model profileModel);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mInterface = (ViewProfileInterface) context;
        } catch (ClassCastException e){
            //Some Error.
        }
    }

    public static Profile_Fragment newInstance(@Nullable User_Model profileModel, @Nullable String profileID){
        Profile_Fragment fragment = new Profile_Fragment();
        Bundle args = new Bundle();
        if(profileID!=null){
            args.putString(PROFILE_ID, profileID);
        }
        if(profileModel!=null){
            args.putParcelable(PROFILE_MODEL, profileModel);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragViewModel = ViewModelProviders.of(this).get(ProfileFragment_ViewModel.class);
        mActivityViewModel = ViewModelProviders.of(requireActivity()).get(ProfileActivity_ViewModel.class);
        try{
            User_Model profileModel = (User_Model) getArguments().get(PROFILE_MODEL);
            mFragViewModel.setProfileModel(profileModel);
        } catch (NullPointerException e){
            Log.w("ProfileFragment",e.toString());
        }

        if(getArguments().containsKey(PROFILE_MODEL)){
            User_Model profileModel = (User_Model) getArguments().get(PROFILE_MODEL);
            mFragViewModel.setProfileModel(profileModel);
        } else if (getArguments().containsKey(PROFILE_ID)){
            //Have to fetch the Model from database.
            String profileID = getArguments().getString(PROFILE_ID);
            mFragViewModel.loadProfileModel(profileID);
        }

    }

    private ProgressBar progressAction;
    private ImageButton btnConnect,btnDeleteConnection;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnReturn = view.findViewById(R.id.button_return);
        btnReturn.setOnClickListener((View v) -> requireActivity().onBackPressed());

        progressAction = view.findViewById(R.id.progress_profile_action);
        btnConnect = view.findViewById(R.id.button_connect);
        btnDeleteConnection = view.findViewById(R.id.button_delete_connection);
        ImageButton btnPendingConnection = view.findViewById(R.id.button_pending_connection);

        btnConnect.setOnClickListener((View v)->{
            v.setVisibility(View.GONE);
            progressAction.setVisibility(View.VISIBLE);
            if(mFragViewModel.getProfileModel().getValue()!=null){
                User_Model userModel = mActivityViewModel.getUserModel().getValue();
                String userID = userModel.getEmail();
                User_Model profileModel = mFragViewModel.getProfileModel().getValue();
                String profileID = profileModel.getEmail();

                mActivityViewModel.requestUserConnection(userID, userModel, profileID, profileModel)
                        .addOnCompleteListener((@NonNull Task<Void> task)-> {
                            progressAction.setVisibility(View.INVISIBLE);
                            if(task.isSuccessful()){
                                btnPendingConnection.setVisibility(View.VISIBLE);
                            } else {
                                btnConnect.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        btnDeleteConnection.setOnClickListener((View v)->{
            YesNoDialog dialog = YesNoDialog.newInstance(YesNoDialog.WARNING,getResources().getString(R.string.delete_connection));
            dialog.show(getFragmentManager(),YESNO_DIALOG);
        });

        TextView txtName = view.findViewById(R.id.text_profile_name);
        TextView txtConnectionsNum = view.findViewById(R.id.text_connections_count);
        TextView txtLocationsNum = view.findViewById(R.id.text_locations_count);
        TextView txtInterestsNum = view.findViewById(R.id.text_interests_count);

        /*
         * We bind an observer to the ViewModel's ProfileModel. Whenever the LiveData's value becomes
         * non-null, we know we are viewing the profile of another user. We then query the database
         * for a connection with the current user.
         */
        mFragViewModel.getProfileModel().observe(this,(User_Model profileModel)->{
            String profileID = profileModel.getEmail();
            if(profileModel.getName()!=null && profileModel.getName2()!=null){
                String name = profileModel.getName() + " " + profileModel.getName2();
                txtName.setText(name);
            } else {
                txtName.setText(profileID);
            }

            txtConnectionsNum.setText(String.valueOf(profileModel.getNumconnections()));
            txtInterestsNum.setText(String.valueOf(profileModel.getNuminterests()));
            txtLocationsNum.setText(String.valueOf(profileModel.getNumlocations()));

            String userID = mActivityViewModel.getUserModel().getValue().getEmail();
            mActivityViewModel.checkForConnection(userID,profileID).addOnCompleteListener((Task<Boolean> task)->{
                if(task.getResult()){
                    btnDeleteConnection.setVisibility(View.VISIBLE);
                } else {
                    mActivityViewModel.checkForPendingConnection(userID,profileID)
                            .addOnCompleteListener((Task<Boolean> task2)->{
                                if(task2.isSuccessful()){
                                    if(task2.getResult()){
                                        btnPendingConnection.setVisibility(View.VISIBLE);
                                    } else {
                                        btnConnect.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.w("ProfileFragment","Unable to determine if the user has a connection or pending connection.");
                                }
                            });
                }
            });
        });

        Button btnConnections = view.findViewById(R.id.button_connections);
        btnConnections.setOnClickListener((View v)->{
            //Hide the RecentActivities view and inflate the ViewPager to the Connections page.
        });

        Button btnLocations = view.findViewById(R.id.button_locations);
        btnLocations.setOnClickListener((View v)->{
            //Flip ViewPager to the map fragment.
        });
        Button btnInterests = view.findViewById(R.id.button_interests);
        btnInterests.setOnClickListener((View v)->{
            //Flip ViewPager to the interests fragment.
        });
    }

    /**
     * Calls upon the interface method implemented by the parent activity to instantiated a new
     * Profile_Fragment to view the details of a user.
     * @param userModel User_Model object of which a new Profile_Fragment is inflated for.
     */
    @Override
    public void onProfileClick(User_Model userModel) {
        mInterface.inflateProfileFragment(userModel);
    }

    @Override
    public void onProfileLongClick(User_Model userModel) {

    }

    /**
     * Interface implementation for when the user clicks the positive button on the DeleteConnection
     * dialog fragment.
     */
    @Override
    public void onDialogPositive() {
        progressAction.setVisibility(View.VISIBLE);
        btnDeleteConnection.setVisibility(View.GONE);
        String userID = mActivityViewModel.getUserModel().getValue().getEmail();
        String profileID = mFragViewModel.getProfileModel().getValue().getEmail();
        mActivityViewModel.deleteUserConnection(userID, profileID).addOnCompleteListener((Task<Void> task)-> {
            progressAction.setVisibility(View.INVISIBLE);
            if(task.isSuccessful()){
                btnConnect.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(),"Error deleting user.",Toast.LENGTH_LONG).show();
                btnDeleteConnection.setVisibility(View.VISIBLE);
            }
        });
    }
}
