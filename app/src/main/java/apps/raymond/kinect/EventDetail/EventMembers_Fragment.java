package apps.raymond.kinect.EventDetail;

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
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.ProfileRecyclerAdapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

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
    public void onProfileClick(User_Model userModel) {
        Log.w("EventMembersFrag", "Load the fragment to display the user.");
    }
}
