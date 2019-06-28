package apps.raymond.kinect.UserProfile;

import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.MapsPackage.Locations_MapFragment;
import apps.raymond.kinect.ImageBroadcastReceiver;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.StartUp.Login_Activity;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;

/**
 * Activity class that is loaded when we want to view a User's profile. If the activity is loaded
 * with a User_Model belonging to someone other than the currently logged in user, we have to disable
 * the editing function.
 *
 * User the below tag to see what Fragments are active in this Activity.
 * Log.i(TAG," " + getSupportFragmentManager().getFragments().toString());
 * ToDo:
 *  Add ability to create/delete a connection with the current profile.
 *  Edit the profile picture if the current profile is the currently logged in user.
 */
public class Profile_Activity extends AppCompatActivity implements YesNoDialog.YesNoCallback,
        Locations_MapFragment.MapCardViewClick {
    public static final String CURRENT_USERMODEL = "current_user"; //Mandatory field to determine connection controls required.
    public static final String USER_PROFILEMODEL = "profile_model";
    public static final String FETCH_MODEL = "fetch_model";
    public static final String PROFILE_ID = "profile_id";

    private static final String TAG = "ProfileActivity";
    private static final String CONNECTIONS_FRAG = "ConnectionsFrag";
    private static final String LOCATIONS_FRAG = "LocationsFrag";
    private final static int REQUEST_PROFILE_PICTURE = 0;
    private final static int REQUEST_IMAGE_CAPTURE = 1;

    Button btnConnections, btnLocations, btnInterests;
    private ImageButton btnConnect, btnDeleteConnection;
    private ProgressBar pbProfileAction;
    ImageView profilePic;
    private Profile_ViewModel mViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mViewModel = ViewModelProviders.of(this).get(Profile_ViewModel.class);

        /*
         * We will always require the current user's model when viewing any profile. The model is
         * used to determine what functionality is required of this activity (i.e. removing/adding a
         * connection to the profile we are viewing, or enabling modification of the user's settings).
         */
        try{
            User_Model userModel = getIntent().getExtras().getParcelable(CURRENT_USERMODEL);
            mViewModel.setUserModel(userModel);
        } catch (NullPointerException npe){
            Log.w(TAG,"SOME ERROR WHEN TRYING TO GET CURRENT USER MODEL.");
            //Immediately show some error?
            //Consider a static newInstance method that takes a User_Model parameter to ensure that
            //all instances of Profile_Activity start with the current user's User_Model.
        }

        TextView txtName = findViewById(R.id.text_profile_name);
        final TextView txtConnectionsNum = findViewById(R.id.text_connections_count);
        final TextView txtLocationsNum = findViewById(R.id.text_locations_count);
        final TextView txtInterestsNum = findViewById(R.id.text_interests_count);

        pbProfileAction = findViewById(R.id.progress_profile_action);
        btnConnect = findViewById(R.id.button_connect);
        btnConnect.setOnClickListener((View v)->{
            v.setVisibility(View.GONE);
            pbProfileAction.setVisibility(View.VISIBLE);
            if(mViewModel.getProfileModel().getValue()!=null){
                String userID = mViewModel.getUserModel().getValue().getEmail();
                User_Model profileModel = mViewModel.getProfileModel().getValue();
                mViewModel.createUserConnection(userID,profileModel)
                        .addOnCompleteListener((Task<Void> task2)-> {
                            pbProfileAction.setVisibility(View.INVISIBLE);
                            if(task2.isSuccessful()){
                                btnDeleteConnection.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(this,"Error. Please try again shortly.",Toast.LENGTH_LONG).show();
                                btnConnect.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        btnDeleteConnection = findViewById(R.id.button_delete_connection);
        btnDeleteConnection.setOnClickListener((View v)->{
            if(mViewModel.getProfileModel()!=null){
                String profileID = mViewModel.getProfileModel().getValue().getEmail();
                YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,
                        YesNoDialog.DELETE_CONNECTION + " " + profileID + "?");
                yesNoDialog.setCancelable(false);
                yesNoDialog.show(getSupportFragmentManager(),null);
            }
        });


        /*
         * We bind an observer to the ViewModel's ProfileModel. Whenever the LiveData's value becomes
         * non-null, we know we are viewing the profile of another user. We then query the database
         * for a connection with the current user.
         */
        mViewModel.getProfileModel().observe(this,(User_Model profileModel)->{
            String profileID = profileModel.getEmail();
            if(profileModel.getname()!=null && profileModel.getName2()!=null){
                String name = profileModel.getname() + " " + profileModel.getName2();
                txtName.setText(name);
            } else {
                txtName.setText(profileID);
            }

            txtConnectionsNum.setText(String.valueOf(profileModel.getNumconnections()));
            txtInterestsNum.setText(String.valueOf(profileModel.getNuminterests()));
            txtLocationsNum.setText(String.valueOf(profileModel.getNumlocations()));

            String userID = mViewModel.getUserModel().getValue().getEmail();
            mViewModel.checkForConnection(userID,profileID).addOnCompleteListener((Task<Boolean> task)->{
                if(task.getResult()){
                    btnDeleteConnection.setVisibility(View.VISIBLE);
                } else {
                    btnConnect.setVisibility(View.VISIBLE);
                }
            });

        });

        if(getIntent().hasExtra(PROFILE_ID)){
            /*
             * Check to determine if we are required to fetch the document for the profile of which we
             * are viewing. The FETCH_MODEL extra is currently only passed with the intent via
             * EventDetail_Activity when a post is clicked.
             */
            Log.w(TAG,"We have been given a PROFILE_ID");
            String profileID = getIntent().getStringExtra(PROFILE_ID);
            mViewModel.loadProfileModel(profileID);
        } else if(getIntent().hasExtra(USER_PROFILEMODEL)){
            /*
             * We call execute the following code if the ProfileActivity is started with a Profile
             * User_Model. We then set the User_Model extra to this activity's ViewModel and trigger
             * the observer defined above to determine which views must be inflated for this instance
             * of the activity.
             */
            User_Model profileModel = getIntent().getParcelableExtra(USER_PROFILEMODEL);
            mViewModel.setProfileModel(profileModel);
            //Fragment that holds details regarding a User's profile.
            ViewProfile_Fragment viewFragment = ViewProfile_Fragment.newInstance(profileModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_profilefragment,viewFragment)
                    .commit();
        } else {
            /*
             * We execute the code below when the Profile_Activity is passed neither a profile model
             * or profile id. This means that we have only been given the current user's User_Model
             * and therefore can inflate the settings fragment.
             */
            User_Model userModel = mViewModel.getUserModel().getValue();
            String name = userModel.getname() + " " + userModel.getName2();
            if(name!=null){
                txtName.setText(name);
            } else {
                txtName.setText(userModel.getEmail());
            }

            txtConnectionsNum.setText(String.valueOf(userModel.getNumconnections()));
            txtInterestsNum.setText(String.valueOf(userModel.getNuminterests()));
            txtLocationsNum.setText(String.valueOf(userModel.getNumlocations()));

            //Fragment that shows the User's current settings and preferences.
            ProfileSettings_Fragment profileFragment = ProfileSettings_Fragment.newInstance(userModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_profilefragment,profileFragment)
                    .commit();

            ImageButton btnLogout = findViewById(R.id.button_logout);
            btnLogout.setVisibility(View.VISIBLE);
            btnLogout.setOnClickListener((View v)->{
                mViewModel.signOut();
                Intent loginIntent = new Intent(this, Login_Activity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
            });
        }

        ImageButton btnReturn = findViewById(R.id.button_return);
        btnReturn.setOnClickListener((View v)->onBackPressed());
        btnConnections = findViewById(R.id.button_connections);
        btnConnections.setOnClickListener((View v)->{
            Connections_Fragment fragment;
            if(mViewModel.getProfileModel().getValue()!=null){
                    /*
                    There has been no profile model set to the view model and therefore we are viewing
                    the current user's Profile Activity and want to pass the current user's model
                    (perhaps the ID?) in order to load the current user's connections.
                     */
                User_Model profileModel = mViewModel.getProfileModel().getValue();
                fragment = Connections_Fragment.newInstance(profileModel);
            } else {
                User_Model userModel = mViewModel.getUserModel().getValue();
                fragment = Connections_Fragment.newInstance(userModel);
            }

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_down,R.anim.slide_out_up,R.anim.slide_in_down,R.anim.slide_out_up)
                    .replace(R.id.frame_profile,fragment,CONNECTIONS_FRAG)
                    .addToBackStack(CONNECTIONS_FRAG)
                    .commit();
        });
        btnLocations = findViewById(R.id.button_locations);
        btnLocations.setOnClickListener((View v)->{
            Locations_MapFragment mapFragment;
            if(mViewModel.getProfileModel().getValue()!=null){
                    /*
                    There has been no profile model set to the view model and therefore we are viewing
                    the current user's Profile Activity and want to pass the current user's model
                    (perhaps the ID?) in order to load the current user's connections.
                     */
                String profileID = mViewModel.getProfileModel().getValue().getEmail();
                mapFragment = Locations_MapFragment.newInstance(profileID,Locations_MapFragment.EVENT_PROFILE);
            } else {
                String userID = mViewModel.getUserModel().getValue().getEmail();
                mapFragment = Locations_MapFragment.newInstance(userID,Locations_MapFragment.EVENT_PROFILE);
            }
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_down,R.anim.slide_out_up,R.anim.slide_in_down,R.anim.slide_out_up)
                    .replace(R.id.frame_profile,mapFragment,LOCATIONS_FRAG)
                    .addToBackStack(LOCATIONS_FRAG)
                    .commit();
        });
        btnInterests = findViewById(R.id.button_interests);
        btnInterests.setOnClickListener((View v)->{
            Toast.makeText(this,"This feature has not been implemented.", Toast.LENGTH_LONG).show();
        });

    }


    /**
     * Interface implementation for when the user clicks the positive button on the DeleteConnection
     * dialog fragment.
     */
    @Override
    public void onDialogPositive() {
        pbProfileAction.setVisibility(View.VISIBLE);
        btnDeleteConnection.setVisibility(View.GONE);
        String userID = mViewModel.getUserModel().getValue().getEmail();
        String profileID = mViewModel.getProfileModel().getValue().getEmail();
        mViewModel.deleteUserConnection(userID, profileID).addOnCompleteListener((Task<Void> task)-> {
            pbProfileAction.setVisibility(View.INVISIBLE);
            if(task.isSuccessful()){
                btnConnect.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this,"Error deleting user.",Toast.LENGTH_LONG).show();
                btnDeleteConnection.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Callback interface when user opts to save a new Location (from Locations_MapFragment) for
     * future usage.
     * @param location The address class of which we will create a Location_Model to store to DB.
     */
    @Override
    public void onCardViewPositiveClick(Location_Model location) {
        //ToDo:CHECK IF THE LOCATION EXISTS IN THE USER'S LOCATIONSET, IF YES PROMPT TO ADD, IF NO PROMOPT TO DELETE
        //If the prompt returns a positive click, increment the Text on locations button after we
        //increment number of locations in the user document.
        //In the activity result, use the ViewModel held by THIS ACTIVITY (Profile_ViewModel)
        Log.w(TAG,"PROMPT TO ADD A LOOKUP NAME");
        location.setLookup("TEMPLOOKUP");
        User_Model userModel = mViewModel.getUserModel().getValue();
        String userID = userModel.getEmail();
        mViewModel.addLocationToUser(userID,location).addOnCompleteListener((Task<Void> task)->{
            if(task.isSuccessful()){
                Toast.makeText(getBaseContext(),"Successfully saved location.",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_PROFILE_PICTURE){
                Log.i(TAG,"Successfully fetched photo.");
                //setProfilePic();
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                profilePic.setImageBitmap(imageBitmap);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
    }

    /*
        When creating the chooserIntent, we want to create a file to save the photo if the mUser selects
        the camera option. How do we create a file only if the mUser selects the camera option.
        -Potential solution is to simply create file regardless of the choice but then we have to delete
        the file if the mUser does not take a photo > how do we determine if mUser selects the gallery option?
         */
    Uri photoUri;
    private void updateProfilePicture(){
        Intent receiver = new Intent(this, ImageBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                REQUEST_PROFILE_PICTURE,receiver,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent getPictureIntent = new Intent();
        getPictureIntent.setType("image/*");
        getPictureIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent chooserIntent = Intent.createChooser(getPictureIntent,getString(R.string.imageDialogTitle),pendingIntent.getIntentSender());
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent});

        startActivityForResult(chooserIntent,REQUEST_PROFILE_PICTURE);

        //Intent.resolveActivity returns the Activity component to execute the intent.
        /*if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch(IOException | NullPointerException exception){
                Log.w(TAG,"Error creating photo file.", exception);
            }
            if(photoFile!=null){
                photoUri = FileProvider.getUriForFile(requireContext(),
                        "apps.raymond.kinect.provider", photoFile);
                if(photoUri!=null){
                    //Note that by providing a Uri via putExtra method, onActivityResult will not return to us a bitmap of the photo taken.
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    return;
                }
                Log.w(TAG,"photoUri is null!");
            } else {
                Log.w(TAG,"Null file.");
            }
        }*/
    }

    //Create a file that will hold the photo taken by the camera intent.
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir);      /* directory */
    }

    private void setProfilePic(){
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),photoUri);
            if(bitmap!=null){
                Log.i(TAG,"GOT THE FULL SIZED IMAGE!");
                profilePic.setImageBitmap(bitmap);
            }
        }
        catch (IOException | NullPointerException e){
            Log.w(TAG,"Error retrieving full size image.",e);
        }
    }

}

/*
*     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //if mProfileModel is null we should inflate the logout button.
        //Use a ViewModel to store the clicked UserModel/Location depending on the inflated fragment and determine what MenuItems need to be inflated.
        getMenuInflater().inflate(R.menu.menu_profile_activity,menu);
        if(mProfileModel==null){
            menu.findItem(R.id.action_connect).setEnabled(false).setVisible(false);
            menu.findItem(R.id.action_delete_connection).setEnabled(false).setVisible(false);
        } else {
            menu.findItem(R.id.action_logout).setEnabled(false).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                return true;
            case R.id.action_add_location:
                return true;
            case R.id.action_connect:
                return true;
            case R.id.action_delete_connection:
                return true;
        }
        return false;
    }*/
