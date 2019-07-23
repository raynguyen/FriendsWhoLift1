package apps.raymond.kinect.CoreFragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.R;
import apps.raymond.kinect.ObjectModels.User_Model;

public class AddUsers_Adapter extends RecyclerView.Adapter<AddUsers_Adapter.UserViewHolder> {

    public interface CheckProfileInterface{
        void addToCheckedList(User_Model clickedUser);
        void removeFromCheckedList(User_Model clickedUser);
    }

    private List<User_Model> mUserList = new ArrayList<>();
    private CheckProfileInterface checkedInterface;
    public AddUsers_Adapter(CheckProfileInterface checkedInterface){
        this.checkedInterface = checkedInterface;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_users_item,viewGroup,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder userViewHolder, int i) {
        final User_Model currentUser = mUserList.get(i);
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
        if(mUserList !=null){
            return mUserList.size();
        } else {
            return 0;
        }
    }

    public void setData(List<User_Model> userModels){
        this.mUserList = userModels;
        notifyItemRangeChanged(0,mUserList.size());
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
}
