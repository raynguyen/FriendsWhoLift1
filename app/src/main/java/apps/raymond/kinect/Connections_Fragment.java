package apps.raymond.kinect;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import apps.raymond.kinect.UserProfile.Profile_Activity;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;

public class Connections_Fragment extends Fragment implements
        ProfileRecyclerAdapter.ProfileClickListener{

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
        mUserID =mUserModel.getEmail();
    }

    private RecyclerView recyclerConnections,recyclerSuggested;
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
        TextView textNullSuggested = view.findViewById(R.id.text_null_suggested_connections);
        ProgressBar pbConnections = view.findViewById(R.id.progress_connections);
        ProgressBar pbSuggested = view.findViewById(R.id.progress_suggested_connections);

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
        });

        mViewModel.getSuggestedConnections().observe(this,(@Nullable List<User_Model> result)->{
            if(result!=null){
                if(pbSuggested.getVisibility()==View.VISIBLE){
                    pbSuggested.setVisibility(View.INVISIBLE);
                }
                if(result.size()==0){
                    textNullSuggested.setVisibility(View.VISIBLE);
                } else {
                    textNullSuggested.setVisibility(View.INVISIBLE);
                }
                mSuggestedAdapter.setData(result);
            }
        });
        //ToDo: We want to wait until the connections are retrieved. Then we try and fetch at suggested
        // list of users and then filter out the already connected users.
        mViewModel.loadConnections(mUserID);
        mViewModel.loadSuggestedConnections(mUserID);
    }

    @Override
    public void onProfileClick(User_Model profileModel) {
        Toast.makeText(getContext(),"Clicked on profile: "+profileModel.getEmail(),Toast.LENGTH_LONG).show();
        Intent viewProfileIntent = new Intent(getContext(), Profile_Activity.class);
        viewProfileIntent.putExtra("profilemodel",profileModel).putExtra("user",mUserModel);
        startActivity(viewProfileIntent);
    }
}
