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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    ViewFlipper viewFlipper;
    TextInputEditText nameEdit, descEdit, monthEdit, dayEdit;
    ArrayList<String> profiles;
    ProfileRecyclerAdapter acceptedAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewFlipper = view.findViewById(R.id.event_edit_flipper);

        if(event.getCreator().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            nameEdit = view.findViewById(R.id.event_name_edit);
            descEdit = view.findViewById(R.id.event_desc_edit);
            monthEdit = view.findViewById(R.id.event_month_edit);
            dayEdit = view.findViewById(R.id.event_day_edit);
        }

        TextView eventName = view.findViewById(R.id.event_title);
        TextView eventDesc = view.findViewById(R.id.event_desc);
        TextView eventMonth = view.findViewById(R.id.event_month);
        TextView eventDay = view.findViewById(R.id.event_day);

        ImageButton editSaveBtn = view.findViewById(R.id.save_event_btn);
        editSaveBtn.setOnClickListener(this);

        eventName.setText(event.getName());
        eventDesc.setText(event.getDesc());
        eventMonth.setText(event.getMonth());
        eventDay.setText(event.getDay());

        /*
         * The profiles list is null when it is passed into the Adapter so no views are created.
         * How do I fix this?
         */

        RecyclerView acceptedRecycler = view.findViewById(R.id.accepted_recycler);
        acceptedAdapter = new ProfileRecyclerAdapter(profiles, this);
        getInviteList();
        acceptedRecycler.setAdapter(acceptedAdapter);
        acceptedRecycler.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
        acceptedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.save_event_btn:
                Log.i(TAG,"Saving edits to Event: "+ event.getName());
                viewFlipper.showPrevious();

                getActivity().invalidateOptionsMenu();
                event.setName(nameEdit.getText().toString());
                event.setDesc(descEdit.getText().toString());
                event.setMonth(monthEdit.getText().toString());
                event.setDay(dayEdit.getText().toString());
                eventViewModel.createEvent(event);
        }
    }

    /*
     * OnClick call returns the Original name of the clicked profile. We then use this to query Firebase
     * to retrieve the entire profile to load.
    */
    @Override
    public void onProfileClick() {
        Log.i(TAG,"Clicked on a profile.");
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
                viewFlipper.showNext();
        }
        return super.onOptionsItemSelected(item);
    }

    //CALL REPOSITORY METHOD TO RETRIEVE THE LIST HERE!!!!!!!
    private void getInviteList(){



        eventViewModel.getEventInvitees(event).addOnCompleteListener(new OnCompleteListener<List<String>>() {
            @Override
            public void onComplete(@NonNull Task<List<String>> task) {
                if(task.isSuccessful()){
                    /*
                    profiles = new ArrayList<>();
                    profiles.add("hello");
                    profiles.add("fewa");
                    profiles.add("fewafweafwe");
                    profiles.add("hgreagaerge");
                    Log.i(TAG,"SETTING CONTENTS OF PROFILES LIST.");
                    acceptedAdapter.setData(profiles);
                    */

                    Log.i(TAG,"Retrieved list of invited profiles.");
                    profiles = new ArrayList<>();
                    profiles.addAll(task.getResult());
                    Log.i(TAG,"Contents of profiles list: "+profiles.toString());
                    acceptedAdapter.setData(profiles);
                    acceptedAdapter.notifyDataSetChanged();
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

    /*
     * Recycler view with tablayout
     * Tablayouts are: Attending, maybe, invited
     * The recyclerview will only load the User display name and their user profile image
     * If the user clicks on a user, the whole profile for the user loads in a separate fragment.
     *
     *
     *
     * POPULATE EACH RECYCLER VIEW
     */
}
