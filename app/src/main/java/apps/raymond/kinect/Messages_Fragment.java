package apps.raymond.kinect;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Groups.Group_Model;
import apps.raymond.kinect.UserProfile.User_Model;

public class Messages_Fragment extends Fragment implements View.OnClickListener, Messages_Adapter.ProfileClickListener{
    private static final String TAG = "MessagesFragment";
    private static final String EVENT = "Event";
    private static final String GROUP = "Group";

    private MessagesFragment_Interface activityInterface;
    public interface MessagesFragment_Interface{
        User_Model getCurrentUser();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            activityInterface = (MessagesFragment_Interface) context;
        }catch (ClassCastException e){
            Log.w(TAG,"Exception.",e);
        }
    }

    public static Messages_Fragment newInstance(Event_Model event){
        Messages_Fragment fragment = new Messages_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    public static Messages_Fragment newInstance(Group_Model groupBase){
        Messages_Fragment fragment = new Messages_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(GROUP,groupBase);
        fragment.setArguments(args);
        return fragment;
    }

    Repository_ViewModel viewModel;
    Event_Model event;
    Group_Model group;
    User_Model currUser;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
        try{
            event = getArguments().getParcelable(EVENT);
            group = getArguments().getParcelable(GROUP);
        } catch (Exception e){
            Log.w(TAG, "Some exception caught:",e);
        }

        currUser = activityInterface.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_,container,false);
        return view;
    }

    List<Message_Model> messages = new ArrayList<>();
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

    private void loadMessages(){
    }

    private void createMessage(String messageBody){
        String author = currUser.getEmail();
        long timeStamp = System.currentTimeMillis();
        final Message_Model newMessage = new Message_Model(author,messageBody,timeStamp);
        viewModel.postNewMessage(event, newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mAdapter.addNewMessage(newMessage);
                    return;
                }
                Log.w(TAG,"Some error when creating message.",task.getException());
            }
        });

    }
}