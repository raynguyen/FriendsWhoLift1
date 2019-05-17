package apps.raymond.kinect.Events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
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
        void onEventClick(Event_Model groupEvent);
    }

    private List<Event_Model> mListFull;
    private List<Event_Model> mListClone;

    public EventsCore_Adapter(EventClickListener eventClickListener){
        this.eventClickListener = eventClickListener;
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
        if(mListFull !=null){
            final Event_Model currEvent = mListFull.get(position);
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
                    eventClickListener.onEventClick(currEvent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mListFull !=null){
            return mListFull.size();
        } else {
            return 0;
        }
    }

    /**
     * Method to set the data for the RecyclerView. If mListFull is null, this adapter has not been
     * passed data to populate the recycler view.
     *
     * DiffUtil is used to determine the changes between the existing mListFull and eventsList.
     * Using the change determined by DiffUtil, we can simply request the Adapter to create/delete
     * views as required by the change in data.
     *
     * Note that notifyItemRangeChanged is preferred over notifyDataSetChanged.
     *
     * @param newList New data set to populate the RecyclerView
     */
    public void setData(final List<Event_Model> newList){
        if(mListFull ==null){
            mListFull = new ArrayList<>(newList);
            mListClone = mListFull;
            notifyItemRangeChanged(0,newList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mListFull.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldPosition, int newPosition) {
                    return mListFull.get(oldPosition).getOriginalName()
                            .equals(newList.get(newPosition).getOriginalName());
                }

                //Disabled currently
                @Override
                public boolean areContentsTheSame(int oldPosition, int newPosition) {
                    return true;
                }
            });
            mListFull.clear();
            mListFull.addAll(newList);
            mListClone.clear();
            mListClone.addAll(newList);
            result.dispatchUpdatesTo(this);
        }
    }

    //ToDo: The search functionality no longer works, we are unable to clear the filtered list after search.
    private Filter eventFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event_Model> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(mListClone);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Event_Model event : mListClone){
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
            mListFull.clear();
            mListFull.addAll((List) results.values);
        }
    };

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

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
