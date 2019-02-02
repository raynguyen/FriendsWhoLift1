package apps.raymond.friendswholift.Groups;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.raymond.friendswholift.R;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.GroupViewHolder>{
    private static final String TAG = "GROUP RECYCLER ADAPTER";
    private List<GroupBase> myGroups;

    GroupRecyclerAdapter(List<GroupBase> myGroups){
        this.myGroups = myGroups;
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt, descTxt, tagsTxt;
        private GroupViewHolder(View groupCardView){
            super(groupCardView);
            nameTxt = groupCardView.findViewById(R.id.group_name_txt);
            descTxt = groupCardView.findViewById(R.id.desc_txt);
            tagsTxt = groupCardView.findViewById(R.id.tags_txt);
        }
    }

    // Called whenever we need to create a new ViewHolder
    @NonNull
    @Override
    public GroupRecyclerAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.group_cardview, viewGroup, false);

        return new GroupViewHolder(view);
    }

    // Called immediately after onCreateViewHolder. Binds our data to the new ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull GroupRecyclerAdapter.GroupViewHolder viewHolder, int position) {
        if(myGroups!=null){
            GroupBase currentGroup = myGroups.get(position);
            viewHolder.nameTxt.setText(currentGroup.getName());
            viewHolder.descTxt.setText(currentGroup.getDescription());
        }
    }

    public void setData(List<GroupBase> myGroups){
        this.myGroups = myGroups;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (myGroups!=null){
            return myGroups.size();
        } else {
            return 0;
        }
    }

}
