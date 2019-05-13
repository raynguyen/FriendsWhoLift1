package apps.raymond.kinect.Events;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import apps.raymond.kinect.DialogFragments.EventInvitations_Adapter;
import apps.raymond.kinect.R;

public class EventInvitations_Fragment extends EventControl_Fragment
        implements EventInvitations_Adapter.InviteResponseListener {
    private static final String TAG = "Event_Invite_Fragment";
    private static final String DATASET = "DataSet";
    private EventControlInterface mInterface;

    public static EventInvitations_Fragment newInstance(ArrayList<Event_Model> eventInvitations){
        EventInvitations_Fragment fragment = new EventInvitations_Fragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(DATASET,eventInvitations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mInterface = (EventControlInterface) context;
        } catch (ClassCastException e){}
    }

    private ArrayList<Event_Model> mEventInvitationSet;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            mEventInvitationSet = getArguments().getParcelableArrayList(DATASET);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_recycler_fragment,container,false);
    }

    TextView nullDataTxt;

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
        mAdapter.setData(mEventInvitationSet);
        eventInviteRecycler.setAdapter(mAdapter);

        if(mEventInvitationSet.isEmpty()){
            nullDataTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAcceptEventInvitation(final Event_Model event) {
        mInterface.onAttendEvent(event, EventControl_Fragment.INVITATION);
    }

    @Override
    public void onDeclineEventInvitation(Event_Model event) {
    }

    @Override
    public void onEventDetail(Event_Model event) {
    }

}