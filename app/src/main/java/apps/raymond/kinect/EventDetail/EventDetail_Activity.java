package apps.raymond.kinect.EventDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.Profile_Activity;
import apps.raymond.kinect.ObjectModels.User_Model;

//Todo: On Scroll of messages, animate the information hover bar so that it is hidden and all that
// remains is the information icon on the right panel.
public class EventDetail_Activity extends AppCompatActivity implements
        Messages_Adapter.MessageClickListener {
    private SimpleDateFormat monthSDF = new SimpleDateFormat("MMM",Locale.getDefault());
    private SimpleDateFormat dateSDF = new SimpleDateFormat("dd",Locale.getDefault());
    private SimpleDateFormat timeSDF = new SimpleDateFormat("h:mm a",Locale.getDefault());
    private Drawable mSelectedIcon;
    private Drawable mBtnRipple;
    private View mFocusedView;
    private ImageButton btnMessages;
    private EventDetail_ViewModel mViewModel;
    private String mUserID, mEventName;
    private EventMembers_Fragment membersFrag;
    private EventLocation_Fragment locationFrag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        mViewModel = ViewModelProviders.of(this).get(EventDetail_ViewModel.class);

        TextView textEventName = findViewById(R.id.text_event_name);
        if(getIntent().hasExtra("event_name")){
            mEventName = getIntent().getStringExtra("event_name");
            textEventName.setText(mEventName);
        } else {
            //Some error pertaining to loading the event.
        }

        User_Model mUserModel = getIntent().getParcelableExtra("user");


        if(mUserModel!=null){
            mViewModel.setUserModel(mUserModel);
            mUserID = mUserModel.getEmail();
        }

        mSelectedIcon = ContextCompat.getDrawable(this,R.drawable.icon_selected_background);
        mBtnRipple = ContextCompat.getDrawable(this,R.drawable.button_ripple);

        Toolbar toolbar = findViewById(R.id.toolbar_event);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener((View v)->onBackPressed());
        Messages_Adapter mMessageAdapter = new Messages_Adapter(this);


        TextView textDesc = findViewById(R.id.text_description);
        ViewGroup viewDate = findViewById(R.id.layout_event_date);
        TextView textMonth = findViewById(R.id.text_event_month);
        TextView textDate = findViewById(R.id.text_event_date);
        TextView textTime = findViewById(R.id.text_event_time);
        TextView textDateSuggest = findViewById(R.id.text_date_suggest);
        LinearLayout viewDetails = findViewById(R.id.layout_details);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_messages);
        ProgressBar mMessagesProgress = findViewById(R.id.progress_loading_messages);
        TextView txtEmptyMessages = findViewById(R.id.text_empty_messages);

        btnMessages = findViewById(R.id.button_messages_show);
        ImageButton btnMapView = findViewById(R.id.button_map_show);
        TextView txtAttending = findViewById(R.id.text_attending_show);
        TextView txtInvited = findViewById(R.id.text_invited_show);
        ImageView imgPrivacy = findViewById(R.id.image_privacy);

        recyclerView.setAdapter(mMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewDate.setOnClickListener((View v)->Log.w("EventDetailAct","Clicked on the layout to suggest a date!"));

        mFocusedView = btnMessages;
        textDateSuggest.setOnClickListener((View v)->Log.w("EventDetail","Suggest new date."));
        btnMessages.setOnClickListener(this::switchDetailFragment);
        btnMapView.setOnClickListener(this::switchDetailFragment);
        txtAttending.setOnClickListener(this::switchDetailFragment);
        txtInvited.setOnClickListener(this::switchDetailFragment);


        //Update the information views with the event model retrieved from the data base.
        mViewModel.getEventModel().observe(this,(Event_Model event)->{
            if(event!=null){
                textDesc.setText(event.getDesc());
                txtAttending.setText(String.valueOf(event.getAttending()));
                txtInvited.setText(String.valueOf(event.getInvited()));

                switch (event.getPrivacy()){
                    case Event_Model.EXCLUSIVE:
                        imgPrivacy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_lock_outline_black_24dp));
                        break;
                    case Event_Model.PRIVATE:
                        imgPrivacy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vpn_lock_black_24dp));
                        break;
                    case Event_Model.PUBLIC:
                        imgPrivacy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_public_black_24dp));
                        break;
                }

                mViewModel.loadEventMessages(event.getName());

                if(event.getLong1()!=0) {
                    Date date = new Date(event.getLong1());
                    textMonth.setText(monthSDF.format(date));
                    textDate.setText(dateSDF.format(date));
                    textTime.setText(timeSDF.format(date));
                    textTime.setVisibility(View.VISIBLE);
                } else {
                    textDateSuggest.setVisibility(View.VISIBLE);
                }

                membersFrag = EventMembers_Fragment.newInstance(mEventName);
                locationFrag = new EventLocation_Fragment();
            }
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
                postMessage(editNewMessage.getText().toString());
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

    /**
     *
     * @param v the view of which we want to highlight as the icon for the displayed content.
     */
    private void switchDetailFragment(View v){
        if(mFocusedView==v){
            return;
        }

        mFocusedView.setBackground(mBtnRipple);
        v.setBackground(mSelectedIcon);
        mFocusedView = v;

        getSupportFragmentManager().popBackStack();
        switch (v.getId()){
            case R.id.button_messages_show:
                //Do nothing
                break;
            case R.id.button_map_show:
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack("location")
                        .setCustomAnimations(R.anim.slide_in_down,R.anim.slide_out_down,R.anim.slide_in_down,R.anim.slide_out_down)
                        .replace(R.id.frame_members_fragment,locationFrag,"location")
                        .commit();
                break;
            case R.id.text_attending_show:
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack("members")
                        .setCustomAnimations(R.anim.slide_in_down,R.anim.slide_out_down,R.anim.slide_in_down,R.anim.slide_out_down)
                        .replace(R.id.frame_members_fragment,membersFrag,"members")
                        .commit();
                break;
        }
    }

    /**
     *
     * @param messageBody text of the message to be posted to the event.
     */
    private void postMessage(String messageBody){
        User_Model userModel = mViewModel.getUserModel().getValue();
        if(userModel!=null){
            String userName = userModel.getName() + " " + userModel.getName2();
            long timeStamp = System.currentTimeMillis();
            final Message_Model newMessage = new Message_Model(userName, mUserID, messageBody, timeStamp);
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

    }

    @Override
    public void loadAuthorProfile(String author) {
        //Called when clicking on a message to load the message author's profile.
        Toast.makeText(this,"Clicked on " + author + "'s message.",Toast.LENGTH_LONG).show();
        Intent viewProfileIntent = new Intent(this, Profile_Activity.class);
        viewProfileIntent.putExtra(Profile_Activity.PROFILE_ID, author);
        startActivity(viewProfileIntent);
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

    @Override
    public void onBackPressed() {
        int i = getSupportFragmentManager().getFragments().size();
        super.onBackPressed();
        if(i>1){
            btnMessages.performClick();
        }

    }
}
