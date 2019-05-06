package apps.raymond.kinect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Groups.GroupBase;

public class Messages_Fragment extends Fragment implements View.OnClickListener, Messages_Adapter.ProfileClickListener{
    private static final String EVENT = "Event";
    private static final String GROUP = "Group";

    public static Messages_Fragment newInstance(Event_Model event){
        Messages_Fragment fragment = new Messages_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    public static Messages_Fragment newInstance(GroupBase groupBase){
        Messages_Fragment fragment = new Messages_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(GROUP,groupBase);
        fragment.setArguments(args);
        return fragment;
    }

    List<Message_Model> messages = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_,container,false);
        return view;
    }

    Button btnPostMessage, btnDiscardMessage;
    RecyclerView messagesRecycler;
    Messages_Adapter mAdapter;
    EditText editNewMessage;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPostMessage = view.findViewById(R.id.button_post);
        btnDiscardMessage = view.findViewById(R.id.button_discard);
        btnPostMessage.setOnClickListener(this);
        btnDiscardMessage.setOnClickListener(this);

        messagesRecycler = view.findViewById(R.id.recyclerview_messages);
        mAdapter = new Messages_Adapter(messages, this);
        messagesRecycler.setAdapter(mAdapter);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        editNewMessage = view.findViewById(R.id.edit_new_message);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_post:
                if(editNewMessage.getText().toString().trim().length()>0){
                    Log.w("MessageFragment","Message is: "+editNewMessage.getText().toString());
                    createMessage(editNewMessage.getText().toString());
                    editNewMessage.getText().clear();
                }
                return;
            case R.id.button_discard:
                editNewMessage.getText().clear();
                return;
            default:
                break;
        }
    }

    @Override
    public void loadProfile() {

    }

    private void createMessage(String message){
        String author = "Me";
        long timeStamp = System.currentTimeMillis();
        Message_Model newMessage = new Message_Model(author,message,timeStamp);
        mAdapter.addNewMessage(newMessage);
    }
}