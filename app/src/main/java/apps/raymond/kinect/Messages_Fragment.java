package apps.raymond.kinect;

import android.arch.lifecycle.Observer;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Groups.Group_Model;
import apps.raymond.kinect.UserProfile.User_Model;

public class Messages_Fragment extends Fragment implements Messages_Adapter.ProfileClickListener {
    private static final String TAG = "MessagesFragment";
    private static final String EVENT = "Event";
    private static final String GROUP = "Group";

    private MessagesFragment_Interface activityInterface;
    public interface MessagesFragment_Interface{
        User_Model getCurrentUser();
        void onMessagesScrolled(View v, int dy);
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

    Core_ViewModel viewModel;
    Event_Model event;
    Group_Model group;
    User_Model currUser;
    List<Message_Model> messagesList = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
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

    TextView textEmptyMessages;
    List<Message_Model> messages = new ArrayList<>();
    ImageButton btnPostMessage;
    RecyclerView mRecyclerView;
    Messages_Adapter mAdapter;
    EditText editNewMessage;
    ProgressBar progressBar;
    ViewGroup mNewMessageView;
    int scrollDist = 0;
    boolean isVisible = true;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNewMessageView = view.findViewById(R.id.layout_new_message);
        btnPostMessage = view.findViewById(R.id.button_post);
        btnPostMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editNewMessage.getText().toString().trim().length()>0){
                    createMessage(editNewMessage.getText().toString());
                    editNewMessage.getText().clear();
                    try {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    } catch (Exception e){
                        //Purposely empty.
                    }
                }
            }
        });

        mRecyclerView = view.findViewById(R.id.recyclerview_messages);
        mAdapter = new Messages_Adapter(messages, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                activityInterface.onMessagesScrolled(mNewMessageView,dy);
            }
        });

        progressBar = view.findViewById(R.id.progress_loading_messages);
        textEmptyMessages = view.findViewById(R.id.text_empty_messages);
        loadMessages();

        editNewMessage = view.findViewById(R.id.edit_new_message);
    }


    @Override
    public void loadProfile() {

    }

    /**
     * Observe LiveData messages list held by the ViewModel.
     */
    private void loadMessages(){
        Observer<List<Message_Model>> mObserver = new Observer<List<Message_Model>>() {
            @Override
            public void onChanged(@Nullable List<Message_Model> newMessages) {
                if(newMessages==null || newMessages.size()==0){
                    textEmptyMessages.setVisibility(View.VISIBLE);
                }
                messagesList = newMessages;
                mAdapter.updateData(newMessages);
                progressBar.setVisibility(View.GONE);
            }
        };
        viewModel.getMessages(event).observe(this,mObserver);
    }

    private void createMessage(String messageBody){
        String author = currUser.getEmail();
        long timeStamp = System.currentTimeMillis();
        final Message_Model newMessage = new Message_Model(author,messageBody,timeStamp);
        viewModel.postNewMessage(event, newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(textEmptyMessages.getVisibility()==View.VISIBLE){
                        textEmptyMessages.setVisibility(View.GONE);
                    }
                    mAdapter.addNewMessage(newMessage);
                    LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    manager.scrollToPositionWithOffset(0,0);
                }
            }
        });
    }

}