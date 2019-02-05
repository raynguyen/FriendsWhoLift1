package apps.raymond.friendswholift.Activity_Main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import apps.raymond.friendswholift.R;

public class EventsRecylcerAdapter extends RecyclerView.Adapter<EventsRecylcerAdapter.EventViewHolder> {


    static class EventViewHolder extends RecyclerView.ViewHolder{

        private String eventName;
        private Date eventDate;

        private EventViewHolder(View eventCardView){
            super(eventCardView);
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

    }

    @Override
    public int getItemCount() {
        return 0;
    }




}
