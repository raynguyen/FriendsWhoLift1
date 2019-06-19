package apps.raymond.kinect;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
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
        void loadProfile(String author);
    }

    private List<Message_Model> mMessages;
    public Messages_Adapter(ProfileClickListener listener){
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
        final Message_Model message = mMessages.get(i);
        vh.viewGroup.setOnClickListener((View v)->listener.loadProfile(message.getAuthor()));
        vh.textAuthor.setText(message.getAuthor());
        vh.textMessage.setText(message.getMessage());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
        Date date = new Date(message.getTimestamp());
        String timeStamp = sdf.format(date);
        vh.textTimestamp.setText(timeStamp);

    }

    @Override
    public int getItemCount() {
        if(mMessages !=null){
            return mMessages.size();
        } else {
            return 0;
        }
    }

    public void setData(List<Message_Model> newMessages){
        if(mMessages ==null){
            mMessages = new ArrayList<>(newMessages);
            notifyItemRangeChanged(0,newMessages.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mMessages.size();
                }

                @Override
                public int getNewListSize() {
                    return newMessages.size();
                }

                @Override
                public boolean areItemsTheSame(int oldPosition, int newPosition) {
                    return mMessages.get(oldPosition).getMessage()
                            .equals(newMessages.get(newPosition).getMessage());
                }

                //Disabled currently
                @Override
                public boolean areContentsTheSame(int oldPosition, int newPosition) {
                    return true;
                }
            });
            mMessages.clear();
            mMessages.addAll(newMessages);
            result.dispatchUpdatesTo(this);
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{
        private ViewGroup viewGroup;
        private TextView textAuthor,textMessage,textTimestamp;
        private MessageViewHolder(View view){
            super(view);
            viewGroup = view.findViewById(R.id.cardview_message);
            textAuthor = view.findViewById(R.id.text_author);
            textMessage = view.findViewById(R.id.text_message);
            textTimestamp = view.findViewById(R.id.text_timestamp);
        }
    }
}
