package apps.raymond.kinect;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.DialogFragments.GroupInviteAdapter;
import apps.raymond.kinect.Groups.GroupBase;

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
        return inflater.inflate(R.layout.group_invite_fragment,container,false);
    }

    List<GroupBase> groupInviteList;
    RecyclerView groupInviteRecycler;
    GroupInviteAdapter adapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupInviteRecycler = view.findViewById(R.id.group_invite_recycler);
        groupInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupInviteAdapter(this);
        groupInviteRecycler.setAdapter(adapter);
        groupInviteList = new ArrayList<>();
        fetchInvites();
    }

    private void fetchInvites(){
        // Show the progress bar animation~
        viewModel.fetchGroupInvites().addOnCompleteListener(new OnCompleteListener<List<GroupBase>>() {
            @Override
            public void onComplete(@NonNull Task<List<GroupBase>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        groupInviteList.addAll(task.getResult());
                        adapter.setData(groupInviteList);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG,"No group invites found.");
                    }
                }
            }
        });
    }

    @Override
    public void onAccept(GroupBase group) {
        Log.i(TAG,"Clicked to accept this event.");
        viewModel.addUserToGroup(group);
        groupInviteList.remove(group);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDecline() {
        Log.i(TAG,"Clicked to decline this event.");
    }

    @Override
    public void onDetail() {
    }
}
