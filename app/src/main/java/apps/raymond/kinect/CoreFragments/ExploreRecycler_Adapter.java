package apps.raymond.kinect.CoreFragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.R;

/**
 * Adapter class designed to create views for a set of events of which the user is able to opt in
 * via the Explore_Fragment.
 */
public class ExploreRecycler_Adapter extends RecyclerView.Adapter<ExploreRecycler_Adapter.EventViewHolder> {
    private static final String TAG = "ExploreRecycler_Adapter";

    /**
     * Interface to trigger MapView animations and events when required.
     */
    public interface ExploreAdapterInterface{
        void onItemViewClick(Event_Model event, int position, View transitionView);
    }

    private ExploreAdapterInterface mAdapterInterface;
    private List<Event_Model> mDataSet;
    ExploreRecycler_Adapter(Fragment fragment){
        try{
            mAdapterInterface = (ExploreAdapterInterface) fragment;
        } catch (ClassCastException e){}
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_event_explore_condensed, viewGroup, false);
        return new EventViewHolder(view, mAdapterInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {
        if(mDataSet != null){
            eventViewHolder.onBind(mDataSet.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if(mDataSet != null){
            return mDataSet.size();
        } else {
            return 0;
        }
    }

    public void setData(List<Event_Model> newData){
        if(mDataSet == null){
            mDataSet = new ArrayList<>(newData);
        } else {
            //DIFFUTIL SHIT
        }
    }

    /**
     * ViewHolder class to which an event is bound.
     */
    static class EventViewHolder extends RecyclerView.ViewHolder{

        private ExploreAdapterInterface mAdapterInterface;
        private TextView textEventName;
        EventViewHolder(View itemView, ExploreAdapterInterface adapterInterface){
            super(itemView);
            textEventName = itemView.findViewById(R.id.text_event_name);
            mAdapterInterface = adapterInterface;
        }

        /**
         * Method responsible for populating the ViewHolder's views with the proper content.
         *
         * @param event Event_Model that will be bound to the respective ViewHolder.
         */
        void onBind(Event_Model event){
            String eventName = event.getName();
            String transitionName = "transition_name_" + getAdapterPosition();
            textEventName.setText(eventName);
            textEventName.setTransitionName(transitionName);
            itemView.setOnClickListener((View v) -> mAdapterInterface.onItemViewClick(event,
                    getAdapterPosition(), textEventName));
        }
    }
}
