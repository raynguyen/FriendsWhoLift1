package apps.raymond.kinect.Invitations;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.UserProfile.User_Model;

public class ConnectionRequests_Adapter extends RecyclerView.Adapter<ConnectionRequests_Adapter.RequestViewHolder> {

    public interface ConnectionRequestListener{
        void onRequestResponse(User_Model profile);
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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder requestViewHolder, int i) {

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

        private TextView txtName;
        private RequestViewHolder(@NonNull View view){
            super(view);
        }
    }
}
