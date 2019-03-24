package apps.raymond.kinect.Events;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.R;

public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.EventViewHolder>
    implements Filterable {

    private EventClickListener eventClickListener;
    public interface EventClickListener {
        void onEventClick(int position, GroupEvent groupEvent);
    }

    private List<GroupEvent> eventsListFull; //eventsListFull should never be touched.
    private List<GroupEvent> eventsListClone;

    public EventsRecyclerAdapter(List<GroupEvent> eventsList, EventClickListener eventClickListener){
        this.eventClickListener = eventClickListener;
        this.eventsListFull = new ArrayList<>(eventsList);
        eventsListClone = eventsList;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder{
        private TextView eventName, eventDesc, eventDay, eventMonth;
        private EventViewHolder(View view){
            super(view);
            eventName = view.findViewById(R.id.event_title);
            eventDesc = view.findViewById(R.id.event_desc);
            eventDay = view.findViewById(R.id.event_day);
            eventMonth = view.findViewById(R.id.event_month);
        }
    }

    @NonNull
    @Override
    public EventsRecyclerAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_cardview,viewGroup,false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsRecyclerAdapter.EventViewHolder eventViewHolder, int position) {
        if(eventsListFull !=null){
            final GroupEvent currEvent = eventsListFull.get(position);
            eventViewHolder.eventName.setText(currEvent.getName());
            eventViewHolder.eventDesc.setText(currEvent.getDesc());
            eventViewHolder.eventMonth.setText(currEvent.getMonth());
            eventViewHolder.eventDay.setText(currEvent.getDay());

            eventViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventClickListener.onEventClick(eventViewHolder.getAdapterPosition(),currEvent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(eventsListFull !=null){
            return eventsListFull.size();
        } else {
            return 0;
        }
    }

    public void setData(List<GroupEvent> eventsList){
        this.eventsListFull = new ArrayList<>(eventsList);
        eventsListClone = eventsList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    private Filter eventFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<GroupEvent> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                Log.i("ADAPTER","Nothing should be filtered. Need to return the full list.");
                filteredList.addAll(eventsListClone);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                Log.i("Adapter","Should be filtering through the entire list of events which is currently of size = "+ eventsListFull.size());
                for(GroupEvent event : eventsListFull){
                    if(event.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(event);
                    }
                }
                Log.i("Adapter","AFter filter, we return a list of size = " + filteredList.size());
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            eventsListFull.clear();
            eventsListFull.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
