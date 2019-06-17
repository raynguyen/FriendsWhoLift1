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

//ToDo: Add a tab for invited/declined invitations. Allow for swipe left to decline or swipe right to accept!
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton closeBtn = view.findViewById(R.id.button_close_invitations);
        closeBtn.setOnClickListener((View v)->requireActivity().getSupportFragmentManager().popBackStack());

        ProgressBar progressBar = view.findViewById(R.id.progress_bar_invitations);
        TextView nullDataTxt = view.findViewById(R.id.text_null_data_invitations);
        eventInviteRecycler = view.findViewById(R.id.recycler_invitations);
        eventInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new EventInvitations_Adapter(this);
        mAdapter.setData(mViewModel.getEventInvitations().getValue());
        eventInviteRecycler.setAdapter(mAdapter);

        mViewModel.getAcceptedEvents().observe(requireActivity(), (List<Event_Model> events)-> {
            progressBar.setVisibility(View.GONE);
            if(events!=null && events.isEmpty()){
                nullDataTxt.setVisibility(View.VISIBLE);
            } else {
                mAdapter.setData(events);
            }
        });
        mViewModel.loadUserInvitations(mUserID);
    }

    @Override
    public void onEventDetail(Event_Model event) {

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
            mViewModel.declineEventInvitation(event.getName(),mUserID,mUserModel);//Add the user to event's Declined collection. NOT SURE WHAT TO DO WITH THIS DATA.
        }
    }
}
