package apps.raymond.kinect.Events;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.R;

public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.EventViewHolder>
    implements Filterable {

    private EventClickListener eventClickListener;
    public interface EventClickListener {
        void onEventClick(int position, Event_Model groupEvent);
    }

    private List<Event_Model> eventsListFull; //eventsListFull should never be touched.
    private List<Event_Model> eventsListClone;

    public EventsRecyclerAdapter(List<Event_Model> eventsList, EventClickListener eventClickListener){
        this.eventClickListener = eventClickListener;
        this.eventsListFull = new ArrayList<>(eventsList);
        eventsListClone = eventsList;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder{
        private TextView eventName, eventDay, eventMonth, attendingCount, invitedCount, creator, location;
        private ImageView sportsTag, foodTag, drinksTag, moviesTag, chillTag;
        private EventViewHolder(View view){
            super(view);
            eventName = view.findViewById(R.id.event_title);
            eventDay = view.findViewById(R.id.event_day);
            eventMonth = view.findViewById(R.id.event_month);
            attendingCount = view.findViewById(R.id.attending_count);
            invitedCount = view.findViewById(R.id.invited_count);
            creator = view.findViewById(R.id.created_by_txt);
            location = view.findViewById(R.id.location_txt);
            sportsTag = view.findViewById(R.id.sport_tag);
            foodTag = view.findViewById(R.id.food_tag);
            drinksTag = view.findViewById(R.id.drinks_tag);
            moviesTag = view.findViewById(R.id.movie_tag);
            chillTag = view.findViewById(R.id.chill_tag);
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
    public void onBindViewHolder(@NonNull final EventsRecyclerAdapter.EventViewHolder vh, int position) {
        if(eventsListFull !=null){
            final Event_Model currEvent = eventsListFull.get(position);
            vh.eventName.setText(currEvent.getName());
            vh.eventMonth.setText(currEvent.getMonth1());
            vh.eventDay.setText(currEvent.getDay1());
            vh.attendingCount.setText(String.format(Locale.getDefault(),"%s",currEvent.getAttending()));
            vh.invitedCount.setText(String.format(Locale.getDefault(),"%s",currEvent.getInvited()));
            vh.creator.setText(currEvent.getCreator());

            List<String> primes = currEvent.getPrimes();
            if(primes!=null){
                for(String prime : primes){
                    switch (prime){
                        case Event_Model.SPORTS:
                            vh.sportsTag.setVisibility(View.VISIBLE);
                            break;
                        case Event_Model.FOOD:
                            vh.foodTag.setVisibility(View.VISIBLE);
                            break;
                        case Event_Model.DRINKS:
                            vh.drinksTag.setVisibility(View.VISIBLE);
                            break;
                        case Event_Model.MOVIES:
                            vh.moviesTag.setVisibility(View.VISIBLE);
                            break;
                        case Event_Model.CHILL:
                            vh.chillTag.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventClickListener.onEventClick(vh.getAdapterPosition(),currEvent);
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

    public void setData(List<Event_Model> eventsList){
        this.eventsListFull = new ArrayList<>(eventsList);
        eventsListClone = eventsList;
        notifyDataSetChanged();
    }

    public void addData(Event_Model event){
        eventsListFull.add(event);
        notifyItemInserted(eventsListFull.size() - 1);
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    private Filter eventFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.i("Adapter","Filtering recycler view.");
            List<Event_Model> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(eventsListClone);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Event_Model event : eventsListClone){
                    if(event.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(event);
                    }
                }
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
