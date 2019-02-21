package apps.raymond.friendswholift.Events;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.raymond.friendswholift.Interfaces.EventClickListener;
import apps.raymond.friendswholift.R;

public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.EventViewHolder> {
    private static final String TAG = "EventsRecyclerAdapter";
    private List<GroupEvent> eventsList;
    private EventClickListener eventClickListener;

    public EventsRecyclerAdapter(List<GroupEvent> eventsList, EventClickListener eventClickListener){
        this.eventsList = eventsList;
        this.eventClickListener = eventClickListener;
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
        if(eventsList!=null){
            final GroupEvent currEvent = eventsList.get(position);
            eventViewHolder.eventName.setText(currEvent.getName());
            eventViewHolder.eventDesc.setText(currEvent.getDesc());
            eventViewHolder.eventMonth.setText(currEvent.getMonth());
            eventViewHolder.eventDay.setText(currEvent.getDay());
            eventViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Event was clicked.");
                    eventClickListener.onEventClick(eventViewHolder.getAdapterPosition(),currEvent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(eventsList!=null){
            return eventsList.size();
        } else {
            return 0;
        }
    }

    public void setData(List<GroupEvent> groupEvents){
        this.eventsList = groupEvents;
        notifyDataSetChanged();
    }
}
