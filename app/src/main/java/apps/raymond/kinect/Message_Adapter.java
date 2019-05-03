package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.MessageViewHolder> {

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{

        private MessageViewHolder(View view){
            super(view);
        }
    }
}
