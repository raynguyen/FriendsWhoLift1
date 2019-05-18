package apps.raymond.kinect.Events;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.Core_ViewModel;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventInvitations_Fragment extends Fragment
        implements EventInvitations_Adapter.EventInvitationInterface {

    private List<Event_Model> mEventInvitations;
    private Core_ViewModel mViewModel;
    private User_Model mUserModel;
    private String mUserID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        mUserModel = mViewModel.getUserModel().getValue();
        mUserID = mUserModel.getEmail();
        mEventInvitations = mViewModel.getEventInvitations().getValue();
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
        mAdapter.setData(mEventInvitations);
        eventInviteRecycler.setAdapter(mAdapter);

        if(mEventInvitations.isEmpty()){
            nullDataTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRespond(Event_Model event, int response) {
        List<Event_Model> newList = mViewModel.getEventInvitations().getValue();
        //Delete the event invitation from DB and ViewModel set.
        if(newList.contains(event)){
            newList.remove(event);
            mViewModel.setEventInvitations(newList); //Remove the invitation from the ViewModel set and increment attending count.
            mViewModel.deleteEventInvitation(mUserID,event.getOriginalName()); //Delete the invitation doc and decrement invited count.
        }
        if (response == EventInvitations_Adapter.ACCEPT) {
            mViewModel.addUserToEvent(mUserID,mUserModel,event.getOriginalName());//Add user to event's Accepted collection and increment attending count.
            mViewModel.addEventToUser(mUserID,event);//Add the event to User's Event collection.
        } else if (response == EventInvitations_Adapter.DECLINE) {
            mViewModel.declineEventInvitation(event.getOriginalName(),mUserID,mUserModel);//Add the user to event's Declined collection. NOT SURE WHAT TO DO WITH THIS DATA.
        }
    }

    @Override
    public void onEventDetail(Event_Model event) {
    }

}