package apps.raymond.kinect.EventDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.R;

//Todo: On Scroll of messages, animate the information hover bar so that it is hidden and all that
// remains is the information icon on the right panel.
public class EventDetail_Activity extends AppCompatActivity implements
        Messages_Adapter.ProfileClickListener {
    private static final int ATTENDING = 0;
    private static final int INVITED = 1;

    private SimpleDateFormat monthSDF = new SimpleDateFormat("MMM",Locale.getDefault());
    private SimpleDateFormat dateSDF = new SimpleDateFormat("dd",Locale.getDefault());
    private SimpleDateFormat timeSDF = new SimpleDateFormat("h:mm a",Locale.getDefault());
    private EventDetail_ViewModel mViewModel;
    private String mUserID, mEventName;
    private EventMembers_Fragment membersFrag;
    private EventLocation_Fragment locationFrag;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        mUserID = getIntent().getStringExtra("userID");
        mEventName = getIntent().getStringExtra("name");

        mViewModel = ViewModelProviders.of(this).get(EventDetail_ViewModel.class);
        Toolbar toolbar = findViewById(R.id.toolbar_event);
        TextView textName = findViewById(R.id.text_name);
        TextView textDesc = findViewById(R.id.text_description);
        TextView textMonth = findViewById(R.id.text_event_month);
        TextView textDate = findViewById(R.id.text_event_date);
        TextView textTime = findViewById(R.id.text_event_time);
        ImageView imgPrivacy = findViewById(R.id.image_privacy);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_messages);
        ProgressBar mMessagesProgress = findViewById(R.id.progress_loading_messages);
        TextView txtEmptyMessages = findViewById(R.id.text_empty_messages);

        ImageButton btnMessages = findViewById(R.id.button_messages_show);
        ImageButton btnMapView = findViewById(R.id.button_map_show);
        ImageButton btnInviteUser = findViewById(R.id.button_invite_user);
        TextView txtAttending = findViewById(R.id.text_attending_show);
        TextView txtInvited = findViewById(R.id.text_invited_show);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener((View v)->onBackPressed());
        Messages_Adapter mMessageAdapter = new Messages_Adapter(this);
        recyclerView.setAdapter(mMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnMessages.setOnClickListener((View v)->getSupportFragmentManager().popBackStack());
        btnMapView.setOnClickListener((View v)-> {
            v.setBackground(ContextCompat.getDrawable(this,R.drawable.icon_selected_background));
            showLocationMap();
        });
        btnInviteUser.setOnClickListener((View v)->Log.w("EventDetailAct","INVITE USER PROMPT!"));

        txtAttending.setOnClickListener((View  v) -> {
            showMembersPanel(ATTENDING);
            v.setBackground(ContextCompat.getDrawable(this,R.drawable.icon_selected_background));
        });
        txtInvited.setOnClickListener((View v) -> showMembersPanel(INVITED));

        textName.setText(mEventName);
        //Update the information views with the event model retrieved from the data base.
        mViewModel.getEventModel().observe(this,(Event_Model event)->{
            mViewModel.loadEventMessages(event.getName());

            textDesc.setText(event.getDesc());
            txtAttending.setText(String.valueOf(event.getAttending()));
            txtInvited.setText(String.valueOf(event.getInvited()));

            if(event.getLong1()!=0){
                Date date = new Date(event.getLong1());
                textMonth.setText(monthSDF.format(date));
                textDate.setText(dateSDF.format(date));
                textTime.setText(timeSDF.format(date));
            } else {
                textTime.setVisibility(View.GONE);
                Button btnSuggestDate = findViewById(R.id.button_suggest_date);
                btnSuggestDate.setVisibility(View.VISIBLE);
                btnSuggestDate.setOnClickListener((View v)->{
                    Toast.makeText(this,"SUGGEST NEW DATE",Toast.LENGTH_LONG).show();
                });
            }

            switch (event.getPrivacy()){
                case Event_Model.EXCLUSIVE:
                    imgPrivacy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_close_black_24dp));
                    break;
                case Event_Model.PRIVATE:
                    imgPrivacy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_delete_black_24dp));
                    break;
                case Event_Model.PUBLIC:
                    imgPrivacy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_public_black_24dp));
                    break;
            }

            membersFrag = EventMembers_Fragment.newInstance(mEventName);
            locationFrag = new EventLocation_Fragment();
        });

        mViewModel.loadEventModel(mEventName);
        //Load the messages recycler with the retrieved messages.
        mViewModel.getEventMessages().observe(this,(List<Message_Model> messages)->{
            if (messages != null) {
                if(mMessagesProgress.getVisibility()==View.VISIBLE){
                    mMessagesProgress.setVisibility(View.GONE);
                }
                if(messages.size()==0){
                    txtEmptyMessages.setVisibility(View.VISIBLE);
                } else {
                    if(txtEmptyMessages.getVisibility()==View.VISIBLE){
                        txtEmptyMessages.setVisibility(View.GONE);
                    }
                    mMessageAdapter.setData(messages);
                }
            }
        });

        EditText editNewMessage = findViewById(R.id.edit_new_message);
        ImageButton btnPostMessage = findViewById(R.id.button_post_message);
        btnPostMessage.setOnClickListener((View v)->{
            if(editNewMessage.getText().toString().trim().length()>0){
                createMessage(editNewMessage.getText().toString());
                editNewMessage.getText().clear();
                try {
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                } catch (Exception e){
                    //Purposely empty.
                }
            }
        });
    }


    private void createMessage(String messageBody){
        long timeStamp = System.currentTimeMillis();
        final Message_Model newMessage = new Message_Model(mUserID,messageBody,timeStamp);
        mViewModel.postNewMessage(mEventName, newMessage).addOnCompleteListener((Task<Void> task)->{
            if(task.isSuccessful()){
                List<Message_Model> list = mViewModel.getEventMessages().getValue();
                if(list!=null){
                    list.add(newMessage);
                    mViewModel.setEventMessages(list);
                }
            }
        });
    }

    /**
     * Slides the members frame layout into view if it is currently off screen.
     * @param i variable to determine which recycler view to display
     */
    private void showMembersPanel(int i){
        switch (i){
            case ATTENDING:
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack("members")
                        .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right,R.anim.slide_in_right,R.anim.slide_out_right)
                        .replace(R.id.frame_members_fragment,membersFrag,"members")
                        .commit();
                break;
            case INVITED:
                Log.w("EventDetailAct","Show the invited users recycler.");
                break;
        }
    }

    /**
     * Pop the current top fragment from the FragmentManager and add the map fragment to the stack.
     */
    private void showLocationMap(){
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .addToBackStack("location")
                .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right,R.anim.slide_in_right,R.anim.slide_out_right)
                .replace(R.id.frame_members_fragment,locationFrag,"location")
                .commit();
    }



    @Override
    public void loadProfile(String author) {
        //Called when clicking on a message to load the message author's profile.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.event_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.option_leave_event:
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetail_Activity.this);
                builder.setTitle("Warning!")
                        .setMessage(R.string.leave_event_message)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(),"Leaving mEventModel",Toast.LENGTH_LONG).show();
                                mViewModel.leaveEvent(mUserID, mEventName);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(),"Cancel",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.create().show();
                return true;
            default:
                return false;

        }
    }
}
