package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import apps.raymond.kinect.UserProfile.User_Model;

public class AddUsers_Adapter extends RecyclerView.Adapter<AddUsers_Adapter.UserViewHolder> {

    public interface CheckProfileInterface{
        void addToCheckedList(User_Model clickedUser);
        void removeFromCheckedList(User_Model clickedUser);
    }

    private List<User_Model> users;
    private CheckProfileInterface checkedInterface;
    public AddUsers_Adapter(List<User_Model> users, CheckProfileInterface checkedInterface){
        this.users = users;
        this.checkedInterface = checkedInterface;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTxt;
        private CheckBox checkBox;

        private UserViewHolder(View view){
            super(view);
            nameTxt = view.findViewById(R.id.user_name_txt);
            checkBox = view.findViewById(R.id.invite_user_checkbox);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_users_item,viewGroup,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder userViewHolder, int i) {
        final User_Model currentUser = users.get(i);
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