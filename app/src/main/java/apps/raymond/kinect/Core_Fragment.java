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
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class Core_Fragment extends Fragment implements EventsNewsFeed_Adapter.EventClickListener {

    private Core_ViewModel mViewModel;
    private EventsNewsFeed_Adapter mNewAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
        mNewAdapter = new EventsNewsFeed_Adapter(this);
    }

    ViewFlipper viewFlipper;
    RecyclerView recyclerNewEvents;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_newsfeed,container,false);
        viewFlipper = v.findViewById(R.id.viewflipper_news);
        recyclerNewEvents = v.findViewById(R.id.recycler_new_events);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();

        recyclerNewEvents.setAdapter(mNewAdapter);
        recyclerNewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        mViewModel.getNewEventsFeed().observe(requireActivity(),(@Nullable List<Event_Model> event_models)->{
            mNewAdapter.setData(event_models);
            Log.w("CoreFragment","Got some events to load into the new events feed!");

        });
        mViewModel.loadNewEventsFeed();
    }


    @Override
    public void onEventClick(Event_Model event) {

    }
}
