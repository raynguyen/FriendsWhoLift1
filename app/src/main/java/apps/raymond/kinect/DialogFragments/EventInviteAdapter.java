package apps.raymond.kinect.DialogFragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.Events.GroupEvent;
import apps.raymond.kinect.R;

public class EventInviteAdapter extends RecyclerView.Adapter<EventInviteAdapter.InviteMessagesViewHolder> {
    private List<GroupEvent> eventInviteSet;
    private InviteResponseListener callback;

    public interface InviteResponseListener{
        void onAccept(GroupEvent event,int position);
        void onDecline(GroupEvent event);
        void onDetail();
    }

    public EventInviteAdapter(InviteResponseListener callback){
        this.callback = callback;
    }

    public EventInviteAdapter(List<GroupEvent> eventInviteList, InviteResponseListener callback){
        this.eventInviteSet = eventInviteList;
        this.callback = callback;
    }

    static class InviteMessagesViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, monthTxt, dayTxt;
        Button acceptBtn, declineBtn;

        private InviteMessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.event_name);
            monthTxt = itemView.findViewById(R.id.event_month);
            dayTxt = itemView.findViewById(R.id.event_day);
            acceptBtn = itemView.findViewById(R.id.accept_event_btn);
            declineBtn = itemView.findViewById(R.id.decline_event_btn);
        }
    }

    @NonNull
    @Override
    public EventInviteAdapter.InviteMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_invite_item,viewGroup,false);
        return new InviteMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InviteMessagesViewHolder viewHolder, int i) {
        if(eventInviteSet !=null){
            final GroupEvent event = eventInviteSet.get(i);
            viewHolder.titleTxt.setText(event.getName());
            viewHolder.monthTxt.setText(event.getMonth());
            viewHolder.dayTxt.setText(event.getDay());
            viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onAccept(event,viewHolder.getAdapterPosition());
                }
            });

            viewHolder.declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onDecline(event);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(eventInviteSet != null){
            return eventInviteSet.size();
        } else {
            return 0;
        }
    }

    public void setData(List<GroupEvent> eventsInviteList){
        this.eventInviteSet = eventsInviteList;
    }
}
