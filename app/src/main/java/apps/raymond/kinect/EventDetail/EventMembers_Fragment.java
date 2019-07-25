package apps.raymond.kinect.EventDetail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apps.raymond.kinect.ProfileRecyclerAdapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.Profile_Activity;
import apps.raymond.kinect.ObjectModels.User_Model;

public class EventMembers_Fragment extends Fragment implements
        ProfileRecyclerAdapter.ProfileClickListener{

    public static EventMembers_Fragment newInstance(String eventName){
        EventMembers_Fragment fragment = new EventMembers_Fragment();
        Bundle args = new Bundle();
        args.putString("name",eventName);
        fragment.setArguments(args);
        return fragment;
    }

    private EventDetail_ViewModel mViewModel;
    private String mEventName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(EventDetail_ViewModel.class);
        mEventName = getArguments().getString("name");
    }

    private RecyclerView recyclerViewA;
    private TextView txtNullAccepted;
    private ProgressBar pbAccepted;
    private SearchView searchView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_members,container,false);
        recyclerViewA = view.findViewById(R.id.recycler_members_accepted);
        txtNullAccepted = view.findViewById(R.id.text_null_accepted);
        pbAccepted = view.findViewById(R.id.progress_members_loading);
        searchView = view.findViewById(R.id.search_members);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileRecyclerAdapter mAdapterA = new ProfileRecyclerAdapter(this);
        ProfileRecyclerAdapter mAdapterI = new ProfileRecyclerAdapter(this);

        recyclerViewA.setAdapter(mAdapterA);
        recyclerViewA.setLayoutManager(new LinearLayoutManager(getContext()));

        mViewModel.getEventAccepted().observe(requireActivity(),(List<User_Model> list)->{
            Log.w("MembersFragment","There was a change in the attending members list.");
            if(pbAccepted.getVisibility()==View.VISIBLE){
                pbAccepted.setVisibility(View.GONE);
            }
            if(list.size()==0){
                txtNullAccepted.setVisibility(View.VISIBLE);
            } else {
                if(txtNullAccepted.getVisibility()==View.VISIBLE){
                    txtNullAccepted.setVisibility(View.GONE);
                }
                mAdapterA.setData(list);
            }

        });
        mViewModel.getEventInvited().observe(requireActivity(),(List<User_Model> list)->{
            Log.w("MembersFragment","There was a change in the invited members list.");
            mAdapterI.setData(list);
        });

        mViewModel.loadEventMembers(mEventName);
    }

    @Override
    public void onProfileClick(User_Model user) {
        Toast.makeText(getContext(),"Clicked on profile: "+user.getEmail(),Toast.LENGTH_LONG).show();
        User_Model myUserModel = mViewModel.getUserModel().getValue();

        Intent viewProfileIntent = new Intent(getContext(), Profile_Activity.class);
        viewProfileIntent.putExtra("profile_model",user).putExtra("current_user",myUserModel);
        startActivity(viewProfileIntent);
    }

    @Override
    public void onProfileLongClick(User_Model userModel) {

    }
}
