package apps.raymond.friendswholift.Groups;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Interfaces.GroupClickListener;
import apps.raymond.friendswholift.R;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.GroupViewHolder>{
    private static final String TAG = "GROUP RECYCLER ADAPTER";
    private List<GroupBase> groupsList;
    private GroupClickListener groupClickListener;

    GroupRecyclerAdapter(List<GroupBase> myGroups, GroupClickListener groupClickListener){
        this.groupsList = myGroups;
        this.groupClickListener =  groupClickListener;
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
                .inflate(R.layout.group_cardview, viewGroup, false);
        return new GroupViewHolder(view);
    }

    // Called immediately after onCreateViewHolder. Binds our data to the new ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull final GroupRecyclerAdapter.GroupViewHolder viewHolder, int position) {
        if(groupsList !=null){
            final GroupBase currentGroup = groupsList.get(position);

            viewHolder.nameTxt.setText(currentGroup.getName());
            viewHolder.descTxt.setText(currentGroup.getDescription());
            /*
            if(currentGroup.getImageURI()!=null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(currentGroup.getBytes(),0,currentGroup.getBytes().length);
                viewHolder.groupImage.setImageBitmap(bitmap);
            }*/
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupClickListener.onGroupClick(viewHolder.getAdapterPosition(),currentGroup);
                }
            });
        }
    }

    public void setData(List<GroupBase> myGroups){
        this.groupsList = myGroups;
    }

    @Override
    public int getItemCount() {
        if (groupsList !=null){
            return groupsList.size();
        } else {
            return 0;
        }
    }

    public void filter(String searchText){
        List<GroupBase> myGroupsCopy = new ArrayList<>();
        myGroupsCopy.clear();
        if(searchText.isEmpty()){
            myGroupsCopy.addAll(groupsList);
        } else {
            for(GroupBase group:myGroupsCopy){
                if(group.getName().toLowerCase().contains(searchText)){
                    myGroupsCopy.add(group);
                }
            }
        }
    }


}
