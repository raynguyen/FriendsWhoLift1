package apps.raymond.kinect;

import android.support.v7.util.SortedList;

import apps.raymond.kinect.Events.GroupEvent;

public class EventSortedList extends SortedList.Callback<GroupEvent> {

    @Override
    public int compare(GroupEvent event1, GroupEvent event2) {
        return 0;
    }

    @Override
    public void onChanged(int position, int count) {

    }

    @Override
    public boolean areContentsTheSame(GroupEvent event1, GroupEvent event2) {
        return false;
    }

    @Override
    public boolean areItemsTheSame(GroupEvent event1, GroupEvent event2) {
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
