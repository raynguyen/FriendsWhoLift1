package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.UserProfile.User_Model;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ProfileViewHolder>
    implements Filterable {

    private ProfileClickListener listener;
    public interface ProfileClickListener {
        void onProfileClick(User_Model userModel);
    }

    private List<User_Model> mCompleteSet;
    private List<User_Model> mDisplaySet;

    public ProfileRecyclerAdapter(ProfileClickListener profileClickListener){
        this.listener = profileClickListener;
    }

    @NonNull
    @Override
    public ProfileRecyclerAdapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleritem_user,viewGroup,false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileViewHolder vh, int i) {
        final User_Model currUser = mDisplaySet.get(i);
        vh.name.setText(currUser.getEmail());
        vh.itemView.setOnClickListener((View v)-> listener.onProfileClick(currUser));
    }

    @Override
    public int getItemCount() {
        if(mDisplaySet !=null){
            return mDisplaySet.size();
        } else {
            return 0;
        }
    }

    public void setData(List<User_Model> newList){
        if(mCompleteSet ==null){
            mCompleteSet = new ArrayList<>(newList);
            mDisplaySet = new ArrayList<>(newList);
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mCompleteSet.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldPosition, int newPosition) {
                    return mCompleteSet.get(oldPosition).getEmail()
                            .equals(newList.get(newPosition).getEmail());
                }

                //Disabled currently
                @Override
                public boolean areContentsTheSame(int oldPosition, int newPosition) {
                    return true;
                }
            });

            mCompleteSet.clear();
            mCompleteSet.addAll(newList);
            mDisplaySet.clear();
            mDisplaySet.addAll(newList);
            result.dispatchUpdatesTo(this);
        }
        notifyItemRangeChanged(0,newList.size());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if(constraint==null || constraint.length()==0){
                    mDisplaySet = mCompleteSet;
                } else {
                    List<User_Model> filteredList = new ArrayList<>();
                    String string = constraint.toString().toLowerCase().trim();
                    for(User_Model user : mCompleteSet){
                        if(user.getEmail().toLowerCase().trim().contains(string)){
                            filteredList.add(user);
                        }
                    }
                    mDisplaySet = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = mDisplaySet;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDisplaySet = (ArrayList<User_Model>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * View holder class to contain the information of Users.
     */
    static class ProfileViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ProfileViewHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.user_name_txt);
        }
    }
}
