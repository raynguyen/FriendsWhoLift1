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
import apps.raymond.friendswholift.Groups.GroupBase;
import apps.raymond.friendswholift.R;

public class GroupInviteAdapter extends RecyclerView.Adapter<GroupInviteAdapter.GroupInviteViewHolder> {
    private List<GroupBase> groupInviteList;
    private InviteResponseListener callback;

    public interface InviteResponseListener{
        void onAccept(GroupBase group);
        void onDecline();
        void onDetail();
    }

    public GroupInviteAdapter(InviteResponseListener callback){
        this.callback = callback;
    }

    public GroupInviteAdapter(List<GroupBase> groupInviteList, InviteResponseListener callback){
        this.groupInviteList = groupInviteList;
        this.callback = callback;
    }

    static class GroupInviteViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        Button acceptBtn, declineBtn;

        private GroupInviteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.group_name);
            acceptBtn = itemView.findViewById(R.id.accept_group_btn);
            declineBtn = itemView.findViewById(R.id.decline_group_btn);
        }
    }

    @NonNull
    @Override
    public GroupInviteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_invite_item,viewGroup,false);
        return new GroupInviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupInviteViewHolder viewHolder, int i) {
        if(groupInviteList !=null){
            Log.i("INVITEADAPTER","Creating an item for invite.");
            final GroupBase group = groupInviteList.get(i);
            viewHolder.titleTxt.setText(group.getName());

            viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onAccept(group);
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
        if(groupInviteList != null){
            return groupInviteList.size();
        } else {
            return 0;
        }
    }

    public void setData(List<GroupBase> groupInviteList){
        this.groupInviteList = groupInviteList;
    }
}
