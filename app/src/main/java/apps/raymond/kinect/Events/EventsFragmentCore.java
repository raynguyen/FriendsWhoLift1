package apps.raymond.kinect.Events;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import apps.raymond.kinect.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;

public class EventsFragmentCore extends Fragment implements View.OnClickListener,
        EventsAdapterCore.EventClickListener{
    private static final String TAG = "EventsFragmentCore";

    private List<Event_Model> eventList;
    private ProgressBar progressBar;
    private EventsAdapterCore mAdapter;

    private SearchEvents searchEventsInterface;
    public interface SearchEvents{
        void searchEvents();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            searchEventsInterface = (SearchEvents) context;
        } catch (ClassCastException e){
            Log.w(TAG,"Host activity does not implement required interface.",e);
        }
    }

    public EventsFragmentCore(){}

    private Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class); // Is there a better callback for this?
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.core_events_frag, container,false);
    }

    private TextView nullText;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventList = new ArrayList<>();

        nullText = getView().findViewById(R.id.fragment_null_data_text);

        progressBar = view.findViewById(R.id.progress_bar);

        RecyclerView eventsRecycler = view.findViewById(R.id.events_Recycler);
        mAdapter = new EventsAdapterCore(eventList, this);
        eventsRecycler.setAdapter(mAdapter);
        eventsRecycler.addItemDecoration(new Margin_Decoration_RecyclerView());
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Button searchEventsBtn = view.findViewById(R.id.search_events_btn);
        searchEventsBtn.setOnClickListener(this);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            updateEvents();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.search_events_btn){
            if(searchEventsInterface !=null){
                searchEventsInterface.searchEvents();
            } else {
                Toast.makeText(requireContext(),"Unable to search local events at this time!",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onEventClick(int position, Event_Model groupEvent) {
        Fragment detailedEvent = Event_Detail_Fragment.newInstance(groupEvent);
        detailedEvent.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
        getFragmentManager().beginTransaction()
                .replace(R.id.core_frame,detailedEvent,Event_Detail_Fragment.TAG)
                .addToBackStack(Event_Detail_Fragment.TAG)
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
                                eventList.add(((DocumentSnapshot) object).toObject(Event_Model.class)); //Do the toObject in the repo
                            }
                            progressBar.setVisibility(View.GONE);
                            if(eventList.size() == 0){
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

    public void filterRecycler(String constraint){
        mAdapter.getFilter().filter(constraint);
    }

    public void updateEventRecycler(Event_Model event) {
        eventList.add(event);
        mAdapter.addData(event);
        if(eventList.size()==0){
            nullText.setVisibility(View.VISIBLE);
        }
    }

}
