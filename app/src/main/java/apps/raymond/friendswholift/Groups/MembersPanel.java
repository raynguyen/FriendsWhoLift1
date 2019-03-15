package apps.raymond.friendswholift.Groups;

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
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Interfaces.ProfileClickListener;
import apps.raymond.friendswholift.ProfileRecyclerAdapter;
import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.Repository_ViewModel;
import apps.raymond.friendswholift.UserProfile.UserModel;

public class MembersPanel extends Fragment implements ProfileClickListener {
    private static final String TAG = "Custom_Members_Panel";
    private static final String GROUP_PARCEL = "GroupParcel";

    public static MembersPanel newInstance(GroupBase group){
        MembersPanel membersPanel = new MembersPanel();
        Bundle args = new Bundle();
        args.putParcelable(GROUP_PARCEL, group);
        membersPanel.setArguments(args);

        return membersPanel;
    }

    Repository_ViewModel viewModel;
    GroupBase group;
    List<UserModel> membersList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = getArguments().getParcelable(GROUP_PARCEL);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
        membersList = new ArrayList<>();
        fetchMembersList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.members_side_fragment,container,false);
    }

    RecyclerView membersRecycler;
    ProfileRecyclerAdapter pAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        membersRecycler = view.findViewById(R.id.members_recycler);
        pAdapter = new ProfileRecyclerAdapter(membersList, this);
        membersRecycler.setAdapter(pAdapter);
        membersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageButton closeDrawer = view.findViewById(R.id.close_members_drawer_btn);
        closeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"FRAGMENTS:" + getFragmentManager().getFragments().toString());
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onProfileClick(UserModel user) {
        Log.i(TAG,"Need to implement user inflation for: "+user.getEmail());
    }

    private void fetchMembersList(){
        Log.i(TAG,"Fetching members of " + group.getOriginalName());
        viewModel.fetchGroupMembers(group).addOnCompleteListener(new OnCompleteListener<List<UserModel>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserModel>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        membersList.addAll(task.getResult());
                        pAdapter.setData(membersList);
                        pAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"DESTROYING MEMBER CABINET");
    }
}
