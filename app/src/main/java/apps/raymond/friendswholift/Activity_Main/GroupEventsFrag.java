package apps.raymond.friendswholift.Activity_Main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.tasks.Continuation;
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
import apps.raymond.friendswholift.Groups.GroupBase;
import apps.raymond.friendswholift.Interfaces.EventClickListener;
import apps.raymond.friendswholift.R;

public class GroupEventsFrag extends Fragment implements EventClickListener, View.OnClickListener {
    private static final String TAG = "GroupEventsFrag";

    RecyclerView eventsRecycler;
    List<GroupEvent> myEvents;
    EventsRecyclerAdapter mAdapter;
    EventViewModel eventViewModel;
    List<String> myEventNames;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_event_frag, container,false);

        myEvents = new ArrayList<>();
        //testCreateEvent();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*eventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        eventViewModel.getEvents().addOnCompleteListener(new OnCompleteListener<List<GroupEvent>>() {
            @Override
            public void onComplete(@NonNull Task<List<GroupEvent>> task) {
                Log.i(TAG,"Completed the getEvents method.");
            }
        });*/

        ImageButton addEventBtn = view.findViewById(R.id.create_event);
        addEventBtn.setOnClickListener(this);
        eventsRecycler = view.findViewById(R.id.events_Recycler);
        mAdapter = new EventsRecyclerAdapter(myEvents, this);
        eventsRecycler.setAdapter(mAdapter);
        eventsRecycler.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
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
        final Task<List<Task<DocumentSnapshot>>> myGroupEvents;

        switch (i){
            case R.id.create_event://create_event
                //Test method to see if we can return a List of the Document names from the Events Collection **WORKS


                myGroupEvents = FirebaseFirestore.getInstance().collection("Users").document(currUser.getEmail()).collection("Events").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                     for(QueryDocumentSnapshot event:task.getResult()){
                                         myEvents.add(event.getId());
                                     }
                                     Log.i(TAG,"These are stored in the events: " + myEvents.toString());
                                }
                            }
                            //The above will get us a list of the Event names in the User's Event Collection.
                            //Use this list to retrieve the appropriate POJO document from our Groups Collection.

                            
                            //This continuation will try to collect all the GroupEvent objects from the
                        }).continueWith(new Continuation<QuerySnapshot, List<Task<DocumentSnapshot>>>() {
                            @Override
                            public List<Task<DocumentSnapshot>> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                                Log.i(TAG,"Continuing to get the GroupEvents ");
                                final List<Task<DocumentSnapshot>> myGroupEvents;
                                myGroupEvents = new ArrayList<>();
                                for(String eventName : myEvents){
                                    Log.i(TAG,"Attempting to retrieve GroupEvent POJO named: "+eventName);
                                    //myGroupEvents.add(FirebaseFirestore.getInstance().collection("Groups").document(currUser.getEmail()).collection("Events").document(eventName).get());
                                }
                                myGroupEvents.add(FirebaseFirestore.getInstance().collection("Users").document(currUser.getEmail()).get());
                                return myGroupEvents;
                            }
                        });

                /*
                // From this we can conclude that myGroupEvents returns a List of tasks to retrieve documents.
                myGroupEvents.addOnCompleteListener(new OnCompleteListener<List<Task<DocumentSnapshot>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<DocumentSnapshot>>> task) {
                        Tasks.whenAllSuccess(task.getResult()).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Object>> task) {
                                if(task.isSuccessful()){
                                    for(Object object: task.getResult()){
                                        Log.i(TAG,"Retrieved: " + object.toString());

                                    }
                                }

                            }
                        });
                    }
                });
                */



                /*
                Log.i(TAG,"Clicked on button to create new event.");
                Fragment createEventFragment = CreateEventFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.event_FrameLayout,createEventFragment)
                        .show(createEventFragment)
                        .commit();*/
                break;
            case R.id.cancel_btn: // This method shows that we are unable to continue if the Document reference does not exist.
                FirebaseFirestore.getInstance().collection("Users").document("WHACKATTACK").collection("Events").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot event:task.getResult()){
                                        Log.i(TAG,"APPARENTLY THERES A WHACKATTACK DOC?>");
                                    }
                                }
                            }
                            //This continuation will try to collect all the GroupEvent objects from the
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Unable to retrieve the WHACKATTACK DOC",e);
                    }
                });
                break;
        }
    }
}
