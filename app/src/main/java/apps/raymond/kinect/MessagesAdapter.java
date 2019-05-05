package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_cardview,viewGroup,false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder messagesViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class MessagesViewHolder extends RecyclerView.ViewHolder{

        private TextView textMessage;
        private Button buttonEndorse;
        private MessagesViewHolder(View view){
            super(view);
            textMessage = view.findViewById(R.id.text_message);
            buttonEndorse = view.findViewById(R.id.button_endorse);
        }
    }
}
