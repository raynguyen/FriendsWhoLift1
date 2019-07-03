package apps.raymond.kinect.Invitations;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class ConnectionRequests_Adapter extends RecyclerView.Adapter<ConnectionRequests_Adapter.RequestViewHolder> {

    public interface ConnectionRequestListener{
        void onRequestResponse(User_Model profile, boolean response);
        void onProfileDetail(User_Model profile);
    }

    private ConnectionRequestListener mListener;
    private List<User_Model> mDataSet;
    public ConnectionRequests_Adapter(ConnectionRequestListener listener){
        try{
            mListener = listener;
        } catch (ClassCastException e){
            //Some class cast error handling?
        }
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_connection_request,viewGroup,false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder vh, int i) {
        User_Model profileModel = mDataSet.get(i);
        if(profileModel.getName()!=null && profileModel.getName2()!=null){
            String name = profileModel.getName() + " " + profileModel.getName2();
            vh.txtName.setText(name);
        }
        vh.btnAccept.setOnClickListener((View v)-> mListener.onRequestResponse(profileModel,true));
        vh.btnDecline.setOnClickListener((View v)->mListener.onRequestResponse(profileModel, false));
        vh.itemView.setOnClickListener((View v)->mListener.onProfileDetail(profileModel));
    }

    @Override
    public int getItemCount() {
        if(mDataSet!=null){
            return mDataSet.size();
        }
        return 0;
    }

    public void setData(List<User_Model> newData){
        if(mDataSet==null){
            mDataSet = new ArrayList<>(newData);
        } else {
            mDataSet.clear();
            mDataSet.addAll(newData);
            notifyItemRangeChanged(0, mDataSet.size());
        }
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgProfile;
        private TextView txtName;
        private Button btnAccept, btnDecline;
        private RequestViewHolder(@NonNull View view){
            super(view);
            imgProfile = view.findViewById(R.id.image_profile_pic);
            txtName = view.findViewById(R.id.text_name);
            btnAccept = view.findViewById(R.id.button_request_accept);
            btnDecline = view.findViewById(R.id.button_request_decline);
        }
    }
}
