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

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class Core_Fragment extends Fragment implements EventsNewsFeed_Adapter.EventClickListener {

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

        //Needed to determine events that are popular with friends.
        mViewModel.getUserConnections().observe(requireActivity(),(@Nullable List<User_Model> connections)->{
            //We require the user connections so that we can query public events the connections are
            //attending that exclude you.
        });

        //Needed to determine events that are popular with friends.
        mViewModel.getPublicEvents().observe(requireActivity(),(@Nullable List<Event_Model> publicEvents)->{
            //We want a list of public events so that we can check to see if the user is attending
            //the event. If not, check if
        });

        //Needed to determine events that are popular with friends.
        mViewModel.getPopularFeed().observe(requireActivity(),(@Nullable List<Event_Model> event_models)->{
            mPopularAdapter.setData(event_models);
            Log.w("CoreFragment","Got a list of event models that your friends joined: " + event_models.size());
        });

        //Needed to determine events held at locations where you have previously been to.

        //When we have a list of usere connections and public events, we can iterate through the
        mViewModel.loadNewEventsFeed();
    }

    @Override
    public void onEventClick(Event_Model event) {

    }
}
