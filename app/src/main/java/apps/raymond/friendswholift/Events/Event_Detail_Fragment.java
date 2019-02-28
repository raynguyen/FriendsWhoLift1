/*
 * Fragment class that displays the details of the Event when user clicks on an Event in the Main's
 * RecyclerView.
 * https://mikescamell.com/shared-element-transitions-part-4-recyclerview/
 * https://github.com/mikescamell/shared-element-transitions/blob/master/app/src/main/java/com/mikescamell/sharedelementtransitions/recycler_view/recycler_view_to_fragment/AnimalDetailFragment.java
 */

package apps.raymond.friendswholift.Events;

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
import android.widget.ImageButton;
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

import apps.raymond.friendswholift.Interfaces.ProfileClickListener;
import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.UserProfile.ProfileRecyclerAdapter;

public class Event_Detail_Fragment extends Fragment implements
        View.OnClickListener, ProfileClickListener {
    private static final String TAG = "Event_Detail_Fragment";
    private static final String EVENT_ACCEPTED = "Accepted";
    private static final String EVENT_DECLINED = "Declined";

    public Event_Detail_Fragment(){
    }

    public static Event_Detail_Fragment newInstance(){
        return new Event_Detail_Fragment();
    }

    GroupEvent event;
    EventViewModel eventViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        eventViewModel = new EventViewModel(); // mGroupViewModel = ViewModelProviders.of(requireActivity()).get(GroupsViewModel.class);
        Bundle args = this.getArguments();
        if(args !=null){
            this.event = args.getParcelable("EventObject");
        }
        Log.i(TAG,"Creating detail event fragment for: "+ event.getName());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_detail_frag,container,false);
    }

    ViewFlipper editFlipper, profilesFlipper;
    TextInputEditText nameEdit, descEdit, monthEdit, dayEdit;
    List<String> invitedProfiles, declinedProfiles, acceptedProfiles;
    ProfileRecyclerAdapter invitedAdapter, declinedAdapter, acceptedAdapter;
    ProgressBar acceptedBar,invitedBar,declinedBar;
    TextView acceptedNullText,invitedNullText,declinedNullText;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editFlipper = view.findViewById(R.id.event_edit_flipper);

        if(event.getCreator().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            nameEdit = view.findViewById(R.id.event_name_edit);
            descEdit = view.findViewById(R.id.event_desc_edit);
            monthEdit = view.findViewById(R.id.event_month_edit);
            dayEdit = view.findViewById(R.id.event_day_edit);
        }

        TextView eventName = view.findViewById(R.id.event_title);
        TextView eventDesc = view.findViewById(R.id.event_desc);
        TextView eventMonth = view.findViewById(R.id.event_start);
        TextView eventDay = view.findViewById(R.id.event_start);

        ImageButton editSaveBtn = view.findViewById(R.id.save_event_btn);
        editSaveBtn.setOnClickListener(this);

        eventName.setText(event.getName());
        eventDesc.setText(event.getDesc());
        eventMonth.setText(event.getMonth());
        eventDay.setText(event.getDay());

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
                editFlipper.showPrevious();

                getActivity().invalidateOptionsMenu();
                event.setName(nameEdit.getText().toString());
                event.setDesc(descEdit.getText().toString());
                event.setMonth(monthEdit.getText().toString());
                event.setDay(dayEdit.getText().toString());
                eventViewModel.createEvent(event);
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
        menu.findItem(R.id.action_edit_event).setVisible(true);
        menu.findItem(R.id.action_profile).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i){
            case R.id.action_edit_event:
                Log.i(TAG,"Editing event: "+event.getName());
                item.setVisible(false);
                item.setEnabled(false);
                editFlipper.showNext();
        }
        return super.onOptionsItemSelected(item);
    }

    //CALL REPOSITORY METHOD TO RETRIEVE THE LIST HERE!!!!!!!
    private void getInviteList(final GroupEvent event){
        eventViewModel.getEventInvitees(event).addOnCompleteListener(new OnCompleteListener<List<String>>() {
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

    private void getAcceptedList(GroupEvent groupEvent){
        Log.i(TAG,"Attempting to get query of accepted users!");
        eventViewModel.getEventResponses(groupEvent, EVENT_ACCEPTED).addOnCompleteListener(new OnCompleteListener<List<String>>() {
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
        eventViewModel.getEventResponses(groupEvent, EVENT_DECLINED).addOnCompleteListener(new OnCompleteListener<List<String>>() {
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



}
