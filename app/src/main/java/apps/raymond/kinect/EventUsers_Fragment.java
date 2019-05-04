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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventUsers_Fragment extends Fragment implements ProfileRecyclerAdapter.ProfileClickListener {
    private static final String TAG = "EventUsers_Fragment";
    private static final String EVENT = "Event";

    public static EventUsers_Fragment newInstance(Event_Model event){
        EventUsers_Fragment fragments = new EventUsers_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(EVENT, event);
        fragments.setArguments(args);
        return fragments;
    }

    Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(Repository_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventusers_,container,false);
        return view;
    }

    List<User_Model> invitedProfiles,declinedProfiles,acceptedProfiles;
    RecyclerView mAcceptedRecycler, mInvitedRecycler, mDeclinedRecycler;
    ProgressBar mAcceptedBar, mInvitedBar, mDeclinedBar;
    TextView acceptedCount, declinedCount, invitedCount, acceptedNullText, invitedNullText, declinedNullText;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAcceptedRecycler = view.findViewById(R.id.recycler_accepted);
        mInvitedRecycler = view.findViewById(R.id.recycler_invited);
        mDeclinedRecycler = view.findViewById(R.id.recycler_declined);

        mAcceptedBar = view.findViewById(R.id.progress_accepted);
        mInvitedBar = view.findViewById(R.id.progress_invited);
        mDeclinedBar = view.findViewById(R.id.progress_declined);

        acceptedNullText = view.findViewById(R.id.text_accepted_null);
        invitedNullText = view.findViewById(R.id.text_invited_null);
        declinedNullText = view.findViewById(R.id.text_declined_null);
    }

    private void setMemberRecyclers(View view){
        /*
         * For each recycler, simply populate the Recycler with a list of the profile names for each respective category.
         * When user clicks on a user, load the full Profile using the name in the list as our query field.
         */

        ProfileRecyclerAdapter acceptedAdapter = new ProfileRecyclerAdapter(acceptedProfiles,this);
        mAcceptedRecycler.setAdapter(acceptedAdapter);
        getAcceptedList(event);
        mAcceptedRecycler.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        mAcceptedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mAcceptedBar = view.findViewById(R.id.progress_accepted);
        acceptedNullText = view.findViewById(R.id.text_accepted_null);

        ProfileRecyclerAdapter declinedAdapter = new ProfileRecyclerAdapter(declinedProfiles,this);
        mDeclinedRecycler.setAdapter(declinedAdapter);
        getDeclinedList(event);
        mDeclinedRecycler.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        mDeclinedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mDeclinedBar = view.findViewById(R.id.progress_declined);
        declinedNullText = view.findViewById(R.id.text_declined_null);

        ProfileRecyclerAdapter invitedAdapter = new ProfileRecyclerAdapter(invitedProfiles, this);
        getInviteList();
        mInvitedRecycler.setAdapter(invitedAdapter);
        mInvitedRecycler.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        mInvitedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mInvitedBar = view.findViewById(R.id.progress_invited);
        invitedNullText = view.findViewById(R.id.text_invited_null);
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
        Log.i(TAG,"Attempting to get query of accepted users!");
        viewModel.getEventResponses(groupEvent, EVENT_DECLINED).addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
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
}
