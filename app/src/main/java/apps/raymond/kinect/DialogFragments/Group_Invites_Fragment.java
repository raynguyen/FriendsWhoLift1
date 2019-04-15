package apps.raymond.kinect.DialogFragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.Groups.GroupBase;
import apps.raymond.kinect.R;

public class Group_Invites_Fragment extends RecyclerView.Adapter<Group_Invites_Fragment.GroupInviteViewHolder> {
    private List<GroupBase> groupInviteSet;
    private InviteResponseListener callback;

    public interface InviteResponseListener{
        void onAccept(GroupBase group, int position);
        void onDecline(GroupBase group, int position);
        void onDetail();
    }

    public Group_Invites_Fragment(InviteResponseListener callback){
        this.callback = callback;
    }

    public Group_Invites_Fragment(List<GroupBase> groupInviteList, InviteResponseListener callback){
        this.groupInviteSet = groupInviteList;
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
    public void onBindViewHolder(@NonNull final GroupInviteViewHolder viewHolder, int i) {
        if(groupInviteSet !=null){
            final GroupBase group = groupInviteSet.get(i);
            viewHolder.titleTxt.setText(group.getName());
            viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onAccept(group,viewHolder.getAdapterPosition());
                }
            });

            viewHolder.declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onDecline(group,viewHolder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(groupInviteSet != null){
            return groupInviteSet.size();
        } else {
            return 0;
        }
    }

    public void setData(List<GroupBase> groupInviteList){
        this.groupInviteSet = groupInviteList;
    }
}
