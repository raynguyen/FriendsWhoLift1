package apps.raymond.friendswholift.Interfaces;

import android.view.View;

import apps.raymond.friendswholift.Groups.GroupBase;

public interface GroupClickListener {
    void onGroupClick(int position, GroupBase groupBase, View sharedView);
}
