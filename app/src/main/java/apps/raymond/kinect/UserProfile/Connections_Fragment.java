package apps.raymond.kinect.UserProfile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import apps.raymond.kinect.ProfileRecyclerAdapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;

/**
 * Fragment that displays the suggested connections and current connections. There is a search view
 * that filters both the recycler views for users that match the constraint inputted by the user.
 */
public class Connections_Fragment extends Fragment implements
        ProfileRecyclerAdapter.ProfileClickListener {

    public static Connections_Fragment newInstance(User_Model userID){
        Connections_Fragment fragment = new Connections_Fragment();
        Bundle args = new Bundle();
        args.putParcelable("user",userID);
        fragment.setArguments(args);
        return fragment;
    }

    private Profile_ViewModel mViewModel;
    private User_Model mUserModel;
    private String mUserID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Profile_ViewModel.class);
        mUserModel = getArguments().getParcelable("user");
        mUserID = mUserModel.getEmail();
    }

    private RecyclerView recyclerConnections,recyclerSuggested;
    private TextView textNullSuggested;
    private ProgressBar pbSuggested;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_connections,container,false);
        recyclerConnections = v.findViewById(R.id.recycler_connections);
        recyclerSuggested = v.findViewById(R.id.recycler_suggest_connections);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnReturn = view.findViewById(R.id.button_return);
        btnReturn.setOnClickListener((View v)-> getFragmentManager().popBackStack());

        ProfileRecyclerAdapter mConnectionsAdapter = new ProfileRecyclerAdapter(this);
        ProfileRecyclerAdapter mSuggestedAdapter = new ProfileRecyclerAdapter(this);

        recyclerConnections.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSuggested.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerConnections.setAdapter(mConnectionsAdapter);
        recyclerSuggested.setAdapter(mSuggestedAdapter);

        TextView textNullConnections = view.findViewById(R.id.text_null_connections);
        ProgressBar pbConnections = view.findViewById(R.id.progress_connections);
        textNullSuggested = view.findViewById(R.id.text_null_suggested_connections);
        pbSuggested = view.findViewById(R.id.progress_suggested_connections);

        mViewModel.getUserConnections().observe(this, (@Nullable List<User_Model> connections)-> {
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

        mViewModel.getSuggestedConnections().observe(this,(@Nullable List<User_Model> result)->
                filterSuggestedConnections());

        mViewModel.getSuggestedFiltered().observe(this,(@Nullable List<User_Model> result) ->
                mSuggestedAdapter.setData(result));

        //ToDo: We want to wait until the connections are retrieved. Then we try and fetch at suggested
        // list of users and then filter out the already connected users.
        mViewModel.loadConnections(mUserID);
        mViewModel.loadSuggestedConnections(mUserID);

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
        List<User_Model> connections = mViewModel.getUserConnections().getValue();
        List<User_Model> suggestedUsers = mViewModel.getSuggestedConnections().getValue();
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
            mViewModel.setSuggestedFiltered(suggestedUsers);
        }
    }

    //ToDo: This should be converted to being called on LongClick, on normal click consider expanding the
    // user's card view to show more details and inflate an add connection button.
    @Override
    public void onProfileClick(User_Model user) {
        Toast.makeText(getContext(),"Clicked on profile: "+user.getEmail(),Toast.LENGTH_LONG).show();
        Intent viewProfileIntent = new Intent(getContext(), Profile_Activity.class);
        viewProfileIntent.putExtra("profile_model",user).putExtra("current_user",mUserModel);
        startActivity(viewProfileIntent);
    }
}
