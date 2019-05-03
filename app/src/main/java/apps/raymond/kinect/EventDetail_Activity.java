package apps.raymond.kinect;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.UserProfile.User_Model;

public class EventDetail_Activity extends AppCompatActivity implements View.OnClickListener,
        ProfileRecyclerAdapter.ProfileClickListener{
    private static final String TAG = "EventDetail";
    public static final String EVENT = "Event";
    private static final String EVENT_ACCEPTED = "Accepted";
    private static final String EVENT_DECLINED = "Declined";

    public static void init(Event_Model event, Context context){
        Intent intent = new Intent(context, EventDetail_Activity.class);
        intent.putExtra(EVENT, event);
        context.startActivity(intent);
    }

    Repository_ViewModel viewModel;
    Event_Model event;
    ViewGroup informationLayout, usersLayout;
    ViewFlipper profilesFlipper;
    List<User_Model> invitedProfiles,declinedProfiles,acceptedProfiles;
    ProfileRecyclerAdapter invitedAdapter, declinedAdapter, acceptedAdapter;
    ProgressBar acceptedBar,invitedBar,declinedBar,updateBar;
    TextView textName, textHost, textDesc, textMonth, textDate, textTime;
    TextView acceptedNullText,invitedNullText,declinedNullText;
    TextView acceptedCount, declinedCount, invitedCount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_);
        Intent initIntent = getIntent();
        event = initIntent.getParcelableExtra(EVENT);

        viewModel = ViewModelProviders.of(this).get(Repository_ViewModel.class);

        informationLayout = findViewById(R.id.layout_information);
        textName = findViewById(R.id.text_name);
        textName.setText(event.getName());
        textHost = findViewById(R.id.text_host);
        String hostString = getString(R.string.host) + " " + event.getCreator();
        textHost.setText(hostString);
        textDesc = findViewById(R.id.text_description);
        textDesc.setText(event.getDesc());

        textMonth = findViewById(R.id.text_month);
        textDate = findViewById(R.id.text_date);
        textTime = findViewById(R.id.text_time);
        convertLongToDate();

        usersLayout = findViewById(R.id.layout_users);
        Button btnViewUsers = findViewById(R.id.button_view_users);
        btnViewUsers.setOnClickListener(this);

        updateBar = findViewById(R.id.update_progress_bar);

        Button acceptedBtn = findViewById(R.id.button_accepted_users);
        Button declinedBtn = findViewById(R.id.button_declined_users);
        Button invitedBtn = findViewById(R.id.button_invited_users);
        acceptedCount = findViewById(R.id.accepted_count_txt);
        declinedCount = findViewById(R.id.declined_count_txt);
        invitedCount = findViewById(R.id.invited_count_txt);

        acceptedBtn.setOnClickListener(this);
        declinedBtn.setOnClickListener(this);
        invitedBtn.setOnClickListener(this);

        profilesFlipper = findViewById(R.id.profiles_flipper);

        setMemberRecyclers(findViewById(android.R.id.content));
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
        textMonth.setText(new SimpleDateFormat("MMM", Locale.getDefault()).format(c.getTime()));
        textDate.setText(String.valueOf(c.get(Calendar.DATE)));
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a",Locale.getDefault());
        textTime.setText(sdf.format(new Date(long1)));
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
        acceptedRecycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        acceptedRecycler.setLayoutManager(new LinearLayoutManager(this));
        acceptedBar = view.findViewById(R.id.accepted_progress_bar);
        acceptedNullText = view.findViewById(R.id.accepted_null_data_text);

        RecyclerView declinedRecycler = view.findViewById(R.id.declined_recycler);
        declinedAdapter = new ProfileRecyclerAdapter(invitedProfiles,this);
        declinedRecycler.setAdapter(declinedAdapter);
        getDeclinedList(event);
        declinedRecycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        declinedRecycler.setLayoutManager(new LinearLayoutManager(this));
        declinedBar = view.findViewById(R.id.declined_progress_bar);
        declinedNullText = view.findViewById(R.id.declined_null_data_text);

        RecyclerView invitedRecycler = view.findViewById(R.id.invited_recycler);
        invitedAdapter = new ProfileRecyclerAdapter(invitedProfiles, this);
        getInviteList(event);
        invitedRecycler.setAdapter(invitedAdapter);
        invitedRecycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        invitedRecycler.setLayoutManager(new LinearLayoutManager(this));
        invitedBar = view.findViewById(R.id.invited_progress_bar);
        invitedNullText = view.findViewById(R.id.invited_null_data_text);
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
                acceptedBar.setVisibility(View.INVISIBLE);
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
                declinedBar.setVisibility(View.INVISIBLE);
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
        Intent viewProfileIntent = new Intent(this, View_Profile_Activity.class);
        viewProfileIntent.putExtra(View_Profile_Activity.USER,userModel);
        startActivity(viewProfileIntent);
    }
}
