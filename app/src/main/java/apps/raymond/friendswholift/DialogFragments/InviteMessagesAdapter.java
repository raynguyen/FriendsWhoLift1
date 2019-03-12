package apps.raymond.friendswholift.DialogFragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.R;

public class InviteMessagesAdapter extends RecyclerView.Adapter<InviteMessagesAdapter.InviteMessagesViewHolder> {
    List<GroupEvent> eventInviteList;

    public InviteMessagesAdapter(List<GroupEvent> eventInviteList){
        this.eventInviteList = eventInviteList;
    }

    static class InviteMessagesViewHolder extends RecyclerView.ViewHolder{

        public InviteMessagesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public InviteMessagesAdapter.InviteMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_invite_item,viewGroup,false);
        return new InviteMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteMessagesViewHolder inviteMessagesViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
