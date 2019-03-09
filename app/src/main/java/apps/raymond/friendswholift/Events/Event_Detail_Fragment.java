/*
 * Fragment class that displays the details of the Event when user clicks on an Event in the Main's
 * RecyclerView.
 * https://mikescamell.com/shared-element-transitions-part-4-recyclerview/
 * https://github.com/mikescamell/shared-element-transitions/blob/master/app/src/main/java/com/mikescamell/sharedelementtransitions/recycler_view/recycler_view_to_fragment/AnimalDetailFragment.java
 */

package apps.raymond.friendswholift.Events;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.DialogFragments.YesNoDialog;
import apps.raymond.friendswholift.Interfaces.BackPressListener;
import apps.raymond.friendswholift.Interfaces.ProfileClickListener;
import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.Repository_ViewModel;

import static apps.raymond.friendswholift.Core_Activity.YESNO_REQUEST;

public class Event_Detail_Fragment extends Fragment implements
        View.OnClickListener, ProfileClickListener, BackPressListener{

    public static final String TAG = "Event_Detail_Fragment";
    private static final String EVENT_ACCEPTED = "Accepted";
    private static final String EVENT_DECLINED = "Declined";
    private static final int DETAIL_READ = 0;
    private static final int DETAIL_WRITE = 1;

    public Event_Detail_Fragment(){
    }

    public static Event_Detail_Fragment newInstance(){
        return new Event_Detail_Fragment();
    }

    GroupEvent event;
    private Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
        Bundle args = this.getArguments();
        if(args !=null){
            this.event = args.getParcelable("EventObject");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_detail_frag,container,false);
    }

    ViewFlipper editFlipper, profilesFlipper;
    TextInputEditText nameEdit, descEdit;
    List<String> invitedProfiles, declinedProfiles, acceptedProfiles;
    ProfileRecyclerAdapter invitedAdapter, declinedAdapter, acceptedAdapter;
    ProgressBar acceptedBar,invitedBar,declinedBar,updateBar;
    TextView eventName,eventDesc,eventStart,eventEnd;
    TextView acceptedNullText,invitedNullText,declinedNullText, startTxt, endTxt;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editFlipper = view.findViewById(R.id.event_edit_flipper);
        eventName = view.findViewById(R.id.event_title);
        eventDesc = view.findViewById(R.id.event_desc);
        eventStart = view.findViewById(R.id.event_start);
        eventEnd = view.findViewById(R.id.event_end);
        // These are variables that are only required if the user is able to edit the event.
        if(event.getCreator().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            nameEdit = view.findViewById(R.id.event_name_edit);
            descEdit = view.findViewById(R.id.event_desc_edit);
            startTxt = view.findViewById(R.id.event_start_write);
            endTxt = view.findViewById(R.id.event_end_write);
            updateBar = view.findViewById(R.id.update_progress_bar);
            Button editSaveBtn = view.findViewById(R.id.save_event_btn);
            editSaveBtn.setOnClickListener(this);
        }
        updateViews();
        Button acceptedBtn = view.findViewById(R.id.accepted_profiles_btn);
        Button declinedBtn = view.findViewById(R.id.declined_profiles_btn);
        Button invitedBtn = view.findViewById(R.id.invited_profiles_btn);

        acceptedBtn.setOnClickListener(this);
        declinedBtn.setOnClickListener(this);
        invitedBtn.setOnClickListener(this);

        profilesFlipper = view.findViewById(R.id.profiles_flipper);

        /*
         * For each recycler, simply populate the Recycler with a list of the profile names for each respective category.
         * When user clicks on a user, load the full Profile using the name in the list as our query field.
         */
        RecyclerView acceptedRecycler = view.findViewById(R.id.accepted_recycler);
        acceptedAdapter = new ProfileRecyclerAdapter(invitedProfiles,this);
        acceptedRecycler.setAdapter(acceptedAdapter);
        Log.d(TAG,"Calling accepted list.");
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
            case R.id.save_event_btn:
                Log.i(TAG,"Saving edits to Event: "+ event.getName());
                editEvent();
                break;
            case R.id.accepted_profiles_btn:
                profilesFlipper.setDisplayedChild(0);
                break;
            case R.id.declined_profiles_btn:
                profilesFlipper.setDisplayedChild(1);
                break;
            case R.id.invited_profiles_btn:
                profilesFlipper.setDisplayedChild(2);
                break;
        }
    }

    /*
     * OnClick call returns the Original name of the clicked profile. We then use this to query Firebase
     * to retrieve the entire profile to load.
    */
    @Override
    public void onProfileClick(String profileName) {
        Log.i(TAG,"Clicked on: "+profileName);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_edit).setVisible(true);
        menu.findItem(R.id.action_edit).setEnabled(true);
        menu.findItem(R.id.action_profile).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i){
            case R.id.action_edit:
                Log.i(TAG,"Editing event: "+event.getName());

                nameEdit.setText(event.getName(), TextView.BufferType.EDITABLE);
                descEdit.setText(event.getDesc(), TextView.BufferType.EDITABLE);

                item.setVisible(false);
                item.setEnabled(false);

                editFlipper.showNext();
        }
        return super.onOptionsItemSelected(item);
    }

    //CALL REPOSITORY METHOD TO RETRIEVE THE LIST HERE!!!!!!!
    private void getInviteList(final GroupEvent event){
        viewModel.getEventInvitees(event).addOnCompleteListener(new OnCompleteListener<List<String>>() {
            @Override
            public void onComplete(@NonNull Task<List<String>> task) {
                invitedBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        invitedNullText.setVisibility(View.VISIBLE);
                    }
                    Log.i(TAG,"Retrieved list of invited invitedProfiles.");
                    invitedProfiles = new ArrayList<>();
                    invitedProfiles.addAll(task.getResult());
                    invitedAdapter.setData(invitedProfiles);
                    invitedAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error when retrieving guest list.",e);
                Toast.makeText(getContext(),"Failed to retrieve guest list for "+event.getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateViews(){
        eventName.setText(event.getName());
        eventDesc.setText(event.getDesc());
        String eventStartTxt = event.getMonth() + ", " + event.getDay();
        String eventEndTxt = "EVENT END Haven't implemented yet :(";
        eventStart.setText(eventStartTxt);
        eventEnd.setText(eventEndTxt);
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

    private void getAcceptedList(GroupEvent groupEvent){
        Log.i(TAG,"Attempting to get query of accepted users!");
        viewModel.getEventResponses(groupEvent, EVENT_ACCEPTED).addOnCompleteListener(new OnCompleteListener<List<String>>() {
            @Override
            public void onComplete(@NonNull Task<List<String>> task) {
                if(task.getResult().isEmpty()){
                    acceptedNullText.setVisibility(View.VISIBLE);
                }
                acceptedBar.setVisibility(View.INVISIBLE);
                acceptedProfiles = new ArrayList<>();
                acceptedProfiles.addAll(task.getResult());
                acceptedAdapter.setData(acceptedProfiles);
                acceptedAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error when retrieving guest list.",e);
                Toast.makeText(getContext(),"Failed to accepted users for "+event.getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDeclinedList(GroupEvent groupEvent){
        Log.i(TAG,"Attempting to get query of accepted users!");
        viewModel.getEventResponses(groupEvent, EVENT_DECLINED).addOnCompleteListener(new OnCompleteListener<List<String>>() {
            @Override
            public void onComplete(@NonNull Task<List<String>> task) {
                if(task.getResult().isEmpty()){
                    declinedNullText.setVisibility(View.VISIBLE);
                }
                declinedBar.setVisibility(View.INVISIBLE);
                declinedProfiles = new ArrayList<>();
                declinedProfiles.addAll(task.getResult());
                declinedAdapter.setData(declinedProfiles);
                declinedAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error when retrieving guest list.",e);
                Toast.makeText(getContext(),"Failed to accepted users for "+event.getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPress() {
        int i = editFlipper.getDisplayedChild();
        switch(i){
            case DETAIL_READ:
                Log.i(TAG,"Detail currently in read mode.");
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case DETAIL_WRITE:
                YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
                yesNoDialog.setCancelable(false);
                yesNoDialog.setTargetFragment(Event_Detail_Fragment.this,YESNO_REQUEST);
                yesNoDialog.show(getActivity().getSupportFragmentManager(),null);
        }
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
}
