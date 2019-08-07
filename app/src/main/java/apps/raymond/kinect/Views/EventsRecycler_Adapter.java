package apps.raymond.kinect.Views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.R;

/**
 * Adapter class that generates the item views for the RecyclerView that contains a user's accepted
 * events.
 */
public class EventsRecycler_Adapter extends RecyclerView.Adapter<EventsRecycler_Adapter.EventViewHolder>
    implements Filterable {
    private static SimpleDateFormat sdf = new SimpleDateFormat("MMM dd",Locale.getDefault());
    private static SimpleDateFormat timeSDF = new SimpleDateFormat("h:mm a",Locale.getDefault());
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
    EventsRecycler_Adapter(EventClickListener eventClickListener){
        this.eventClickListener = eventClickListener;
    }

    @NonNull
    @Override
    public EventsRecycler_Adapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_event,viewGroup,false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventsRecycler_Adapter.EventViewHolder vh, int position) {
        Event_Model event = mDisplaySet.get(position);
        vh.onBind(event);
        vh.itemView.setOnClickListener((View v)-> eventClickListener.onEventClick(event));
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
     * Method to set the data for the RecyclerView. If mCompleteSet is null, this adapter has not
     * been passed data to populate the recycler view.
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

        private void onBind(Event_Model event){
            if(event.getLong1()!=0){
                long long1 = event.getLong1();
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(long1);
                txtDate.setText(sdf.format(c.getTime()));
                timeTxt.setText(timeSDF.format(new Date(long1)));
                if(timeTxt.getVisibility()==View.GONE){
                    timeTxt.setVisibility(View.VISIBLE);
                }
            } else {
                txtDate.setText(R.string.date_tbd);
                timeTxt.setVisibility(View.GONE);
            }
            txtName.setText(event.getName());
            attendingTxt.setText(String.format(Locale.getDefault(),"%s",event.getAttending()));
            invitedTxt.setText(String.format(Locale.getDefault(),"%s",event.getInvited()));
            hostTxt.setText(event.getCreator());
            if(event.getAddress()!=null){
                locationTxt.setText(event.getAddress());
            } else {
                locationTxt.setText(R.string.location_tbd);
            }

            List<String> primes = event.getPrimes();
            if(primes!=null){
                for(String prime : primes){
                    switch (prime){
                        case Event_Model.SPORTS:
                            sportsTag.setVisibility(View.VISIBLE);
                            break;
                        case Event_Model.FOOD:
                            foodTag.setVisibility(View.VISIBLE);
                            break;
                        case Event_Model.DRINKS:
                            drinksTag.setVisibility(View.VISIBLE);
                            break;
                        case Event_Model.MOVIE:
                            moviesTag.setVisibility(View.VISIBLE);
                            break;
                        case Event_Model.CHILL:
                            chillTag.setVisibility(View.VISIBLE);
                            break;
                        case Event_Model.CONCERT:
                            concertTag.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }

        }
    }
}
