package apps.raymond.friendswholift.Activity_Main;

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

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Events.EventDetailFragment;
import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.Interfaces.EventClickListener;
import apps.raymond.friendswholift.R;

public class GroupEventsFrag extends Fragment implements EventClickListener {
    private static final String TAG = "GroupEventsFrag";

    RecyclerView eventsRecycler;
    List<GroupEvent> myEvents;
    EventsRecyclerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_event_frag, container,false);

        myEvents = new ArrayList<>();
        testCreateEvent();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsRecycler = view.findViewById(R.id.events_Recycler);
        mAdapter = new EventsRecyclerAdapter(myEvents, this);
        eventsRecycler.setAdapter(mAdapter);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void testCreateEvent(){
        GroupEvent event1 = new GroupEvent("Event1","Event1 desc", "Oct","24");
        GroupEvent event2 = new GroupEvent("Event2","Event2 desc", "Jan","14");
        GroupEvent event3 = new GroupEvent("Event3","Event3 desc", "Mar","04");

        myEvents.add(event1);
        myEvents.add(event2);
        myEvents.add(event3);
        for(GroupEvent event:myEvents){
            Log.d(TAG,"Created an ArrayList of events that contain: " + event.getName());
        }
    }

    @Override
    public void onEventClick(int position, GroupEvent groupEvent) {
        Log.i(TAG,"Event CardView was clicked. Position: "+position);
        //Handle Event Clicks here
        Fragment eventDetailFragment = EventDetailFragment.newInstance(groupEvent);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.event_FrameLayout,eventDetailFragment)
                //.add(eventDetailFragment,null)
                .addToBackStack(null)
                .show(eventDetailFragment)
                .commit();
    }
}
