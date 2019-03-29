package apps.raymond.kinect;

import android.support.v7.util.SortedList;

import apps.raymond.kinect.Events.Event_Model;

public class EventSortedList extends SortedList.Callback<Event_Model> {

    @Override
    public int compare(Event_Model event1, Event_Model event2) {
        return 0;
    }

    @Override
    public void onChanged(int position, int count) {

    }

    @Override
    public boolean areContentsTheSame(Event_Model event1, Event_Model event2) {
        return false;
    }

    @Override
    public boolean areItemsTheSame(Event_Model event1, Event_Model event2) {
        return false;
    }

    @Override
    public void onInserted(int position, int count) {

    }

    @Override
    public void onRemoved(int position, int count) {

    }

    @Override
    public void onMoved(int position, int count) {

    }
}
