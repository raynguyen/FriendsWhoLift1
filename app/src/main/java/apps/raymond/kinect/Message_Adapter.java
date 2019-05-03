package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.MessageViewHolder> {

    private ProfileClickListener listener;
    public interface ProfileClickListener{
        void loadProfile();
    }

    private List<Message_Model> messages;
    public Message_Adapter(List<Message_Model> messages, ProfileClickListener listener){
        this.messages = new ArrayList<>(messages);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_cardview, viewGroup,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder vh, int i) {
        if(messages !=null){
            final Message_Model message = messages.get(i);
            vh.messageTxt.setText(message.getMessage());

            long timelong = message.getTimestamp();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timelong);
            vh.timestampTxt.setText(c.getTime().toString());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{

        private TextView messageTxt, timestampTxt;
        private ImageView profileImg;
        private MessageViewHolder(View view){
            super(view);
            messageTxt = view.findViewById(R.id.text_message);
            timestampTxt = view.findViewById(R.id.text_timestamp);
            profileImg = view.findViewById(R.id.image_profile);
        }
    }
}
