package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.zip.Inflater;

import apps.raymond.kinect.Events.Event_Model;

public class EventsNewsFeed_Adapter extends RecyclerView.Adapter<EventsNewsFeed_Adapter.EventViewHolder> {
    private List<Event_Model> mDataSet;
    private EventClickListener listener;


    public interface EventClickListener{
        void onEventClick(Event_Model event);
    }

    public EventsNewsFeed_Adapter(EventClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_event,viewGroup,false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {
        Event_Model event = mDataSet.get(i);

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
            mDataSet = newList;
            notifyItemRangeChanged(0,mDataSet.size());
        } else {

        }
    }

    static class EventViewHolder extends RecyclerView.ViewHolder{
        private EventViewHolder(View view){
            super(view);
        }
    }
}
