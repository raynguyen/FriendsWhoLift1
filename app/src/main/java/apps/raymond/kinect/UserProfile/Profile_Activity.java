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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apps.raymond.kinect.MapsPackage.Locations_MapFragment;
import apps.raymond.kinect.ImageBroadcastReceiver;
import apps.raymond.kinect.MapsPackage.Location_Model;
import apps.raymond.kinect.ObjectModels.User_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.ProfileActivity_ViewModel;

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
public class Profile_Activity extends AppCompatActivity implements
        Locations_MapFragment.MapCardViewClick, Profile_Fragment.ViewProfileInterface {
    public static final String CURRENT_USER = "current_user"; //Mandatory field to determine connection controls required.
    public static final String PROFILE_MODEL = "profile_model";
    public static final String PROFILE_ID = "profile_id";
    private static final String TAG = "ProfileActivity";
    private final static int REQUEST_PROFILE_PICTURE = 0;
    private final static int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView profilePic;
    private ProfileActivity_ViewModel mViewModel;
    private User_Model mUserModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mViewModel = ViewModelProviders.of(this).get(ProfileActivity_ViewModel.class);

        try {
            /*
             * We will always require the current user's model when viewing any profile. The model is
             * used to determine what functionality is required of this activity (i.e. removing/adding a
             * connection to the profile we are viewing, or enabling modification of the user's settings).
             */
            mUserModel = getIntent().getExtras().getParcelable(CURRENT_USER);
            mViewModel.setUserModel(mUserModel);
        } catch (NullPointerException npe) {
            Log.w(TAG, "SOME ERROR WHEN TRYING TO GET CURRENT USER MODEL.");
        }

        if (getIntent().hasExtra(PROFILE_ID)){
            /*
            We have been passed an intent with a String representing a User's ID. Check to see
            if the ID matches the current user, if yes we want to inflate the Fragment to handle
            profile settings. Otherwise inflate the Fragment to view another user's profile.

            This situation comes from clicking on a user from an EventDetail_Activity.
             */
            String profileID = getIntent().getStringExtra(PROFILE_ID);
            String userID = mUserModel.getEmail();
            if(profileID.equals(userID)){
                PersonalProfile_Fragment fragment = new PersonalProfile_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_profile, fragment)
                        .commit();
            } else {
                Profile_Fragment fragment = Profile_Fragment.newInstance(null, profileID);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_profile, fragment)
                        .commit();
            }
        }else if(getIntent().hasExtra(PROFILE_MODEL)){
            /*
             * If this activity instance if created with the PROFILE_MODEL extra, we have the model
             * required to inflate the Profile_Fragment for the provided User_Model extra.
             */
            User_Model profileModel = getIntent().getParcelableExtra(PROFILE_MODEL);
            Profile_Fragment fragment = Profile_Fragment.newInstance(profileModel, null);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_profile,fragment)
                    .commit();
        } else if (getIntent().hasExtra(CURRENT_USER)){
            /*
             * Execute the code below when the Profile_Activity is passed the current user's model.
             *
             * This occurs when we are inflating the Profile_Activity from the Core_Activity.
             */
            PersonalProfile_Fragment profileFragment = new PersonalProfile_Fragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_profile,profileFragment)
                    .commit();
        }
    }

    @Override
    public void inflateProfileFragment(User_Model profileModel) {
        Log.w(TAG,"Inflate a new fragment for a profile.");
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
        //In the activity result, use the ViewModel held by THIS ACTIVITY (ProfileActivity_ViewModel)
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
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    /*
        When creating the chooserIntent, we want to create a file to save the photo if the mUser selects
        the camera option. How do we create a file only if the mUser selects the camera option.
        -Potential solution is to simply create file regardless of the choice but then we have to delete
        the file if the mUser does not take a photo > how do we determine if mUser selects the gallery option?
         */
    private Uri photoUri;
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
