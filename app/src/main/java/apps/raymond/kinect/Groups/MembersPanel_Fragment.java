package apps.raymond.kinect.Groups;

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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.ProfileRecyclerAdapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Core_ViewModel;
import apps.raymond.kinect.UserProfile.User_Model;

public class MembersPanel_Fragment extends Fragment implements
        ProfileRecyclerAdapter.ProfileClickListener {
    private static final String TAG = "Custom_Members_Panel";
    private static final String GROUP_PARCEL = "GroupParcel";

    public static MembersPanel_Fragment newInstance(Event_Model event){
        MembersPanel_Fragment membersPanel = new MembersPanel_Fragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        membersPanel.setArguments(args);
        return membersPanel;
    }

    Core_ViewModel viewModel;
    List<User_Model> membersList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // = getArguments().getParcelable(GROUP_PARCEL);
        viewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
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
    TextView membersCountTxt;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        membersCountTxt = view.findViewById(R.id.members_txt);

        membersRecycler = view.findViewById(R.id.members_recycler);
        pAdapter = new ProfileRecyclerAdapter(this);
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
    public void onProfileClick(User_Model user) {
        Log.i(TAG,"Need to implement user inflation for: "+user.getEmail());
    }

    private void fetchMembersList(){
        /*viewModel.fetchGroupMembers(group).addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<User_Model>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        membersList.addAll(task.getResult());
                        String membersCount = membersList.size() + " MEMBERS";
                        membersCountTxt.setText(membersCount);
                        pAdapter.setData(membersList);
                        pAdapter.notifyDataSetChanged();
                    }
                }
            }
        });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"DESTROYING MEMBER CABINET");
    }
}
