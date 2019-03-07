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
        void addToCheckedList(UserModel clickedUser);
        void removeFromCheckedList(UserModel clickedUser);
    }

    private List<UserModel> users;
    private CheckProfileInterface checkedInterface;
    public Add_Users_Adapter(List<UserModel> users, CheckProfileInterface checkedInterface){
        this.users = users;
        this.checkedInterface = checkedInterface;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTxt;
        private CheckBox checkBox;

        private UserViewHolder(View view){
            super(view);
            //view.setOnClickListener(this);
            nameTxt = view.findViewById(R.id.user_name_txt);
            checkBox = view.findViewById(R.id.invite_user_checkbox);
        }

        // How do we call this once and not per onBindViewHolder?
        /*
        @Override
        public void onClick(View v) {
            if(checkBox.isChecked()){
                Log.i(TAG,"What the fuk");
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
            }
        }*/
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

        final UserModel currentUser = users.get(i);
        userViewHolder.nameTxt.setText(currentUser.getEmail());

        userViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userViewHolder.checkBox.isChecked()){
                    userViewHolder.checkBox.setChecked(false);
                    checkedInterface.removeFromCheckedList(currentUser);
                } else {
                    userViewHolder.checkBox.setChecked(true);
                    checkedInterface.addToCheckedList(currentUser);
                }
            }
        });

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
