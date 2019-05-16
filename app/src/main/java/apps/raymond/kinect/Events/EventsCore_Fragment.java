package apps.raymond.kinect.Events;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Core_ViewModel;
import apps.raymond.kinect.UserProfile.User_Model;

//ToDo: Listen to changes in Store from the EventsExplore fragment - we don't currently update the recyclerview if we attend an event via Explore fragment.
public class EventsCore_Fragment extends Fragment implements View.OnClickListener,
        EventsCore_Adapter.EventClickListener {
    private static final String TAG = "EventsCore_Fragment";

    private EventCore_Interface interfaceCore;
    public interface EventCore_Interface {
        void exploreEvents();
        void startDetailActivity(Event_Model event);
    }

    public static EventsCore_Fragment newInstance(User_Model userModel){
        EventsCore_Fragment fragment = new EventsCore_Fragment();
        Bundle args = new Bundle();
        args.putParcelable("user",userModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            interfaceCore = (EventCore_Interface) context;
        } catch (ClassCastException e){
            Log.w(TAG,"Host activity does not implement required interface.",e);
        }
    }

    public EventsCore_Fragment(){}

    private Core_ViewModel mViewModel;
    private User_Model mUserModel;
    private String mUserID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);

        if(getArguments()!=null){
            mUserModel = getArguments().getParcelable("user");
            mUserID = mUserModel.getEmail();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_core_fragment, container,false);
    }

    private TextView nullText;
    int scrolledHeight = 0;
    private List<Event_Model> mAcceptedEvents;
    private ProgressBar progressBar;
    private EventsCore_Adapter mAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar);
        nullText = getView().findViewById(R.id.fragment_null_data_text);
        final Button exploreEventsBtn = view.findViewById(R.id.search_events_btn);
        exploreEventsBtn.setOnClickListener(this);
        final RecyclerView eventsRecycler = view.findViewById(R.id.events_Recycler);
        mAcceptedEvents = new ArrayList<>();
        mAdapter = new EventsCore_Adapter(mAcceptedEvents, this);
        eventsRecycler.setAdapter(mAdapter);
        eventsRecycler.addItemDecoration(new Margin_Decoration_RecyclerView());
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));



        eventsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledHeight = scrolledHeight + dy;
                if(scrolledHeight >= exploreEventsBtn.getHeight() && exploreEventsBtn.getVisibility()==View.VISIBLE){
                    exploreEventsBtn.setVisibility(View.GONE);
                    eventsRecycler.scrollTo(0, scrolledHeight);
                }
                if(scrolledHeight < exploreEventsBtn.getHeight()){
                    exploreEventsBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            loadEvents();
        }

        /*
         * Observe the ViewModel's LiveData fields. When a change is detected, we want to **********
         */
        mViewModel.getAcceptedEvents().observe(this, new Observer<List<Event_Model>>() {
            @Override
            public void onChanged(@Nullable List<Event_Model> event_models) {
                Log.w(TAG,"Detected a change in the accepted events from the core.");
                if(event_models!=null){
                    mAdapter.setData(event_models);

                    //The listener is currently only for when the fragment is first instantiated.
                    //We hold a copy of the initial list in this Fragment instance and add/delete
                    //from it and call the appropriate methods to the ViewModel to update the DB while
                    //synchronously updating the list here.
                    Log.w(TAG,"EventsList consists of: "+event_models.toString());
                    mAcceptedEvents = new ArrayList<>(event_models);
                    progressBar.setVisibility(View.GONE);
                    if(!mAcceptedEvents.isEmpty()){
                        nullText.setVisibility(View.GONE);
                        mAdapter.setData(mAcceptedEvents);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        nullText.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mViewModel.loadUserEvents(mUserID);
    }

    /**
     * Method call to observe a List<Event_Model> that is held by the Core_ViewModel. Changes to this
     * list triggers this fragment to update the RecyclerView through mAdapter.
     */
    private void loadEvents(){
        mViewModel.getAcceptedEvents().observe(this, new Observer<List<Event_Model>>() {
            @Override
            public void onChanged(@Nullable List<Event_Model> event_models) {
                Log.w(TAG,"There was a change in the list.");
                if(event_models!=null){

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.search_events_btn){
            if(interfaceCore !=null){
                interfaceCore.exploreEvents();
            } else {
                Toast.makeText(requireContext(),"Unable to search local events at this time!",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onEventClick(int position, Event_Model event) {
        interfaceCore.startDetailActivity(event);
    }

    public void notifyNewEvent(final Event_Model event) {
        //ToDo: We provided notifyDataAdded a positional argument because there are intentions to sort the list prior to passing to Adapter.
        mAcceptedEvents.add(event);
        /*mViewModel.attendEvent(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(),"Attending event: "+event.getName(),Toast.LENGTH_LONG).show();
            }
        });*/
        mAdapter.notifyDataAdded(event, mAcceptedEvents.size()-1);
    }

    public void filterRecycler(String constraint){
        mAdapter.getFilter().filter(constraint);
    }

}
