package apps.raymond.friendswholift.UserProfile;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import apps.raymond.friendswholift.Interfaces.ProfileClickListener;
import apps.raymond.friendswholift.R;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ProfileViewHolder> {
    private static final String TAG = "PROFILE RECYCLER ADAPTER";
    private List<String> profiles;
    private ProfileClickListener listener;

    public ProfileRecyclerAdapter(List<String> profiles, ProfileClickListener profileClickListener){
        this.profiles = profiles;
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item,viewGroup,false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileViewHolder profileViewHolder, int i) {
        if(profiles!=null){
            Log.i(TAG,"Attempting to make an item for : " + profiles.get(i));
            profileViewHolder.name.setText(profiles.get(i));
            profileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onProfileClick(profiles.get(profileViewHolder.getAdapterPosition()));
                }
            });
        } else {
            Log.i(TAG,"Empty profiles list.");
        }
    }

    @Override
    public int getItemCount() {
        if(profiles !=null){
            return profiles.size();
        } else {
            return 0;
        }
    }

    public void setData(List<String> profiles){
        this.profiles = profiles;
    }
}
