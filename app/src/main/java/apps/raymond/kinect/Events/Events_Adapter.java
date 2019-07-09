package apps.raymond.kinect.Events;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.R;

public class Events_Adapter extends RecyclerView.Adapter<Events_Adapter.EventViewHolder>
    implements Filterable {
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd",Locale.getDefault());
    private SimpleDateFormat timeSDF = new SimpleDateFormat("h:mm a",Locale.getDefault());
    private EventClickListener eventClickListener;

    public interface EventClickListener {
        void onEventClick(Event_Model event);
    }

    /**
     * mCompleteSet holds the complete list of accepted events. Although this list is not used to populate
     * the recycler view, we require a complete copy so that we may present an unfiltered list to
     * the user when required.
     */
    private List<Event_Model> mCompleteSet; //Copy of the complete data set.
    private List<Event_Model> mDisplaySet; //The list to populate the recycler view.
    public Events_Adapter(EventClickListener eventClickListener){
        this.eventClickListener = eventClickListener;
    }

    @NonNull
    @Override
    public Events_Adapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_event,viewGroup,false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Events_Adapter.EventViewHolder vh, int position) {
        final Event_Model currEvent = mDisplaySet.get(position);
        if(currEvent.getLong1()!=0){
            long long1 = currEvent.getLong1();
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(long1);
            vh.txtDate.setText(sdf.format(c.getTime()));
            vh.timeTxt.setText(timeSDF.format(new Date(long1)));
            if(vh.timeTxt.getVisibility()==View.GONE){
                vh.timeTxt.setVisibility(View.VISIBLE);
            }
        } else {
            vh.txtDate.setText(R.string.date_tbd);
            vh.timeTxt.setVisibility(View.GONE);
        }
        vh.txtName.setText(currEvent.getName());
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
                    case Event_Model.MOVIE:
                        vh.moviesTag.setVisibility(View.VISIBLE);
                        break;
                    case Event_Model.CHILL:
                        vh.chillTag.setVisibility(View.VISIBLE);
                        break;
                    case Event_Model.CONCERT:
                        vh.concertTag.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

        vh.itemView.setOnClickListener((View v)-> eventClickListener.onEventClick(currEvent));
    }

    @Override
    public int getItemCount() {
        if(mDisplaySet != null){
            return mDisplaySet.size();
        } else {
            return 0;
        }
    }

    /**
     * Method to set the data for the RecyclerView. If mCompleteSet is null, this adapter has not been
     * passed data to populate the recycler view.
     *
     * DiffUtil is used to determine the changes between the existing mCompleteSet and eventsList.
     * Using the change determined by DiffUtil, we can simply request the Adapter to create/delete
     * views as required by the change in data.
     *
     * Note that notifyItemRangeChanged is preferred over notifyDataSetChanged.
     *
     * @param newList New data set to populate the RecyclerView
     */
    public void setData(final List<Event_Model> newList){
        if(newList == null) {
            return;
        }

        if(mCompleteSet == null){
            mCompleteSet = new ArrayList<>(newList);
            mDisplaySet = new ArrayList<>(newList);
            notifyItemRangeChanged(0, mDisplaySet.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mCompleteSet.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldPosition, int newPosition) {
                    return mCompleteSet.get(oldPosition).getName()
                            .equals(newList.get(newPosition).getName());
                }

                //Disabled currently
                @Override
                public boolean areContentsTheSame(int oldPosition, int newPosition) {
                    return true;
                }
            });
            mCompleteSet.clear();
            mCompleteSet.addAll(newList);
            mDisplaySet.clear();
            mDisplaySet.addAll(newList);
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if(constraint==null || constraint.length()==0){
                    mDisplaySet = mCompleteSet;
                } else {
                    List<Event_Model> filteredList = new ArrayList<>();
                    String string = constraint.toString().toLowerCase().trim();
                    for(Event_Model event : mCompleteSet){
                        if(event.getName().toLowerCase().trim().contains(string)){
                            filteredList.add(event);
                        }
                    }
                    mDisplaySet = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = mDisplaySet;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDisplaySet = (ArrayList<Event_Model>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class EventViewHolder extends RecyclerView.ViewHolder{
        private TextView txtName, txtDate, attendingTxt, invitedTxt, hostTxt, locationTxt, timeTxt;
        private ImageView sportsTag, foodTag, drinksTag, moviesTag, chillTag, concertTag;
        private EventViewHolder(View view){
            super(view);
            txtName = view.findViewById(R.id.text_event_name);
            txtDate = view.findViewById(R.id.text_month_day);
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
            concertTag = view.findViewById(R.id.image_concert);
        }
    }
}
