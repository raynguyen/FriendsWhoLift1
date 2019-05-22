package apps.raymond.kinect;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;

public class Connections_Fragment extends Fragment implements
        ProfileRecyclerAdapter.ProfileClickListener{

    public static Connections_Fragment newInstance(String userID){
        Connections_Fragment fragment = new Connections_Fragment();
        Bundle args = new Bundle();
        args.putString("user",userID);
        fragment.setArguments(args);
        return fragment;
    }

    private Profile_ViewModel mViewModel;
    private String mUserID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Profile_ViewModel.class);
        mUserID = getArguments().getString("user");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_recycler_fragment,container,false);
    }

    RecyclerView connectionsRecycler;
    ProfileRecyclerAdapter mAdapter;
    TextView nullDataText;
    ProgressBar progressBar;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nullDataText = view.findViewById(R.id.fragment_null_data_text);
        progressBar = view.findViewById(R.id.progress_bar);

        connectionsRecycler = view.findViewById(R.id.fragment_recycler);
        connectionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new ProfileRecyclerAdapter(this);
        connectionsRecycler.setAdapter(mAdapter);

        mViewModel.getUserConnections().observe(this, new Observer<List<User_Model>>() {
            @Override
            public void onChanged(@Nullable List<User_Model> user_models) {
                Log.i("ConnectionsFragment","GOT A LLIST OF USERS!");
                if(user_models.size()==0 && nullDataText.getVisibility()==View.INVISIBLE){
                    nullDataText.setVisibility(View.VISIBLE);
                } else if(user_models.size()!=0 && nullDataText.getVisibility()==View.VISIBLE){
                    nullDataText.setVisibility(View.INVISIBLE);
                }

                mAdapter.setData(user_models);
            }
        });

        mViewModel.loadConnectionsList(mUserID);
    }

    @Override
    public void onProfileClick(User_Model userModel) {
        Toast.makeText(getContext(),"Clicked on profile: "+userModel.getEmail(),Toast.LENGTH_LONG).show();
    }
}
