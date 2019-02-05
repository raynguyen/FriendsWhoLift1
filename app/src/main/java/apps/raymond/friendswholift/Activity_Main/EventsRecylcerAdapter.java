package apps.raymond.friendswholift.Activity_Main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import apps.raymond.friendswholift.Groups.GroupEvent;
import apps.raymond.friendswholift.R;

public class EventsRecylcerAdapter extends RecyclerView.Adapter<EventsRecylcerAdapter.EventViewHolder> {
    private List<GroupEvent> eventsList;

    public EventsRecylcerAdapter(List<GroupEvent> eventsList){
        this.eventsList = eventsList;
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
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_event_frag,viewGroup,false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {
        if(eventsList!=null){
            GroupEvent currEvent = eventsList.get(i);
            eventViewHolder.eventName.setText(currEvent.getName());
            eventViewHolder.eventDesc.setText(currEvent.getDesc());
            eventViewHolder.eventMonth.setText(currEvent.getMonth());
            eventViewHolder.eventDay.setText(currEvent.getDay());
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




}
