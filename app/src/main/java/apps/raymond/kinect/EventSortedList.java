package apps.raymond.kinect;

import android.support.v7.util.SortedList;

import apps.raymond.kinect.Events.GroupEvent;

public class EventSortedList extends SortedList.Callback<GroupEvent> {

    @Override
    public int compare(GroupEvent event, GroupEvent t21) {
        return 0;
    }

    @Override
    public void onChanged(int i, int i1) {

    }

    @Override
    public boolean areContentsTheSame(GroupEvent event, GroupEvent t21) {
        return false;
    }

    @Override
    public boolean areItemsTheSame(GroupEvent event, GroupEvent t21) {
        return false;
    }

    @Override
    public void onInserted(int i, int i1) {

    }

    @Override
    public void onRemoved(int i, int i1) {

    }

    @Override
    public void onMoved(int i, int i1) {

    }
}
