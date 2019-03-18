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

import apps.raymond.kinect.DialogFragments.EventInviteAdapter;
import apps.raymond.kinect.Events.GroupEvent;

public class EventInviteFragment extends Fragment implements EventInviteAdapter.InviteResponseListener {
    private static final String TAG = "Event_Invite_Fragment";

    Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG,"CREATING EVENT INVITE FRAGMENT FOR VIEWPAGER~");
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_invite_fragment,container,false);
    }

    List<GroupEvent> eventInviteList;
    RecyclerView eventInviteRecycler;
    EventInviteAdapter adapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventInviteRecycler = view.findViewById(R.id.event_invite_recycler);
        eventInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventInviteAdapter(this);
        eventInviteRecycler.setAdapter(adapter);
        eventInviteList = new ArrayList<>();
        fetchInvites();
    }

    private void fetchInvites(){
        // Show the progress bar animation~
        viewModel.fetchEventInvites().addOnCompleteListener(new OnCompleteListener<List<GroupEvent>>() {
            @Override
            public void onComplete(@NonNull Task<List<GroupEvent>> task) {
                if(task.isSuccessful()){
                    if(task.getResult() !=null){
                        Log.i(TAG, "fetchInvites returned : "+ task.getResult());
                        eventInviteList.addAll(task.getResult());
                        adapter.setData(eventInviteList);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.i(TAG,"no invites!");
                        //Display the null image and text.
                    }

                }

            }
        });
    }

    @Override
    public void onAccept(GroupEvent event) {
        Log.i(TAG,"Clicked to accept this event.");
        viewModel.addUserToEvent(event);
        eventInviteList.remove(event);
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
