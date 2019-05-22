package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.UserProfile.User_Model;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ProfileViewHolder> {
    private static final String TAG = "PROFILE RECYCLER ADAPTER";

    private ProfileClickListener listener;
    public interface ProfileClickListener {
        void onProfileClick(User_Model userModel);
    }

    private List<User_Model> mDataSet;
    public ProfileRecyclerAdapter(ProfileClickListener profileClickListener){
        this.listener = profileClickListener;
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ProfileViewHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.user_name_txt);
        }
    }

    @NonNull
    @Override
    public ProfileRecyclerAdapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_recycler_item,viewGroup,false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileViewHolder profileViewHolder, int i) {
        if(mDataSet !=null){
            final User_Model currUser = mDataSet.get(i);
            profileViewHolder.name.setText(currUser.getEmail());
            profileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onProfileClick(currUser);
                }
            });
        } else {
            Log.i(TAG,"Empty profiles list.");
        }
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
        this.mDataSet = newData;
        notifyItemRangeChanged(0,newData.size());
    }
}
