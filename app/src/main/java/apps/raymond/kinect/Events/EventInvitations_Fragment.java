package apps.raymond.kinect.Events;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.DialogFragments.EventInvitations_Adapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Core_ViewModel;

public class EventInvitations_Fragment extends EventControl_Fragment
        implements EventInvitations_Adapter.InviteResponseListener {
    private static final String TAG = "Event_Invite_Fragment";
    private TestInterface mInterface;

    private EventResponseListener eventResponseListener;
    public interface EventResponseListener{
        void eventAccepted(Event_Model event);
        void eventDeclined(Event_Model event);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mInterface = (TestInterface) context;
            eventResponseListener = (EventResponseListener) context;
        } catch (ClassCastException e){}
    }

    Core_ViewModel mViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_recycler_fragment,container,false);
    }

    TextView nullDataTxt;
    List<Event_Model> eventInvSet;
    RecyclerView eventInviteRecycler;
    EventInvitations_Adapter mAdapter;
    ProgressBar progressBar;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar);
        nullDataTxt = view.findViewById(R.id.fragment_null_data_text);
        eventInviteRecycler = view.findViewById(R.id.fragment_recycler);
        eventInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new EventInvitations_Adapter(this);
        eventInviteRecycler.setAdapter(mAdapter);
        eventInvSet = new ArrayList<>();
        fetchInvites();
    }

    private void fetchInvites(){
        //ToDo: should retrieve list of event invites from the activity and when clicking to load InvitationFragments pass the respective lists.
        //ToDo: check if the recycler loads properly. There is a chance that we don't detect a change when this fragment is created because the list received no updates.
        mViewModel.getEventInvitations().observe(this, new Observer<List<Event_Model>>() {
            @Override
            public void onChanged(@Nullable List<Event_Model> event_models) {
                if(!event_models.isEmpty()){
                    mAdapter.setData(event_models);
                } else {
                    nullDataTxt.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Interface method called from the ViewHolder when user clicks the accept button.
    @Override
    public void onAccept(final Event_Model event, final int position) {
    }

    //Interface method called from the ViewHolder when user clicks the decline button.
    @Override
    public void onDecline(Event_Model event) {
        eventResponseListener.eventDeclined(event);
    }

    @Override
    public void onDetail() {
    }

}
