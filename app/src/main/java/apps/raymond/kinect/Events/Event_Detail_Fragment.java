/*
 * Fragment class that displays the details of the Event when user clicks on an Event in the Main's
 * RecyclerView.
 * https://mikescamell.com/shared-element-transitions-part-4-recyclerview/
 * https://github.com/mikescamell/shared-element-transitions/blob/master/app/src/main/java/com/mikescamell/sharedelementtransitions/recycler_view/recycler_view_to_fragment/AnimalDetailFragment.java
 */

package apps.raymond.kinect.Events;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.Invite_Users_Fragment;
import apps.raymond.kinect.ProfileRecyclerAdapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.View_Profile_Activity;

import static apps.raymond.kinect.Core_Activity.YESNO_REQUEST;

public class Event_Detail_Fragment extends Fragment implements
        View.OnClickListener, ProfileRecyclerAdapter.ProfileClickListener, BackPressListener{

    public static final String TAG = "Event_Detail_Fragment";
    public static final String EVENT = "EventObject";
    private static final String EVENT_ACCEPTED = "Accepted";
    private static final String EVENT_DECLINED = "Declined";
    private static final int DETAIL_READ = 0;
    private static final int DETAIL_WRITE = 1;

    public Event_Detail_Fragment(){
    }

    //ToDo: The event should be passed as an argument in newInstance!!!!!
    public static Event_Detail_Fragment newInstance(Event_Model event){
        Event_Detail_Fragment fragment = new Event_Detail_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(EVENT,event);
        fragment.setArguments(args);
        return fragment;
    }

    private String currUser;
    private Event_Model event;
    private Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = this.getArguments();
        if(args !=null){
            this.event = args.getParcelable(EVENT);
        }

        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
        currUser = FirebaseAuth.getInstance().getCurrentUser().getEmail(); //ToDo: This should be returned by repository.
        fetchUserList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_event_detail_,container,false);
    }

    SearchView toolbarSearch;
    ViewGroup informationLayout, usersLayout;
    ViewFlipper editFlipper, profilesFlipper;
    TextInputEditText nameEdit, descEdit;
    List<User_Model> invitedProfiles,declinedProfiles, acceptedProfiles;
    ProfileRecyclerAdapter invitedAdapter, declinedAdapter, acceptedAdapter;
    ProgressBar acceptedBar,invitedBar,declinedBar,updateBar;
    TextView textName, textDesc, textMonth, textDate, textTime;
    TextView acceptedNullText,invitedNullText,declinedNullText, startEdit, endEdit;
    TextView acceptedCount, declinedCount, invitedCount;
    String creator;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbarSearch = requireActivity().findViewById(R.id.toolbar_search);
        toolbarSearch.setVisibility(View.GONE);

        informationLayout = view.findViewById(R.id.layout_information);
        textName = view.findViewById(R.id.text_name);
        textName.setText(event.getName());
        textDesc = view.findViewById(R.id.text_description);
        textDesc.setText(event.getDesc());

        textMonth = view.findViewById(R.id.text_month);
        textDate = view.findViewById(R.id.text_date);
        textTime = view.findViewById(R.id.text_time);
        convertLongToDate();

        usersLayout = view.findViewById(R.id.layout_users);
        Button btnViewUsers = view.findViewById(R.id.button_view_users);
        btnViewUsers.setOnClickListener(this);

        updateBar = view.findViewById(R.id.update_progress_bar);
        creator = event.getCreator();

        updateViews();
        Button acceptedBtn = view.findViewById(R.id.button_accepted_users);
        Button declinedBtn = view.findViewById(R.id.button_declined_users);
        Button invitedBtn = view.findViewById(R.id.button_invited_users);
        acceptedCount = view.findViewById(R.id.accepted_count_txt);
        declinedCount = view.findViewById(R.id.declined_count_txt);
        invitedCount = view.findViewById(R.id.invited_count_txt);

        acceptedBtn.setOnClickListener(this);
        declinedBtn.setOnClickListener(this);
        invitedBtn.setOnClickListener(this);

        profilesFlipper = view.findViewById(R.id.profiles_flipper);

        setMemberRecyclers(view);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    private void setMemberRecyclers(View view){
        /*
         * For each recycler, simply populate the Recycler with a list of the profile names for each respective category.
         * When user clicks on a user, load the full Profile using the name in the list as our query field.
         */
        RecyclerView acceptedRecycler = view.findViewById(R.id.accepted_recycler);
        acceptedAdapter = new ProfileRecyclerAdapter(invitedProfiles,this);
        acceptedRecycler.setAdapter(acceptedAdapter);
        getAcceptedList(event);
        acceptedRecycler.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
        acceptedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        acceptedBar = view.findViewById(R.id.accepted_progress_bar);
        acceptedNullText = view.findViewById(R.id.accepted_null_data_text);

        RecyclerView declinedRecycler = view.findViewById(R.id.declined_recycler);
        declinedAdapter = new ProfileRecyclerAdapter(invitedProfiles,this);
        declinedRecycler.setAdapter(declinedAdapter);
        getDeclinedList(event);
        declinedRecycler.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
        declinedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        declinedBar = view.findViewById(R.id.declined_progress_bar);
        declinedNullText = view.findViewById(R.id.declined_null_data_text);

        RecyclerView invitedRecycler = view.findViewById(R.id.invited_recycler);
        invitedAdapter = new ProfileRecyclerAdapter(invitedProfiles, this);
        getInviteList(event);
        invitedRecycler.setAdapter(invitedAdapter);
        invitedRecycler.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
        invitedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        invitedBar = view.findViewById(R.id.invited_progress_bar);
        invitedNullText = view.findViewById(R.id.invited_null_data_text);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.button_accepted_users:
                profilesFlipper.setDisplayedChild(0);
                break;
            case R.id.button_declined_users:
                profilesFlipper.setDisplayedChild(1);
                break;
            case R.id.button_invited_users:
                profilesFlipper.setDisplayedChild(2);
                break;
            case R.id.button_view_users:
                if(usersLayout.getVisibility()!=View.VISIBLE){
                    usersLayout.setVisibility(View.VISIBLE);
                } else {
                    usersLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    /**
     * Converts event long field to a date field and populates the appropriate views.
     */
    private void convertLongToDate(){
        long long1 = event.getLong1();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(long1);
        textMonth.setText(new SimpleDateFormat("MMM",Locale.getDefault()).format(c.getTime()));
        textDate.setText(String.valueOf(c.get(Calendar.DATE)));
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a",Locale.getDefault());
        textTime.setText(sdf.format(new Date(long1)));
    }

    @Override
    public void onProfileClick(User_Model userModel) {
        Intent viewProfileIntent = new Intent(requireActivity(), View_Profile_Activity.class);
        viewProfileIntent.putExtra(View_Profile_Activity.USER,userModel);
        startActivity(viewProfileIntent);
    }

    MenuItem saveAction, editAction;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG,"onCreateOptionsMenu of detailed group fragment.");
        menu.clear();
        inflater.inflate(R.menu.details_menu,menu);
        editAction = menu.findItem(R.id.action_edit);
        saveAction = menu.findItem(R.id.action_save);

        if(creator.equals(currUser)){
            editAction.setEnabled(true);
            editAction.setVisible(true);
        } else {
            editAction.setVisible(false);
            editAction.setEnabled(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Going to handle events in the Activity because not sure why it doesn't work in the fragment.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i){
            case R.id.action_edit:
                setEditView();
                return true;
            case R.id.action_save:
                Log.i(TAG,"Overwriting the event object.");
                item.setVisible(false);
                item.setEnabled(false);
                editAction.setVisible(true);
                editAction.setEnabled(true);

                editEvent();
                editFlipper.setDisplayedChild(0);
                //Todo: Return a task when updating so that we can display the progress bar.

                //Todo: Have to add the ability to modify the image.
                return true;
            case R.id.action_invite:
                Fragment usersInviteFragment = Invite_Users_Fragment.newInstance(userModelArrayList);
                getFragmentManager().beginTransaction()
                        .add(R.id.core_frame,usersInviteFragment, Core_Activity.INVITE_USERS_FRAG)
                        .addToBackStack(Core_Activity.INVITE_USERS_FRAG)
                        .commit();
                return true;
        }
        return false;
    }

    private void setEditView(){
        nameEdit.setText(event.getName());
        descEdit.setText(event.getDesc());
        startEdit.setText("TEMP");
        endEdit.setText("TEMP");

        editAction.setVisible(false);
        editAction.setEnabled(false);
        saveAction.setEnabled(true);
        saveAction.setVisible(true);
        editFlipper.setDisplayedChild(1);
    }

    private void updateViews(){
        textName.setText(event.getName());
        //eventDesc.setText(event.getDesc());
    }

    private void editEvent(){
        updateBar.setVisibility(View.VISIBLE);
        event.setName(nameEdit.getText().toString());
        event.setDesc(descEdit.getText().toString());

        viewModel.updateEvent(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"Successfully updated the event in Firestore.");
                    updateBar.setVisibility(View.INVISIBLE);
                    getActivity().invalidateOptionsMenu();
                    updateViews();
                    editFlipper.showPrevious();
                } else {
                    Log.w(TAG,"Error updating event: "+task.getException());
                    Toast.makeText(getContext(),"Failed to update event.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getInviteList(final Event_Model event){
        viewModel.getEventInvitees(event).addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<User_Model>> task) {
                invitedBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        invitedNullText.setVisibility(View.VISIBLE);
                    }
                    invitedProfiles = new ArrayList<>();
                    invitedProfiles.addAll(task.getResult());
                    invitedCount.setText("" + invitedProfiles.size());
                    invitedAdapter.setData(invitedProfiles);
                    invitedAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error fetching invited list.",e);
                Toast.makeText(getContext(),"Failed to retrieve guest list for "+event.getName(),Toast.LENGTH_SHORT).show();
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
                acceptedBar.setVisibility(View.INVISIBLE);
                acceptedProfiles = new ArrayList<>();
                acceptedProfiles.addAll(task.getResult());
                acceptedCount.setText(String.format(Locale.getDefault(),"%s",acceptedProfiles.size()));
                acceptedAdapter.setData(acceptedProfiles);
                acceptedAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error fetching accepted list.",e);
                Toast.makeText(getContext(),"Failed to accepted users for "+event.getName(),Toast.LENGTH_SHORT).show();
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
                declinedBar.setVisibility(View.INVISIBLE);
                declinedProfiles = new ArrayList<>();
                declinedProfiles.addAll(task.getResult());
                declinedCount.setText(""+declinedProfiles.size());
                declinedAdapter.setData(declinedProfiles);
                declinedAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                declinedCount.setText("--");
                Log.w(TAG,"Error fetching declined list.",e);
                Toast.makeText(getContext(),"Failed to accepted users for "+event.getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPress() {
        getActivity().getSupportFragmentManager().popBackStack();
        toolbarSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case YESNO_REQUEST:
                if(resultCode == YesNoDialog.POS_RESULT){
                    Log.i(TAG,"Returned from YesNoDialog with positive click.");
                    getActivity().invalidateOptionsMenu();
                    editFlipper.setDisplayedChild(DETAIL_READ);
                } else {
                    Log.i(TAG,"Cancel click.");
                    // Do nothing.
                }
                break;
        }
    }

    ArrayList<User_Model> userModelArrayList;
    private void fetchUserList(){
        viewModel.fetchUsers().addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<User_Model>> task) {
                Log.i(TAG,"Fetched list of users.");
                if(task.isSuccessful()){
                    if(task.getResult().size()>0){
                        userModelArrayList = new ArrayList<>(task.getResult());
                    }
                }
            }
        });
    }
}
