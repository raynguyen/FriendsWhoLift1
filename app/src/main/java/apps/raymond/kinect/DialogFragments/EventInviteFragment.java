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

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;

public class EventInviteFragment extends Fragment implements EventInviteAdapter.InviteResponseListener {
    private static final String TAG = "Event_Invite_Fragment";

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
    List<Event_Model> eventInvSet;
    RecyclerView eventInviteRecycler;
    EventInviteAdapter adapter;
    ProgressBar progressBar;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        nullDataTxt = view.findViewById(R.id.null_data_text);
        eventInviteRecycler = view.findViewById(R.id.invite_recycler);
        eventInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventInviteAdapter(this);
        eventInviteRecycler.setAdapter(adapter);
        eventInvSet = new ArrayList<>();
        fetchInvites();
    }

    private void fetchInvites(){
        viewModel.fetchEventInvites().addOnCompleteListener(new OnCompleteListener<List<Event_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<Event_Model>> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    if(task.getResult() !=null && task.getResult().size()>0){
                        Log.i(TAG, "fetch event Invites returned : "+ task.getResult());
                        eventInvSet.addAll(task.getResult());
                        adapter.setData(eventInvSet);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    nullDataTxt.setVisibility(View.VISIBLE);
                } else {
                    Log.w(TAG,"There was an error fetching event invites.");
                }

            }
        });
    }

    @Override
    public void onAccept(final Event_Model event, final int position) {
        //if adding user to event is successful, then we should remove from the recycler.
        progressBar.setVisibility(View.VISIBLE);
        viewModel.addUserToEvent(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    eventInvSet.remove(position);
                    adapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(getContext(),"There was an error!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDecline(Event_Model event) {
        Log.i(TAG,"Clicked to decline this event.");
    }

    @Override
    public void onDetail() {
    }
}
