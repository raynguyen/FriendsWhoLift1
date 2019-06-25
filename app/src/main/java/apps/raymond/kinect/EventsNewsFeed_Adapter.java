package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.Events.Event_Model;

public class EventsNewsFeed_Adapter extends RecyclerView.Adapter<EventsNewsFeed_Adapter.EventViewHolder> {
    private List<Event_Model> mDataSet;
    private EventClickListener listener;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private SimpleDateFormat timeSDF = new SimpleDateFormat("h:mm a",Locale.getDefault());

    public interface EventClickListener{
        void onEventClick(Event_Model event);
    }

    public EventsNewsFeed_Adapter(EventClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_newsfeed_event,viewGroup,false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder vh, int i) {
        Event_Model event = mDataSet.get(i);
        List<String> eventPrimes = event.getPrimes();

        vh.textName.setText(event.getName());
        Date date = new Date(event.getLong1());
        String dateString = sdf.format(date);
        String timeString = timeSDF.format(date);
        vh.textDate.setText(dateString);
        vh.textTime.setText(timeString);

        for(String prime : eventPrimes){
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

        vh.itemView.setOnClickListener((View v)->listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        if(mDataSet!=null){
            return mDataSet.size();
        } else {
            return 0;
        }
    }

    public void setData(List<Event_Model> newList){
        if(mDataSet==null){
            mDataSet = new ArrayList<>(newList);
        } else {
            mDataSet.clear();
            mDataSet.addAll(newList);
        }
        notifyItemRangeChanged(0,mDataSet.size());
    }

    static class EventViewHolder extends RecyclerView.ViewHolder{
        private TextView textName, textDate, textTime;
        private ImageView sportsTag, foodTag, drinksTag, moviesTag, chillTag, concertTag;
        private EventViewHolder(View view){
            super(view);
            textName = view.findViewById(R.id.text_name);
            textDate = view.findViewById(R.id.text_month_day);
            textTime = view.findViewById(R.id.text_time);
            sportsTag = view.findViewById(R.id.image_sport);
            foodTag = view.findViewById(R.id.image_food);
            drinksTag = view.findViewById(R.id.image_drinks);
            moviesTag = view.findViewById(R.id.image_movie);
            chillTag = view.findViewById(R.id.image_chill);
            concertTag = view.findViewById(R.id.image_concert);
        }
    }
}
