package apps.raymond.friendswholift.Interfaces;

import android.widget.TextView;

import apps.raymond.friendswholift.Groups.GroupBase;

public interface GroupClickListener {
    void onGroupClick(int position, GroupBase groupBase, TextView sharedView);
}
