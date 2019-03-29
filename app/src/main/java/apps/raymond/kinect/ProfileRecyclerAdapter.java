package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.UserProfile.UserModel;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ProfileViewHolder> {
    private static final String TAG = "PROFILE RECYCLER ADAPTER";

    private ProfileClickListener listener;
    public interface ProfileClickListener {
        void onProfileClick(UserModel userModel);
    }

    private List<UserModel> userModels;

    public ProfileRecyclerAdapter(List<UserModel> userModels, ProfileClickListener profileClickListener){
        this.userModels = userModels;
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
        if(userModels!=null){
            final UserModel currUser = userModels.get(i);
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
        if(userModels !=null){
            return userModels.size();
        } else {
            return 0;
        }
    }

    public void setData(List<UserModel> profiles){
        this.userModels = profiles;
    }
}
