package apps.raymond.kinect.Events;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.EventDetail_Activity;
import apps.raymond.kinect.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Core_ViewModel;
import apps.raymond.kinect.UserProfile.User_Model;

//ToDo: Listen to changes in Store from the EventsExplore fragment - we don't currently update the recyclerview if we attend an event via Explore fragment.
public class EventsCore_Fragment extends Fragment implements EventsCore_Adapter.EventClickListener {
    public EventsCore_Fragment(){}

    private Core_ViewModel mViewModel;
    private User_Model mUserModel;
    private String mUserID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events_core, container,false);
    }

    Button btnExploreEvents;
    private TextView nullText;
    private ProgressBar progressBar;
    private EventsCore_Adapter mAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getUserModel().observe(this, new Observer<User_Model>() {
            @Override
            public void onChanged(@Nullable User_Model user_model) {
                if(user_model!=null){
                    mUserModel = user_model;
                    mUserID = mUserModel.getEmail();
                    mViewModel.loadAcceptedEvents(mUserID);
                }
            }
        });

        progressBar = view.findViewById(R.id.progress_bar);
        nullText = view.findViewById(R.id.fragment_null_data_text);

        final RecyclerView eventsRecycler = view.findViewById(R.id.events_Recycler);
        mAdapter = new EventsCore_Adapter(this);
        eventsRecycler.setAdapter(mAdapter);
        eventsRecycler.addItemDecoration(new Margin_Decoration_RecyclerView());
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //ToDo: This whole thing is wonky!
                /*scrolledHeight = scrolledHeight + dy;
                if(scrolledHeight >= exploreEventsBtn.getHeight() && exploreEventsBtn.getVisibility()==View.VISIBLE){
                    exploreEventsBtn.setVisibility(View.GONE);
                    eventsRecycler.scrollTo(0, scrolledHeight);
                }
                if(scrolledHeight < exploreEventsBtn.getHeight()){
                    exploreEventsBtn.setVisibility(View.VISIBLE);
                }*/
            }
        });

        mViewModel.getAcceptedEvents().observe(requireActivity(), (List<Event_Model> event_models)-> {
            if(event_models!=null){
                progressBar.setVisibility(View.GONE);
                if(!event_models.isEmpty()){
                    if(nullText.getVisibility()==View.VISIBLE){
                        nullText.setVisibility(View.GONE);
                    }
                    mAdapter.setData(event_models);
                } else {
                    nullText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Interface implementation to tell the parent activity to start the DetailActivity. This could
     * presumably be called here as opposed to telling the interface core to start the activity.
     * @param event The event that was clicked.
     */
    @Override
    public void onEventClick(Event_Model event) {
        Intent detailActivity = new Intent(requireActivity(),EventDetail_Activity.class);
        detailActivity.putExtra("user",mUserModel).putExtra("event",event);
        startActivity(detailActivity);
    }

    /**
     * Call from the Core_Activity whenever text is written to the Toolbar's Searchview. We cascade
     * this text to the Adapter to filter the RecyclerView's ViewHolders corresponding to the search.
     * @param constraint The filtering parameters
     */
    public void filterRecycler(String constraint){
        mAdapter.getFilter().filter(constraint);
    }

}
