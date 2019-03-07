package apps.raymond.friendswholift;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import apps.raymond.friendswholift.UserProfile.UserModel;

public class Add_Users_Adapter extends RecyclerView.Adapter<Add_Users_Adapter.UserViewHolder> {
    private static final String TAG = "Add_Users_Adapter";

    public interface CheckProfileInterface{
        void getCheckedList();
    }

    private List<UserModel> users;
    private CheckProfileInterface checkedInterface;
    public Add_Users_Adapter(List<UserModel> users, CheckProfileInterface checkedInterface){
        this.users = users;
        this.checkedInterface = checkedInterface;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTxt;
        private CheckBox checkBox;

        private UserViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
            nameTxt = view.findViewById(R.id.user_name_txt);
            checkBox = view.findViewById(R.id.invite_user_checkbox);
        }

        @Override
        public void onClick(View v) {
            if(checkBox.isChecked()){
                Log.i(TAG,"What the fuk");
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
            }
        }

        // TODO: When an item is clicked, we want to update the array list in the Group_Create_Fragment that stores all users to invite.
        // How do we access the interface in the adapter from inside the ViewHolder??
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.i(TAG,"Creating view holder.");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_users_item,viewGroup,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder userViewHolder, int i) {
        Log.i(TAG,"Binding view holder for user: "+users.get(i).getEmail());
        if(users != null){
            final UserModel currentUser = users.get(i);
            userViewHolder.nameTxt.setText(currentUser.getEmail());
        } else {
            Log.i(TAG,"Adapter was passed an empty list.");
        }
    }

    @Override
    public int getItemCount() {
        if(users !=null){
            return users.size();
        } else {
            return 0;
        }
    }
}
