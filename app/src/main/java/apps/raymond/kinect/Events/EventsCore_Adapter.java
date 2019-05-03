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

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.R;

public class EventsCore_Adapter extends RecyclerView.Adapter<EventsCore_Adapter.EventViewHolder>
    implements Filterable {

    private EventClickListener eventClickListener;
    public interface EventClickListener {
        void onEventClick(int position, Event_Model groupEvent);
    }

    private List<Event_Model> eventsListFull;
    private List<Event_Model> eventsListClone;

    public EventsCore_Adapter(List<Event_Model> eventsList, EventClickListener eventClickListener){
        this.eventClickListener = eventClickListener;
        this.eventsListFull = new ArrayList<>(eventsList);
        eventsListClone = eventsList;
    }

    @NonNull
    @Override
    public EventsCore_Adapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_cardview,viewGroup,false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsCore_Adapter.EventViewHolder vh, int position) {
        if(eventsListFull !=null){
            final Event_Model currEvent = eventsListFull.get(position);

            if(currEvent.getLong1()!=0){
                long long1 = currEvent.getLong1();
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(long1);
                vh.monthTxt.setText(new DateFormatSymbols().getMonths()[c.get(Calendar.MONTH)]);
                vh.dayTxt.setText(String.valueOf(c.get(Calendar.DATE)));
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a",Locale.getDefault());
                vh.timeTxt.setText(sdf.format(new Date(long1)));
            }

            vh.nameTxt.setText(currEvent.getName());
            vh.attendingTxt.setText(String.format(Locale.getDefault(),"%s",currEvent.getAttending()));
            vh.invitedTxt.setText(String.format(Locale.getDefault(),"%s",currEvent.getInvited()));
            vh.hostTxt.setText(currEvent.getCreator());
            if(currEvent.getAddress()!=null){
                vh.locationTxt.setText(currEvent.getAddress());
            } else {
                vh.locationTxt.setText(R.string.location_tbd);
            }

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

    static class EventViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt, dayTxt, monthTxt, attendingTxt, invitedTxt, hostTxt, locationTxt, timeTxt;
        private ImageView sportsTag, foodTag, drinksTag, moviesTag, chillTag;
        private EventViewHolder(View view){
            super(view);
            nameTxt = view.findViewById(R.id.text_name);
            dayTxt = view.findViewById(R.id.text_day);
            monthTxt = view.findViewById(R.id.text_month);
            attendingTxt = view.findViewById(R.id.text_attending);
            invitedTxt = view.findViewById(R.id.text_invited);
            hostTxt = view.findViewById(R.id.text_host);
            locationTxt = view.findViewById(R.id.text_location);
            timeTxt = view.findViewById(R.id.text_time);
            sportsTag = view.findViewById(R.id.image_sport);
            foodTag = view.findViewById(R.id.image_food);
            drinksTag = view.findViewById(R.id.image_drinks);
            moviesTag = view.findViewById(R.id.image_movie);
            chillTag = view.findViewById(R.id.image_chill);
        }
    }
}
