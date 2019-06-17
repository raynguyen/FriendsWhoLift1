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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.Events.EventInvitations_Adapter;
import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class ViewInvitations_Fragment extends Fragment implements
        EventInvitations_Adapter.EventInvitationInterface{

    private Core_ViewModel mViewModel;
    private User_Model mUserModel;
    private String mUserID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        mUserModel = mViewModel.getUserModel().getValue();
        mUserID = mUserModel.getEmail();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invitations,container,false);
    }

    RecyclerView eventInviteRecycler;
    EventInvitations_Adapter mAdapter;
    private TextView txtNullData;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton closeBtn = view.findViewById(R.id.button_close_invitations);
        closeBtn.setOnClickListener((View v)->requireActivity().getSupportFragmentManager().popBackStack());

        ProgressBar progressBar = view.findViewById(R.id.progress_bar_invitations);
        txtNullData = view.findViewById(R.id.text_null_data_invitations);
        eventInviteRecycler = view.findViewById(R.id.recycler_invitations);
        eventInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new EventInvitations_Adapter(this);
        eventInviteRecycler.setAdapter(mAdapter);

        if(mViewModel.getEventInvitations().getValue()!=null){
            initRecycler(mViewModel.getEventInvitations().getValue());
        }
        mViewModel.getEventInvitations().observe(requireActivity(), (List<Event_Model> events)-> {
            if(progressBar.getVisibility()==View.VISIBLE){
                progressBar.setVisibility(View.GONE);
            }
            initRecycler(events);
        });
    }

    private void initRecycler(List<Event_Model> events){
        if(events.isEmpty()){
            txtNullData.setVisibility(View.VISIBLE);
        } else {
            mAdapter.setData(events);
        }
    }

    @Override
    public void onEventDetail(Event_Model event) {
    }

    @Override
    public void onRespond(Event_Model event, int response) {
        List<Event_Model> mList = mViewModel.getEventInvitations().getValue();
        if(mList.contains(event)){
            mList.remove(event);
            mViewModel.deleteEventInvitation(mUserID,event.getName()); //Delete the invitation in both user and event collections.
            mViewModel.setEventInvitations(mList); //Remove the invitation from the ViewModel set and increment attending count.
        }
        if (response == EventInvitations_Adapter.ACCEPT) {
            event.setAttending(event.getAttending()+1);
            event.setInvited(event.getInvited()-1);
            mViewModel.addUserToEvent(mUserID,mUserModel,event.getName());//Add user to event's Accepted collection and increment attending count.
            mViewModel.addEventToUser(mUserID,event);//Add the event to User's Event collection.

            //Notify the core Recycler of new event.
            List<Event_Model> acceptedEvents = mViewModel.getAcceptedEvents().getValue();
            acceptedEvents.add(event);
            mViewModel.setAcceptedEvents(acceptedEvents);
        } else if (response == EventInvitations_Adapter.DECLINE) {
            mViewModel.updateEventInviteDeclined(event.getName(),mUserID,mUserModel);//Move the user to the declined invitation list.
        }
        mViewModel.deleteEventInvitation(mUserID,event.getName());//Remove and update all fields regarding the invitation.
    }
}
