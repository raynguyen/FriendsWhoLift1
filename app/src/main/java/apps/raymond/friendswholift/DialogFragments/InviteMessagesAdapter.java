package apps.raymond.friendswholift.DialogFragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.R;

public class InviteMessagesAdapter extends RecyclerView.Adapter<InviteMessagesAdapter.InviteMessagesViewHolder> {
    private List<GroupEvent> eventInviteList;
    private InviteResponseListener callback;

    public interface InviteResponseListener{
        void onAccept(GroupEvent groupEvent);
        void onDecline();
        void onDetail();
    }

    public InviteMessagesAdapter(InviteResponseListener callback){
        this.callback = callback;
    }

    public InviteMessagesAdapter(List<GroupEvent> eventInviteList, InviteResponseListener callback){
        this.eventInviteList = eventInviteList;
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
    public InviteMessagesAdapter.InviteMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_invite_item,viewGroup,false);
        return new InviteMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteMessagesViewHolder viewHolder, int i) {
        if(eventInviteList !=null){
            Log.i("INVITEADAPTER","Creating an item for invite.");
            final GroupEvent event = eventInviteList.get(i);
            viewHolder.titleTxt.setText(event.getName());
            viewHolder.monthTxt.setText(event.getMonth());
            viewHolder.dayTxt.setText(event.getDay());

            viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onAccept(event);
                }
            });

            viewHolder.declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onDecline();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(eventInviteList != null){
            return eventInviteList.size();
        } else {
            return 0;
        }
    }

    public void setData(List<GroupEvent> eventsInviteList){
        this.eventInviteList = eventsInviteList;
    }
}
