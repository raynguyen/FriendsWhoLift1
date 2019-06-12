package apps.raymond.kinect.Events;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
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
    private List<Event_Model> mListFiltered;

    public EventsCore_Adapter(EventClickListener eventClickListener){
        this.eventClickListener = eventClickListener;
    }

    @NonNull
    @Override
    public EventsCore_Adapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_event,viewGroup,false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsCore_Adapter.EventViewHolder vh, int position) {
        final Event_Model currEvent = mListFiltered.get(position);
        if(currEvent.getLong1()!=0){
            Log.w("COREEVENTADAPER: ","long 1 = "+currEvent.getLong1() );
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
                    case "sports":
                        vh.sportsTag.setVisibility(View.VISIBLE);
                        break;
                    case "food":
                        vh.foodTag.setVisibility(View.VISIBLE);
                        break;
                    case "drinks":
                        vh.drinksTag.setVisibility(View.VISIBLE);
                        break;
                    case "movies":
                        vh.moviesTag.setVisibility(View.VISIBLE);
                        break;
                    case "chill":
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

    @Override
    public int getItemCount() {
        if(mListFull !=null){
            return mListFiltered.size();
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
        if(mListFull==null){
            mListFull = new ArrayList<>(newList);
            mListFiltered = new ArrayList<>(newList);
            notifyItemRangeChanged(0,mListFiltered.size());
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
                    return mListFull.get(oldPosition).getName()
                            .equals(newList.get(newPosition).getName());
                }

                //Disabled currently
                @Override
                public boolean areContentsTheSame(int oldPosition, int newPosition) {
                    return true;
                }
            });
            mListFull.clear();
            mListFull.addAll(newList);
            mListFiltered.clear();
            mListFiltered.addAll(newList);
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if(constraint==null || constraint.length()==0){
                    mListFiltered = mListFull;
                } else {
                    List<Event_Model> filteredList = new ArrayList<>();
                    String string = constraint.toString().toLowerCase().trim();
                    for(Event_Model event : mListFull){
                        if(event.getName().toLowerCase().trim().contains(string)){
                            filteredList.add(event);
                        }
                    }
                    mListFiltered = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = mListFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListFiltered = (ArrayList<Event_Model>) results.values;
                notifyDataSetChanged();
            }
        };
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
            locationTxt = view.findViewById(R.id.text_lookup);
            timeTxt = view.findViewById(R.id.text_time);
            sportsTag = view.findViewById(R.id.image_sport);
            foodTag = view.findViewById(R.id.image_food);
            drinksTag = view.findViewById(R.id.image_drinks);
            moviesTag = view.findViewById(R.id.image_movie);
            chillTag = view.findViewById(R.id.image_chill);
        }
    }
}
