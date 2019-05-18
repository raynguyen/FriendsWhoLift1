package apps.raymond.kinect.Events;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.R;

/**
 * Adapter class responsible for inflating the views for the event invitations recycler view.
 */
public class EventInvitations_Adapter extends
        RecyclerView.Adapter<EventInvitations_Adapter.InviteMessagesViewHolder> {
    public static final int DECLINE = 0;
    public static final int ACCEPT = 1;
    private List<Event_Model> eventInviteSet;
    private EventInvitationInterface callback;

    public interface EventInvitationInterface {
        void onEventDetail(Event_Model event);
        void onRespond(Event_Model event, int response);
    }

    public EventInvitations_Adapter(EventInvitationInterface callback){
        this.callback = callback;
    }

    static class InviteMessagesViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, monthTxt, dayTxt;
        Button acceptBtn, declineBtn;

        private InviteMessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.event_name);
            monthTxt = itemView.findViewById(R.id.text_month);
            dayTxt = itemView.findViewById(R.id.text_day);
            acceptBtn = itemView.findViewById(R.id.accept_event_btn);
            declineBtn = itemView.findViewById(R.id.decline_event_btn);
        }
    }

    @NonNull
    @Override
    public EventInvitations_Adapter.InviteMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_invite_item,viewGroup,false);
        return new InviteMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InviteMessagesViewHolder viewHolder, int i) {
        if(eventInviteSet !=null){
            final Event_Model event = eventInviteSet.get(i);
            viewHolder.titleTxt.setText(event.getName());
            viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onRespond(event, ACCEPT);
                    removeItem(viewHolder.getAdapterPosition());
                }
            });

            viewHolder.declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onRespond(event,DECLINE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(eventInviteSet != null){
            return eventInviteSet.size();
        }
        return 0;
    }

    public void setData(List<Event_Model> eventsInviteList){
        this.eventInviteSet = eventsInviteList;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        eventInviteSet.remove(position);
        notifyItemRemoved(position);
    }
}
