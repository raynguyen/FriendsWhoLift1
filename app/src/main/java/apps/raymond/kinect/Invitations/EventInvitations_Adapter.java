package apps.raymond.kinect.Invitations;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.R;

/**
 * Adapter class responsible for inflating the views for the event invitations recycler view.
 */
public class EventInvitations_Adapter extends
        RecyclerView.Adapter<EventInvitations_Adapter.InviteMessagesViewHolder> {
    public static final int DECLINE = 0;
    public static final int ACCEPT = 1;
    private List<Event_Model> mEventSet;
    private EventInvitationInterface callback;

    public interface EventInvitationInterface {
        void onEventDetail(Event_Model event);
        void onInvitationResponse(Event_Model event, int response);
    }

    public EventInvitations_Adapter(EventInvitationInterface callback){
        this.callback = callback;
    }

    @NonNull
    @Override
    public InviteMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_invite_item,viewGroup,false);
        return new InviteMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InviteMessagesViewHolder viewHolder, int i) {
        if(mEventSet !=null){
            final Event_Model event = mEventSet.get(i);
            viewHolder.titleTxt.setText(event.getName());
            viewHolder.viewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w("eventInvite","blow up detail event activity");
                }
            });

            viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(viewHolder.getAdapterPosition());
                    callback.onInvitationResponse(event, ACCEPT);
                }
            });

            viewHolder.declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(viewHolder.getAdapterPosition());
                    callback.onInvitationResponse(event,DECLINE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mEventSet != null){
            return mEventSet.size();
        }
        return 0;
    }

    /**
     * Method to set the data for the RecyclerView. If mEventSet is null, this adapter has not been
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
        if(mEventSet==null){
            mEventSet = new ArrayList<>(newList);
            notifyItemRangeChanged(0,newList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mEventSet.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldPosition, int newPosition) {
                    return mEventSet.get(oldPosition).getName()
                            .equals(newList.get(newPosition).getName());
                }

                //Disabled currently
                @Override
                public boolean areContentsTheSame(int oldPosition, int newPosition) {
                    return true;
                }
            });
            mEventSet.clear();
            mEventSet.addAll(newList);
            result.dispatchUpdatesTo(this);
        }
    }

    private void removeItem(int position){
        mEventSet.remove(position);
        notifyItemRemoved(position);
    }

    static class InviteMessagesViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt, monthTxt, dayTxt;
        Button acceptBtn, declineBtn;
        View viewGroup;
        private InviteMessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.event_name);
            monthTxt = itemView.findViewById(R.id.text_month_day);
            dayTxt = itemView.findViewById(R.id.text_day);
            acceptBtn = itemView.findViewById(R.id.accept_event_btn);
            declineBtn = itemView.findViewById(R.id.decline_event_btn);
            viewGroup = itemView;
        }
    }
}
