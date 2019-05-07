package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Messages_Adapter extends RecyclerView.Adapter<Messages_Adapter.MessageViewHolder> {

    private ProfileClickListener listener;
    public interface ProfileClickListener{
        void loadProfile();
    }

    private List<Message_Model> messages;
    public Messages_Adapter(List<Message_Model> messages, ProfileClickListener listener){
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
        if(messages!=null && messages.size()!=0){
            final Message_Model message = messages.get(i);
            vh.textAuthor.setText(message.getAuthor());
            vh.textMessage.setText(message.getMessage());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
            Date date = new Date(message.getTimestamp());
            String timeStamp = sdf.format(date);
            vh.textTimestamp.setText(timeStamp);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateData(List<Message_Model> newMessages){
        messages = newMessages;
        notifyDataSetChanged();
    }

    public void addNewMessage(Message_Model newMessage){
        messages.add(0,newMessage);
        notifyItemInserted(0);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{

        private TextView textAuthor,textMessage,textTimestamp;
        private MessageViewHolder(View view){
            super(view);
            textAuthor = view.findViewById(R.id.text_author);
            textMessage = view.findViewById(R.id.text_message);
            textTimestamp = view.findViewById(R.id.text_timestamp);
        }
    }
}
