package apps.raymond.kinect.CoreFragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.R;

public class ExploreEvents_Adapter extends RecyclerView.Adapter<ExploreEvents_Adapter.EventViewHolder> {
    private static final String TAG = "ExploreEvents_Adapter";

    private interface ViewHolderListener{
        void onItemClicked(View view, int adapterPosition);
    }

    private ViewHolderListener viewHolderListener;
    private List<Event_Model> mDataSet;
    ExploreEvents_Adapter(Fragment fragment){
        this.viewHolderListener = new ViewHolderListenerImpl(fragment);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_explore_event, viewGroup, false);
        return new EventViewHolder(view, viewHolderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int i) {
        if(mDataSet != null){
            eventViewHolder.onBind(mDataSet.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if(mDataSet != null){
            return mDataSet.size();
        } else {
            return 0;
        }
    }

    public void setData(List<Event_Model> newData){
        if(mDataSet == null){
            mDataSet = new ArrayList<>(newData);
        } else {
            //DIFFUTIL SHIT
        }
    }

    /**
     * ViewHolder class to which an event is bound.
     */
    static class EventViewHolder extends RecyclerView.ViewHolder{

        TextView textEventName;
        EventViewHolder(View itemView, ViewHolderListener listener){
            super(itemView);
            textEventName = itemView.findViewById(R.id.text_event_name);

            /*
            The ViewHolderListener passed to each ViewModel is the listener of the adapter. All
            ViewHolders created for the ViewPager are held by the same adapter.
            */
            itemView.findViewById(R.id.cardview_event).setOnClickListener((View v) ->
                    listener.onItemClicked(v, getAdapterPosition()));
        }

        /**
         * Method which is called by the ViewHolder's adapter. This method will bind the Event with
         * a ViewHolder so that the ViewHolder displays the proper information.
         * @param event Event_Model that will be bound to the respective ViewHolder.
         */
        void onBind(Event_Model event){
            String eventName = event.getName();
            textEventName.setText(eventName);
        }


    }

    /**
     *
     * Note: By having the ViewHolderListener implementation class a subclass of the ExploreEvents
     * Adapter class, we can separate the Fragment from the interface should we require the fragment
     * without the interface.
     */
    private static class ViewHolderListenerImpl implements ViewHolderListener{

        private Fragment fragment;
        private AtomicBoolean enterTransitionStarted;

        ViewHolderListenerImpl(Fragment fragment){
            this.fragment = fragment;
            this.enterTransitionStarted = new AtomicBoolean();
        }

        @Override
        public void onItemClicked(View view, int adapterPosition) {
            Log.w(TAG,"Clicked on an event: ");
        }

    }
}
