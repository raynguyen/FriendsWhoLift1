package apps.raymond.kinect.CoreFragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.atomic.AtomicBoolean;

public class ExploreEvents_Adapter extends RecyclerView.Adapter<ExploreEvents_Adapter.EventViewHolder> {

    private interface ViewHolderListener{
        void onItemClicked(View view, int adapterPosition);
    }

    private ViewHolderListener viewHolderListener;

    public ExploreEvents_Adapter(){
        this.viewHolderListener = new ViewHolderListenerImpl();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder{

        private ViewHolderListener listener;

        EventViewHolder(View itemView, ViewHolderListener listener){
            super(itemView);
            this.listener = listener;

        }


    }

    private static class ViewHolderListenerImpl implements ViewHolderListener{

        private AtomicBoolean enterTransitionStarted;

        @Override
        public void onItemClicked(View view, int adapterPosition) {

        }

    }
}
