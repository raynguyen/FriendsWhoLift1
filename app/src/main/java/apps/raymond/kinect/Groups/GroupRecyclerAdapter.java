package apps.raymond.kinect.Groups;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.R;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.GroupViewHolder>
    implements Filterable {

    private GroupClickListener groupClickListener;
    public interface GroupClickListener {
        void onGroupClick(int position, Group_Model groupBase, View sharedView);
    }

    private List<Group_Model> groupsList, groupsListClone;
    GroupRecyclerAdapter(List<Group_Model> myGroups, GroupClickListener groupClickListener){
        this.groupClickListener =  groupClickListener;
        this.groupsList = new ArrayList<>(myGroups);
        groupsListClone = myGroups;
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTxt, descTxt, tagsTxt;
        private ImageView groupImage;

        private GroupViewHolder(View groupCardView){
            super(groupCardView);
            nameTxt = groupCardView.findViewById(R.id.group_name_txt);
            descTxt = groupCardView.findViewById(R.id.desc_txt);
            tagsTxt = groupCardView.findViewById(R.id.tags_txt);
            groupImage = groupCardView.findViewById(R.id.group_image);
        }
    }

    // Called whenever we need to create a new ViewHolder
    @NonNull
    @Override
    public GroupRecyclerAdapter.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_group, viewGroup, false);
        return new GroupViewHolder(view);
    }

    // Called immediately after onCreateViewHolder. Binds our data to the new ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull final GroupRecyclerAdapter.GroupViewHolder viewHolder, int position) {
        if(groupsList !=null){
            final Group_Model currentGroup = groupsList.get(position);
            viewHolder.nameTxt.setText(currentGroup.getName());
            viewHolder.nameTxt.setTransitionName("Transition"+position);
            viewHolder.descTxt.setText(currentGroup.getDescription());

            // Todo: Best to move this into onCreateViewHolder otherwise each User_Model that is bound to a holder will create  anew onclick listener. It is better to create one.
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupClickListener.onGroupClick(viewHolder.getAdapterPosition(), currentGroup, viewHolder.nameTxt);
                }
            });
        }
    }

    public void setData(List<Group_Model> myGroups){
        this.groupsList = new ArrayList<>(myGroups);
        groupsListClone = myGroups;
        notifyDataSetChanged();
    }

    public void addData(Group_Model group){
        groupsList.add(group);
        notifyItemInserted(groupsList.size()-1);
    }

    @Override
    public int getItemCount() {
        if (groupsList !=null){
            return groupsList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return groupFilter;
    }

    private Filter groupFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Group_Model> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(groupsListClone);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Group_Model group : groupsListClone){
                    if(group.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(group);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            groupsList.clear();
            groupsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
