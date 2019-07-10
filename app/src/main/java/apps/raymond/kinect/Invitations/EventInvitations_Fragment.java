package apps.raymond.kinect.Invitations;

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
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class EventInvitations_Fragment extends Fragment implements
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
        return inflater.inflate(R.layout.fragment_simple_recycler,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressBar progressBar = view.findViewById(R.id.progress_loading_recycler);
        TextView txtNullData = view.findViewById(R.id.text_null_data);
        RecyclerView eventInviteRecycler = view.findViewById(R.id.recycler_invitations);
        eventInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        EventInvitations_Adapter mAdapter = new EventInvitations_Adapter(this);
        eventInviteRecycler.setAdapter(mAdapter);

        mViewModel.getEventInvitations().observe(requireActivity(), (@Nullable List<Event_Model> events)->{
            progressBar.setVisibility(View.INVISIBLE);
            if(events!=null && events.size()>0){
                mAdapter.setData(events);
            } else {
                txtNullData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onEventDetail(Event_Model event) {
    }

    @Override
    public void onInvitationResponse(Event_Model event, int response) {
        List<Event_Model> eventInvitations = mViewModel.getEventInvitations().getValue();
        if(eventInvitations.contains(event)){
            eventInvitations.remove(event);
            mViewModel.deleteEventInvitation(mUserID,event.getName()); //Delete the invitation in both user and event collections.
            mViewModel.setEventInvitations(eventInvitations); //Remove the invitation from the ViewModel set and increment attending count.
        }
        if (response == EventInvitations_Adapter.ACCEPT) {
            event.setAttending(event.getAttending()+1);
            event.setInvited(event.getInvited()-1);
            mViewModel.addUserToEvent(event).addOnCompleteListener((Task<Void> task)->{
                Toast.makeText(getContext(),"Attending "+ event.getName(),Toast.LENGTH_LONG).show();
            });//Add user to event's Accepted collection and increment attending count.

            //Notify the core Recycler of new event.
            List<Event_Model> acceptedEvents = mViewModel.getMyEvents().getValue();
            acceptedEvents.add(event);
            mViewModel.setMyEvents(acceptedEvents);
        } else if (response == EventInvitations_Adapter.DECLINE) {
            mViewModel.updateEventInviteDeclined(event.getName(),mUserID,mUserModel);//Move the user to the declined invitation list.
        }
        mViewModel.deleteEventInvitation(mUserID,event.getName());//Remove and update all fields regarding the invitation.
    }
}
