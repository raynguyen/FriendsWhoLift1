package apps.raymond.friendswholift.DialogFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.Groups.GroupBase;
import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.UserProfile.UserModel;

public class MessagesDialog extends DialogFragment {
    private static final String TAG = "SearchUsersDialog";

    public static MessagesDialog newInstance(){
        MessagesDialog dialog =  new MessagesDialog();
        //Some arguments for dialog here?
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    List<GroupEvent> eventInviteList;
    List<GroupBase> groupInviteList;
    RecyclerView eventInviteRecycler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messages_dialog_fragment,container,false);

        eventInviteRecycler = view.findViewById(R.id.messages_invite_recycler);
        eventInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //When an item inside the usersRecycler is clicked, we need to check it and add to arraylist.
        return view;
    }
}
