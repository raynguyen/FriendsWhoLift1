package apps.raymond.kinect.UserProfile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apps.raymond.kinect.ObjectModels.User_Model;
import apps.raymond.kinect.ProfileRecyclerAdapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;
import apps.raymond.kinect.ViewModels.ProfileFragment_ViewModel;

/**
 * Fragment that displays the suggested connections and current connections. There is a search view
 * that filters both the recycler views for users that match the constraint inputted by the user.
 */
public class ProfileConnections_Fragment extends Fragment implements
        ProfileRecyclerAdapter.ProfileClickListener {

    private Profile_ViewModel mActViewModel;
    private ProfileFragment_ViewModel mFragViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActViewModel = ViewModelProviders.of(requireActivity()).get(Profile_ViewModel.class);
        if(getParentFragment()!=null){
            mFragViewModel = ViewModelProviders.of(getParentFragment()).get(ProfileFragment_ViewModel.class);
        }
    }

    private TextView textNullSuggested;
    private ProgressBar pbSuggested;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_connections,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnReturn = view.findViewById(R.id.button_return);
        btnReturn.setOnClickListener((View v)-> getFragmentManager().popBackStack());

        RecyclerView recyclerConnections = view.findViewById(R.id.recycler_connections);
        RecyclerView recyclerSuggested = view.findViewById(R.id.recycler_suggest_connections);

        ProfileRecyclerAdapter mConnectionsAdapter = new ProfileRecyclerAdapter(this);
        ProfileRecyclerAdapter mSuggestedAdapter = new ProfileRecyclerAdapter(this);

        recyclerConnections.setAdapter(mConnectionsAdapter);
        recyclerSuggested.setAdapter(mSuggestedAdapter);

        TextView textNullConnections = view.findViewById(R.id.text_null_connections);
        ProgressBar pbConnections = view.findViewById(R.id.progress_events);
        textNullSuggested = view.findViewById(R.id.text_null_suggested_connections);
        pbSuggested = view.findViewById(R.id.progress_suggested_connections);

        mActViewModel.getUserConnections().observe(this, (@Nullable List<User_Model> connections)-> {
            if(connections!=null){
                if(pbConnections.getVisibility()==View.VISIBLE){
                    pbConnections.setVisibility(View.INVISIBLE);
                }
                if(connections.size()==0){
                    textNullConnections.setVisibility(View.VISIBLE);
                } else {
                    textNullConnections.setVisibility(View.INVISIBLE);
                }
                mConnectionsAdapter.setData(connections);
            }
            filterSuggestedConnections();
        });

        mActViewModel.getSuggestedConnections().observe(this,(@Nullable List<User_Model> result)->
                filterSuggestedConnections());

        mActViewModel.getSuggestedFiltered().observe(this,(@Nullable List<User_Model> result) ->
                mSuggestedAdapter.setData(result));

        //ToDo: We want to wait until the connections are retrieved. Then we try and fetch at suggested
        // list of users and then filter out the already connected users.
        if(getParentFragment() instanceof PersonalProfile_Fragment){
            Log.w("ProfileConnectionsFrag","Personal Profile Fragment!");
            User_Model userModel = mActViewModel.getUserModel().getValue();
            String userID = userModel.getEmail();
            mActViewModel.loadConnections(userID);
            mActViewModel.loadSuggestedConnections(userID);
        } else if(getParentFragment() instanceof Profile_Fragment){
            Log.w("ProfileConnectionsFrag","ProfileFragment!");
            User_Model profileModel = mFragViewModel.getProfileModel().getValue();
            String profileID = profileModel.getEmail();
            mActViewModel.loadConnections(profileID);
            mActViewModel.loadSuggestedConnections(profileID);
        }

        SearchView searchView = view.findViewById(R.id.search_view_connections);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mConnectionsAdapter.getFilter().filter(newText);
                mSuggestedAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    /**
     * This method will filter the list of the user's connections to the list of suggested users. If
     * there are any users that appear in both lists, we remove the user from the suggested list.
     * Once we are done iterating through te user's connections, we set the data to the appropriate
     * adapter and populate the recycler view.
     *
     * Note that the User_Model%class overrides its #equals method. We compare two User_Model objects
     * via the User_Model.getEmail() function currently.
     */
    private void filterSuggestedConnections(){
        List<User_Model> connections = mActViewModel.getUserConnections().getValue();
        List<User_Model> suggestedUsers = mActViewModel.getSuggestedConnections().getValue();
        if(connections!=null && suggestedUsers!=null){
            for(User_Model user : connections){
                suggestedUsers.remove(user);
            }
            if(pbSuggested.getVisibility()==View.VISIBLE){
                pbSuggested.setVisibility(View.INVISIBLE);
            }
            if(suggestedUsers.size()==0){
                textNullSuggested.setVisibility(View.VISIBLE);
            } else {
                textNullSuggested.setVisibility(View.INVISIBLE);
            }
            mActViewModel.setSuggestedFiltered(suggestedUsers);
        }
    }

    //ToDo: This should be converted to being called on LongClick, on normal click consider expanding the
    // user's card view to show more details and inflate an add connection button.
    @Override
    public void onProfileClick(User_Model user) {
        Log.w("ConnFragment","should inflate some floating buttons to add user or send message.");
    }

    @Override
    public void onProfileLongClick(User_Model user) {
        Toast.makeText(getContext(),"Clicked on profile: "+user.getEmail(),Toast.LENGTH_LONG).show();
        //ToDo: this will now be a fragment instantiation and not stacked activities.
        /*Intent viewProfileIntent = new Intent(getContext(), Profile_Activity.class);
        //viewProfileIntent.putExtra("profile_model",user).putExtra("current_user",mUserModel);
        startActivity(viewProfileIntent);*/
    }
}
