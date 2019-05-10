package apps.raymond.kinect;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.UserProfile.User_Model;


//ToDo: Currently three separate views that are navigated via ViewFlipper. This should be converted to a
// ViewPager if there are no plans to implement animations when clicking on users.
public class EventUsers_Fragment extends Fragment implements
        ProfileRecyclerAdapter.ProfileClickListener, View.OnClickListener{
    private static final String TAG = "EventUsers_Fragment";
    private static final String EVENT = "Event";
    private static final String EVENT_ACCEPTED = "Accepted";
    private static final String EVENT_DECLINED = "Declined";

    public static EventUsers_Fragment newInstance(Event_Model event){
        EventUsers_Fragment fragments = new EventUsers_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(EVENT, event);
        fragments.setArguments(args);
        return fragments;
    }

    Event_Model event;
    Core_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);
        event = getArguments().getParcelable(EVENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eventusers_,container,false);
    }

    ViewFlipper viewFlipper;
    Button mAcceptedButton, mInvitedButton, mDeclinedButton;
    List<User_Model> invitedProfiles,declinedProfiles,acceptedProfiles;
    RecyclerView mAcceptedRecycler, mInvitedRecycler, mDeclinedRecycler;
    ProgressBar mAcceptedBar, mInvitedBar, mDeclinedBar;
    TextView acceptedCount, declinedCount, invitedCount, acceptedNullText, invitedNullText, declinedNullText;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewFlipper = view.findViewById(R.id.viewflipper_profiles);

        mAcceptedButton = view.findViewById(R.id.button_accepted_users);
        mInvitedButton = view.findViewById(R.id.button_invited_users);
        mDeclinedButton = view.findViewById(R.id.button_declined_users);

        mAcceptedButton.setOnClickListener(this);
        mInvitedButton.setOnClickListener(this);
        mDeclinedButton.setOnClickListener(this);

        mAcceptedRecycler = view.findViewById(R.id.recycler_accepted);
        mInvitedRecycler = view.findViewById(R.id.recycler_invited);
        mDeclinedRecycler = view.findViewById(R.id.recycler_declined);

        mAcceptedBar = view.findViewById(R.id.progress_accepted);
        mInvitedBar = view.findViewById(R.id.progress_invited);
        mDeclinedBar = view.findViewById(R.id.progress_declined);

        acceptedNullText = view.findViewById(R.id.text_accepted_null);
        invitedNullText = view.findViewById(R.id.text_invited_null);
        declinedNullText = view.findViewById(R.id.text_declined_null);

        acceptedCount = view.findViewById(R.id.text_accepted_count);
        invitedCount = view.findViewById(R.id.text_invited_count);
        declinedCount = view.findViewById(R.id.text_declined_count);

        mAcceptedBar = view.findViewById(R.id.progress_accepted);
        mInvitedBar = view.findViewById(R.id.progress_invited);
        mDeclinedBar = view.findViewById(R.id.progress_declined);

        setMemberRecyclers();
    }

    ProfileRecyclerAdapter acceptedAdapter, invitedAdapter, declinedAdapter;
    private void setMemberRecyclers(){
        /*
         * For each recycler, simply populate the Recycler with a list of the profile names for each respective category.
         * When user clicks on a user, load the full Profile using the name in the list as our query field.
         */
        getAcceptedList(event);
        getInviteList(event);
        getDeclinedList(event);

        acceptedAdapter = new ProfileRecyclerAdapter(acceptedProfiles,this);
        mAcceptedRecycler.setAdapter(acceptedAdapter);
        mAcceptedRecycler.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        mAcceptedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        invitedAdapter = new ProfileRecyclerAdapter(invitedProfiles, this);
        mInvitedRecycler.setAdapter(invitedAdapter);
        mInvitedRecycler.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        mInvitedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        declinedAdapter = new ProfileRecyclerAdapter(declinedProfiles,this);
        mDeclinedRecycler.setAdapter(declinedAdapter);
        mDeclinedRecycler.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        mDeclinedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    private void getInviteList(final Event_Model event){
        viewModel.getEventInvitees(event).addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<User_Model>> task) {
                mInvitedBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        invitedNullText.setVisibility(View.VISIBLE);
                    }
                    invitedProfiles = new ArrayList<>();
                    invitedProfiles.addAll(task.getResult());
                    invitedCount.setText(String.valueOf(invitedProfiles.size()));
                    invitedAdapter.setData(invitedProfiles);
                    invitedAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error fetching invited list.",e);
            }
        });
    }

    //Todo: called twice??
    private void getAcceptedList(Event_Model groupEvent){
        viewModel.getEventResponses(groupEvent, EVENT_ACCEPTED).addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<User_Model>> task) {
                if(task.getResult().isEmpty()){
                    acceptedNullText.setVisibility(View.VISIBLE);
                }
                mAcceptedBar.setVisibility(View.INVISIBLE);
                acceptedProfiles = new ArrayList<>();
                acceptedProfiles.addAll(task.getResult());
                acceptedCount.setText(String.valueOf(acceptedProfiles.size()));
                acceptedAdapter.setData(acceptedProfiles);
                acceptedAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error fetching accepted list.",e);
            }
        });
    }

    private void getDeclinedList(Event_Model groupEvent){
        viewModel.getEventResponses(groupEvent, EVENT_DECLINED)
                .addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<User_Model>> task) {
                if(task.getResult().isEmpty()){
                    declinedNullText.setVisibility(View.VISIBLE);
                }
                mDeclinedBar.setVisibility(View.INVISIBLE);
                declinedProfiles = new ArrayList<>();
                declinedProfiles.addAll(task.getResult());
                declinedCount.setText(String.valueOf(declinedProfiles.size()));
                declinedAdapter.setData(declinedProfiles);
                declinedAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                declinedCount.setText("--");
                Log.w(TAG,"Error fetching declined list.",e);
            }
        });
    }

    @Override
    public void onProfileClick(User_Model userModel) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.button_accepted_users:
                viewFlipper.setDisplayedChild(0);
                break;
            case R.id.button_declined_users:
                viewFlipper.setDisplayedChild(1);
                break;
            case R.id.button_invited_users:
                viewFlipper.setDisplayedChild(2);
                break;
        }
    }
}
