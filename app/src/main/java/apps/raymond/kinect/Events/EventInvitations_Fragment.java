package apps.raymond.kinect.Events;

import android.arch.lifecycle.Observer;
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

import java.util.List;

import apps.raymond.kinect.ViewModels.Core_ViewModel;
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

        mViewModel.getAcceptedEvents().observe(requireActivity(), new Observer<List<Event_Model>>() {
            @Override
            public void onChanged(@Nullable List<Event_Model> event_models) {
                Log.w("EventInviteFrag","Observed change in accepted events....");
            }
        });
    }

    @Override
    public void onRespond(Event_Model event, int response) {
        Log.w("EventInviteFrag","Bruh we are in respond so we should be updating the recycler!!");
        List<Event_Model> newList = mViewModel.getEventInvitations().getValue();
        if(newList.contains(event)){
            newList.remove(event);
            mViewModel.setEventInvitations(newList); //Remove the invitation from the ViewModel set and increment attending count.
            mViewModel.deleteEventInvitation(mUserID,event.getName()); //Delete the invitation doc and decrement invited count.
        }
        if (response == EventInvitations_Adapter.ACCEPT) {
            Log.w("EventInviteFrag","Accepted event. Should notify observers of activity: "+getActivity().getClass());
            mViewModel.addUserToEvent(mUserID,mUserModel,event.getName());//Add user to event's Accepted collection and increment attending count.
            mViewModel.addEventToUser(mUserID,event);//Add the event to User's Event collection.
            List<Event_Model> acceptedEvents = mViewModel.getAcceptedEvents().getValue();
            acceptedEvents.add(event);
            mViewModel.setAcceptedEvents(acceptedEvents);
        } else if (response == EventInvitations_Adapter.DECLINE) {
            Log.w("EventInviteFrag","Declined event.");
            mViewModel.updateEventInviteDeclined(event.getName(),mUserID,mUserModel);//Add the user to event's Declined collection. NOT SURE WHAT TO DO WITH THIS DATA.
        }
    }

    @Override
    public void onEventDetail(Event_Model event) {
    }

}