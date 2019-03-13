package apps.raymond.friendswholift.DialogFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Events.GroupEvent;
import apps.raymond.friendswholift.Groups.GroupBase;
import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.Repository_ViewModel;

public class InviteDialog extends Fragment implements InviteMessagesAdapter.InviteResponseListener {
    private static final String TAG = "Invites_Dialog";

    public static InviteDialog newInstance(){
        InviteDialog dialog =  new InviteDialog();
        //Some arguments for dialog here?
        return dialog;
    }

    Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
    }

    List<GroupEvent> eventInviteList;
    List<GroupBase> groupInviteList;
    RecyclerView eventInviteRecycler;
    InviteMessagesAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_dialog,container,false);

        eventInviteRecycler = view.findViewById(R.id.messages_invite_recycler);
        eventInviteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InviteMessagesAdapter(this);
        eventInviteRecycler.setAdapter(adapter);
        eventInviteList = new ArrayList<>();
        fetchInvites();
        return view;
    }

    private void fetchInvites(){
        viewModel.fetchEventInvites().addOnCompleteListener(new OnCompleteListener<List<GroupEvent>>() {
            @Override
            public void onComplete(@NonNull Task<List<GroupEvent>> task) {
                if(task.isSuccessful()){
                    Log.i(TAG, "fetchInvites returned : "+ task.getResult());
                    eventInviteList.addAll(task.getResult());
                    adapter.setData(eventInviteList);
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public void onAccept() {
        Log.i(TAG,"Clicked to accept this event.");
    }

    @Override
    public void onDecline() {
        Log.i(TAG,"Clicked to decline this event.");
    }

    @Override
    public void onDetail() {
        Log.i(TAG,"Clicked to view in detail this event.");
    }
}
