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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Groups.GroupBase;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;

public class GroupInviteFragment extends Fragment implements GroupInviteAdapter.InviteResponseListener {
    private static final String TAG = "Group_Invite_Fragment";

    Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_invites_fragment,container,false);
    }

    TextView nullDataTxt;
    List<GroupBase> groupInvSet;
    RecyclerView groupInviteRecycler;
    GroupInviteAdapter adapter;
    ProgressBar progressBar;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        nullDataTxt = view.findViewById(R.id.null_data_text);
        groupInviteRecycler = view.findViewById(R.id.invite_recycler);
        groupInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupInviteAdapter(this);
        groupInviteRecycler.setAdapter(adapter);
        groupInvSet = new ArrayList<>();
        fetchInvites();
    }

    private void fetchInvites(){
        viewModel.fetchGroupInvites().addOnCompleteListener(new OnCompleteListener<List<GroupBase>>() {
            @Override
            public void onComplete(@NonNull Task<List<GroupBase>> task) {
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
        });
    }

    @Override
    public void onAccept(final GroupBase group, final int position) {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.addUserToGroup(group).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        });
        //groupInvSet.remove(group);
    }

    @Override
    public void onDecline(final GroupBase group, final int position) {
        Log.i(TAG,"calling onDecline");
        viewModel.declineGroupInvite(group).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        });
    }

    @Override
    public void onDetail() {
    }
}