package apps.raymond.kinect.EventDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_members,container,false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileRecyclerAdapter mAdapterA = new ProfileRecyclerAdapter(this);
        ProfileRecyclerAdapter mAdapterI = new ProfileRecyclerAdapter(this);

        mViewModel.getEventAccepted().observe(requireActivity(),(List<User_Model> list)->{
            Log.w("MembersFragment","There was a change in the attending members list.");
            mAdapterA.setData(list);
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
