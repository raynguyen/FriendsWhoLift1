package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.UserProfile.User_Model;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ProfileViewHolder> {

    private ProfileClickListener listener;
    public interface ProfileClickListener {
        void onProfileClick(User_Model userModel);
    }

    private List<User_Model> mDataSet;
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
        final User_Model currUser = mDataSet.get(i);
        vh.name.setText(currUser.getEmail());
        vh.itemView.setOnClickListener((View v)-> listener.onProfileClick(currUser));
    }

    @Override
    public int getItemCount() {
        if(mDataSet !=null){
            return mDataSet.size();
        } else {
            return 0;
        }
    }

    public void setData(List<User_Model> newData){
        if(mDataSet==null){
            this.mDataSet = newData;
            notifyItemRangeChanged(0,newData.size());
        } else {
            //DIFFUTIL HERE.
        }

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
