package apps.raymond.friendswholift.Activity_Main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Events.CreateEventFragment;
import apps.raymond.friendswholift.Events.EventDetailFragment;
import apps.raymond.friendswholift.Events.EventViewModel;
import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.Interfaces.EventClickListener;
import apps.raymond.friendswholift.R;

public class GroupEventsFrag extends Fragment implements EventClickListener, View.OnClickListener {
    private static final String TAG = "GroupEventsFrag";

    RecyclerView eventsRecycler;
    List<GroupEvent> myEvents;
    EventsRecyclerAdapter mAdapter;
    EventViewModel eventViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_event_frag, container,false);
        eventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        myEvents = new ArrayList<>();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton addEventBtn = view.findViewById(R.id.create_event);
        addEventBtn.setOnClickListener(this);

        eventsRecycler = view.findViewById(R.id.events_Recycler);
        mAdapter = new EventsRecyclerAdapter(myEvents, this);
        eventsRecycler.setAdapter(mAdapter);
        eventsRecycler.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        updateEventCards();
    }

    @Override
    public void onStart() {
        super.onStart();
        eventViewModel.attachListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        eventViewModel.removeListener(); //Doesn't do anything yet.
    }

    /*
     * Will retrieve the User's events to populate the RecyclerView.
     * Only updates when the Fragment is first started. Need to figure out how to wrap the data in LiveData to trigger automatic updates.
     */
    private void updateEventCards(){
        Log.i(TAG,"Inside updateEventCards");
        eventViewModel.getUsersEvents().addOnCompleteListener(new OnCompleteListener<List<Task<DocumentSnapshot>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<DocumentSnapshot>>> task) {
                if(task.isSuccessful()){
                    Tasks.whenAllSuccess(task.getResult()).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            for(Object object:objects){
                                Log.i(TAG,"Adding event " +((DocumentSnapshot)object).toObject(GroupEvent.class).getName() + " to RecyclerView.");
                                myEvents.add(((DocumentSnapshot) object).toObject(GroupEvent.class));
                            }
                            mAdapter.setData(myEvents);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onEventClick(int position, GroupEvent groupEvent) {
        Log.i(TAG,"Event CardView was clicked. Position: "+position);
        //Handle Event Clicks here
        Fragment eventDetailFragment = EventDetailFragment.newInstance(groupEvent);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.event_FrameLayout,eventDetailFragment)
                //.add(eventDetailFragment,null)
                .addToBackStack(null)
                .show(eventDetailFragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        final FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        final List<String> myEvents = new ArrayList<>();
        final List<GroupEvent> groupEvents = new ArrayList<>();
        final Task<List<Task<DocumentSnapshot>>> myGroupEvents;

        switch (i){
            case R.id.create_event:
                Log.i(TAG,"Clicked on button to create new event.");
                Fragment createEventFragment = CreateEventFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.event_FrameLayout,createEventFragment)
                        .show(createEventFragment)
                        .commit();
        }
    }
}
