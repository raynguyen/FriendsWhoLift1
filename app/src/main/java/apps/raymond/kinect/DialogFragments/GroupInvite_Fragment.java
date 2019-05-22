package apps.raymond.kinect.DialogFragments;

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

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Groups.Group_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class GroupInvite_Fragment extends Fragment implements Group_Invites_Fragment.InviteResponseListener {
    private static final String TAG = "Group_Invite_Fragment";

    Core_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_recycler_fragment,container,false);
    }

    TextView nullDataTxt;
    List<Group_Model> groupInvSet;
    RecyclerView groupInviteRecycler;
    Group_Invites_Fragment adapter;
    ProgressBar progressBar;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        nullDataTxt = view.findViewById(R.id.fragment_null_data_text);
        groupInviteRecycler = view.findViewById(R.id.fragment_recycler);
        groupInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Group_Invites_Fragment(this);
        groupInviteRecycler.setAdapter(adapter);
        groupInvSet = new ArrayList<>();
        fetchInvites();
    }

    private void fetchInvites(){
        /*
        viewModel.fetchGroupInvites().addOnCompleteListener(new OnCompleteListener<List<Group_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<Group_Model>> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    if(task.getResult()!=null && task.getResult().size()>0){
                        groupInvSet.addAll(task.getResult());
                        adapter.setData(groupInvSet);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    nullDataTxt.setVisibility(View.VISIBLE);
                } else {
                    Log.w(TAG,"There was an error fetching the group invites.");
                }
            }
        });*/
    }

    @Override
    public void onAccept(final Group_Model group, final int position) {
        progressBar.setVisibility(View.VISIBLE);
        /*viewModel.addUserToGroup(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    groupInvSet.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(getContext(),"There was an error!", Toast.LENGTH_SHORT).show();
                    Log.w(TAG,"ERROR: "+task.getException());
                }
            }
        });*/
        //groupInvSet.remove(group);
    }

    @Override
    public void onDecline(final Group_Model group, final int position) {
        Log.i(TAG,"calling onDeclineEventInvitation");
        /*viewModel.declineGroupInvite(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"Successfully declined invite to "+group.getName());
                    groupInvSet.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(requireContext(),"Error declining invite to "+group.getOriginalName(),Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    @Override
    public void onDetail() {
    }
}
