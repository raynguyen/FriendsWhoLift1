package apps.raymond.kinect.CoreFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.EventDetail.EventDetail_Activity;
import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UIResources.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.ObjectModels.User_Model;
import apps.raymond.kinect.Core_ViewModel;

public class Events_Fragment extends Fragment implements EventsRecycler_Adapter.EventClickListener{
    private static final String TAG = "Events_Fragment:";
    private Core_ViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textAcceptedCount = view.findViewById(R.id.text_accepted_count);
        ProgressBar progressBar = view.findViewById(R.id.progress_events);
        TextView textNull = view.findViewById(R.id.text_events_null);
        SearchView searchView = view.findViewById(R.id.search_events);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_events);
        EventsRecycler_Adapter adapter = new EventsRecycler_Adapter(this);
        Margin_Decoration_RecyclerView dividerDecoration = new Margin_Decoration_RecyclerView(requireActivity());

        //searchIcon.setColorFilter(ContextCompat.getColor(getContext(),R.color.white));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(dividerDecoration);
        recyclerView.setAdapter(adapter);

        mViewModel.getMyEvents().observe(requireActivity(), (@Nullable List<Event_Model> events) -> {
            Log.w(TAG,"Change in my events detected.");
            progressBar.setVisibility(View.GONE);
            if(events != null){
                textAcceptedCount.setText(String.valueOf(events.size()));
                if(events.size() != 0){
                    adapter.setData(events);
                } else {
                    textNull.setVisibility(View.VISIBLE);
                }
            }
        });
        mViewModel.loadMyEvents();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //START FILTERING THE EVENTS ADAPTER FOR THE CONTENT.
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    @Override
    public void onEventClick(Event_Model event) {
        Intent detailActivity = new Intent(getContext(), EventDetail_Activity.class);
        User_Model userModel = mViewModel.getUserModel().getValue();
        detailActivity.putExtra("user",userModel).putExtra("event_name",event.getName());
        startActivity(detailActivity);
        requireActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        //Todo: shared element transition.
    }
}
