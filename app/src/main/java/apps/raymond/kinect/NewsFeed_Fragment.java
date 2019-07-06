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
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class NewsFeed_Fragment extends Fragment implements EventsNewsFeed_Adapter.EventClickListener {
    private Core_ViewModel mViewModel;
    private EventsNewsFeed_Adapter mNewAdapter, mPopularAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        mNewAdapter = new EventsNewsFeed_Adapter(this);
        mPopularAdapter = new EventsNewsFeed_Adapter(this);
    }

    ViewFlipper viewFlipper;
    RecyclerView recyclerNewEvents, recyclerPopularFriends;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_newsfeed,container,false);
        viewFlipper = v.findViewById(R.id.viewflipper_news);
        recyclerNewEvents = v.findViewById(R.id.recycler_new_events);
        recyclerPopularFriends = v.findViewById(R.id.recycler_events_friends);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();

        recyclerNewEvents.setAdapter(mNewAdapter);
        recyclerNewEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPopularFriends.setAdapter(mPopularAdapter);
        recyclerPopularFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        //Needed to show new events in area.
        mViewModel.getNewEventsFeed().observe(requireActivity(),(@Nullable List<Event_Model> event_models)->{
            mNewAdapter.setData(event_models);
            Log.w("CoreFragment","Got some events to load into the new events feed! " + event_models.size());
        });
        mViewModel.loadNewEventsFeed();

        //Needed to determine events that are popular with friends.
        mViewModel.getUserConnections().observe(requireActivity(),(@Nullable List<User_Model> connections)->{
            //We require the user connections so that we can query public events the connections are
            //attending that exclude you.
            filterPopularEvents();
        });


        //Needed to determine events that are popular with friends.
        mViewModel.getPublicEvents().observe(requireActivity(),(@Nullable List<Event_Model> publicEvents)->{
            //We want a list of public events so that we can check to see if the user is attending
            //the event. If not, check if
            filterPopularEvents();
        });

        //Needed to determine events that are popular with friends.
        mViewModel.getPopularFeed().observe(requireActivity(),(@Nullable List<Event_Model> event_models)->{
            mPopularAdapter.setData(event_models);
        });

        //Needed to determine events held at locations where you have previously been to.
        /// STUFF HERE

    }

    //ToDo: Public events need to be filtered to exlude all events held by the AcceptedEvents.
    //ToDo: There is no guarantee that AcceptedEvents is not null.
    /**
     * Method to filter out from the public events,
     */
    private void filterPopularEvents(){
        List<Event_Model> publicEvents = mViewModel.getPublicEvents().getValue();
        List<User_Model> userConnections = mViewModel.getUserConnections().getValue();
        List<Event_Model> acceptedEvents = mViewModel.getAcceptedEvents().getValue();
        List<Event_Model> filteredEvents = new ArrayList<>(); //List containing events after filtering for public without user and with connections.
        if(publicEvents!=null && userConnections!=null){
            Log.w("CoreFragment","Size of public events before filter = "+publicEvents.size());
            //Filter all public events for events the user has not enrolled.
            if(acceptedEvents!=null){
                List<Event_Model> tempList = new ArrayList<>();
                for(Event_Model event : publicEvents){
                    if(acceptedEvents.contains(event)){
                        tempList.add(event);
                    }
                }
                publicEvents.removeAll(tempList);
            }
            //For each connection, check each event in the filtered public events to see if there is a connected user attending the event.
            for(User_Model connection : userConnections){
                for(Event_Model event : publicEvents){
                    mViewModel.checkForUser(connection.getEmail(),event.getName())
                        .addOnCompleteListener((@NonNull Task<Boolean> task)->{
                            if(task.getResult()!=null && task.getResult()){
                                filteredEvents.add(event);
                                mViewModel.getPopularFeed().postValue(filteredEvents);
                            }
                        });
                }
            }
            //Todo: Because the check is asynchronous, we have to use the filteredEvents as a mutable live data
            // and continuously update the livedata as the tasks are being completed.
        }
    }

    @Override
    public void onEventClick(Event_Model event) {
        Log.w("CoreFragment","Clicked on event in news feed: " +event.getName());
    }
}
