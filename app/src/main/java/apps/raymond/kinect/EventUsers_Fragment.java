package apps.raymond.kinect;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.UserProfile.Profile_Activity;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.EventDetail.EventDetail_ViewModel;


//ToDo: Currently three separate views that are navigated via ViewFlipper. This should be converted to a
// ViewPager if there are no plans to implement animations when clicking on users.
public class EventUsers_Fragment extends Fragment implements View.OnClickListener,
        ProfileRecyclerAdapter.ProfileClickListener{

    public static EventUsers_Fragment newInstance(Event_Model event, User_Model user){
        EventUsers_Fragment fragments = new EventUsers_Fragment();
        Bundle args = new Bundle();
        args.putParcelable("event", event);
        args.putParcelable("user",user);
        fragments.setArguments(args);
        return fragments;
    }

    private Event_Model mEventModel;
    private EventDetail_ViewModel mViewModel;
    private User_Model mUserModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(EventDetail_ViewModel.class);
        mEventModel = getArguments().getParcelable("event");
        mUserModel = getArguments().getParcelable("user");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eventusers_,container,false);
    }

    ViewFlipper viewFlipper;
    Button mAcceptedButton, mInvitedButton, mDeclinedButton;
    RecyclerView mAcceptedRecycler, mInvitedRecycler, mDeclinedRecycler;
    ProgressBar mAcceptedPB, mInvitedPB, mDeclinedPB;
    TextView txtAccepted, txtDeclined, txtInvited, txtAcceptedNull, txtInvitedNull, txtDeclinedNull;
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

        mAcceptedPB = view.findViewById(R.id.progress_accepted);
        mInvitedPB = view.findViewById(R.id.progress_invited);
        mDeclinedPB = view.findViewById(R.id.progress_declined);

        txtAcceptedNull = view.findViewById(R.id.text_accepted_null);
        txtInvitedNull = view.findViewById(R.id.text_invited_null);
        txtDeclinedNull = view.findViewById(R.id.text_declined_null);

        txtAccepted = view.findViewById(R.id.text_accepted_count);
        txtInvited = view.findViewById(R.id.text_invited_show);
        txtDeclined = view.findViewById(R.id.text_declined_count);

        mAcceptedPB = view.findViewById(R.id.progress_accepted);
        mInvitedPB = view.findViewById(R.id.progress_invited);
        mDeclinedPB = view.findViewById(R.id.progress_declined);

        setMemberRecyclers();
    }

    private ProfileRecyclerAdapter acceptedAdapter, invitedAdapter, declinedAdapter;

    /**
     * Fetch from the database a List of all the user's that have accepted, been invited, and
     * declined the Event invitation. Load the data into the RecyclerViews.
     */
    private void setMemberRecyclers(){
        acceptedAdapter = new ProfileRecyclerAdapter(this);
        mAcceptedRecycler.setAdapter(acceptedAdapter);
        mAcceptedRecycler.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        mAcceptedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        invitedAdapter = new ProfileRecyclerAdapter(this);
        mInvitedRecycler.setAdapter(invitedAdapter);
        mInvitedRecycler.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        mInvitedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        declinedAdapter = new ProfileRecyclerAdapter(this);
        mDeclinedRecycler.setAdapter(declinedAdapter);
        mDeclinedRecycler.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));
        mDeclinedRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mViewModel.getEventAccepted().observe(requireActivity(), new Observer<List<User_Model>>() {
            @Override
            public void onChanged(@Nullable List<User_Model> user_models) {
                if(mAcceptedPB.getVisibility()==View.VISIBLE){
                    mAcceptedPB.setVisibility(View.GONE);
                }
                if(user_models.size()==0 && txtAcceptedNull.getVisibility()!=View.GONE){
                    txtAcceptedNull.setVisibility(View.VISIBLE);
                }
                txtAccepted.setText(String.valueOf(user_models.size()));
                acceptedAdapter.setData(user_models);
            }
        });
        mViewModel.getEventInvited().observe(requireActivity(), new Observer<List<User_Model>>() {
            @Override
            public void onChanged(@Nullable List<User_Model> user_models) {
                if(mInvitedPB.getVisibility()==View.VISIBLE){
                    mInvitedPB.setVisibility(View.GONE);
                }
                if(user_models.size()==0 && mInvitedPB.getVisibility()==View.GONE){
                    mInvitedPB.setVisibility(View.VISIBLE);
                }
                txtInvited.setText(String.valueOf(user_models.size()));
                invitedAdapter.setData(user_models);
            }
        });
        mViewModel.loadEventMessages(mEventModel.getName());
    }

    @Override
    public void onProfileClick(User_Model profileModel) {
        if(profileModel.getEmail().equals(mUserModel.getEmail())){
            return;
        }
        Intent viewProfileIntent = new Intent(getContext(), Profile_Activity.class);
        viewProfileIntent.putExtra("user",mUserModel).putExtra("profilemodel",profileModel);
        startActivity(viewProfileIntent);
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
