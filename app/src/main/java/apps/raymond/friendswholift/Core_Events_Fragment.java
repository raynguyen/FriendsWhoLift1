package apps.raymond.friendswholift;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Events.Event_Detail_Fragment;
import apps.raymond.friendswholift.Events.EventsRecyclerAdapter;
import apps.raymond.friendswholift.Events.Event_Create_Fragment;
import apps.raymond.friendswholift.Events.GroupEvent;

public class Core_Events_Fragment extends Fragment implements
        EventsRecyclerAdapter.EventClickListener,View.OnClickListener, Core_Activity.UpdateEventRecycler {
    private static final String TAG = "Core_EventsFragment";


    private List<GroupEvent> eventList;
    private ProgressBar progressBar;
    private EventsRecyclerAdapter mAdapter;

    public Core_Events_Fragment(){}

    private Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this.getActivity()).get(Repository_ViewModel.class); // Is there a better callback for this?
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.core_events_frag, container,false);
    }

    private ImageView nullImage;
    private TextView nullText;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventList = new ArrayList<>();

        nullImage = getView().findViewById(R.id.null_data_image);
        nullText = getView().findViewById(R.id.null_data_text);

        ImageButton addEventBtn = view.findViewById(R.id.create_event_btn);
        addEventBtn.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progress_bar);

        RecyclerView eventsRecycler = view.findViewById(R.id.events_Recycler);
        mAdapter = new EventsRecyclerAdapter(eventList, this);
        eventsRecycler.setAdapter(mAdapter);
        eventsRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Button searchEventsBtn = view.findViewById(R.id.search_events_btn);
        searchEventsBtn.setOnClickListener(this);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            updateEvents();
        }
    }


    @Override
    public void updateEventRecycler(GroupEvent groupEvent) {
        eventList.add(groupEvent);
        nullImage.setVisibility(View.INVISIBLE);
        nullText.setVisibility(View.INVISIBLE);
        mAdapter.notifyItemInserted(eventList.size()-1);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.create_event_btn:
                Log.i(TAG,"Clicked on button to create new event.");
                Fragment createEventFragment = Event_Create_Fragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.core_frame,createEventFragment,Event_Create_Fragment.TAG)
                        .addToBackStack(Event_Create_Fragment.TAG)
                        .show(createEventFragment)
                        .commit();
                break;
            case R.id.search_events_btn:
                Log.i(TAG,"Clicked on search events Btn.");
                Toast.makeText(getContext(),"Search for new events.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEventClick(int position, GroupEvent groupEvent) {
        Fragment detailedEvent = Event_Detail_Fragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("EventObject",groupEvent);

        detailedEvent.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.core_frame,detailedEvent,Event_Detail_Fragment.TAG)
                .addToBackStack(Event_Detail_Fragment.TAG)
                .show(detailedEvent)
                .commit();
    }

    /*
     * Repository calls should provide a clean API from the rest of the application from the underlying data.
     * Clearly, requiring an onComplete listener here voids the previous statement so we will have to
     * revisit this.
     */
    private void updateEvents(){
        viewModel.getUsersEvents().addOnCompleteListener(new OnCompleteListener<List<Task<DocumentSnapshot>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<DocumentSnapshot>>> task) {
                if(task.isSuccessful()){
                    Tasks.whenAllSuccess(task.getResult()).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            for(Object object:objects){
                                Log.i(TAG,"Adding event " +((DocumentSnapshot)object).toObject(GroupEvent.class).getName() + " to RecyclerView.");
                                eventList.add(((DocumentSnapshot) object).toObject(GroupEvent.class)); //Do the toObject in the repo
                            }
                            progressBar.setVisibility(View.GONE);
                            if(eventList.size() == 0){
                                // If there are no upcoming events, we want to fill the fragment with a text that says No Upcoming Events.
                                nullImage.setVisibility(View.VISIBLE);
                                nullText.setVisibility(View.VISIBLE);
                            } else {
                                mAdapter.setData(eventList);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }


}
