package apps.raymond.kinect;

import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apps.raymond.kinect.UserProfile.ProfileSettings_Fragment;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.Login.Login_Activity;
import apps.raymond.kinect.UserProfile.ViewProfile_Fragment;
import apps.raymond.kinect.ViewModels.Profile_ViewModel;

/**
 * Activity class that is loaded when we want to view a User's profile. If the activity is loaded
 * with a User_Model belonging to someone other than the currently logged in user, we have to disable
 * the editing function.
 *
 * ToDo:
 *  Add ability to create/delete a connection with the current profile.
 *  Edit the profile picture if the current profile is the currently logged in user.
 */
public class Profile_Activity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "ProfileActivity";
    private static final String CONNECTIONS_FRAG = "ConnectionsFrag";
    private final static int REQUEST_PROFILE_PICTURE = 0;
    private final static int REQUEST_IMAGE_CAPTURE = 1;

    TextView txtName, txtConnectionsNum, txtInterestsNum, txtLocationsNum;
    Button btnConnections, btnLocations, btnInterests;
    ImageView profilePic;
    User_Model mUserModel,mProfileModel;
    String mUserID, mProfileID;
    private Profile_ViewModel mViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mViewModel = ViewModelProviders.of(this).get(Profile_ViewModel.class);
        mUserModel = getIntent().getExtras().getParcelable("user");
        mUserID = mUserModel.getEmail();
        Log.w(TAG,"mUserID "+mUserID);

        txtName = findViewById(R.id.text_profile_name);
        txtConnectionsNum = findViewById(R.id.text_connections_count);
        txtInterestsNum = findViewById(R.id.text_interests_count);
        txtLocationsNum = findViewById(R.id.text_locations_count);

        if(getIntent().hasExtra("profilemodel")){
            Log.w(TAG,"We are passed a profile model as well as user.");
            mProfileModel = getIntent().getExtras().getParcelable("profilemodel");
            mProfileID = mProfileModel.getEmail();
            mViewModel.checkForConnection(mUserID,mProfileID)
                    .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if(task.getResult()){
                                //DELETE BUTTON
                            } else {
                                //ADD BUTTON
                            }
                        }
                    });
            txtName.setText(mProfileModel.getEmail());
            txtConnectionsNum.setText(String.valueOf(mProfileModel.getNumconnections()));
            txtInterestsNum.setText(String.valueOf(mProfileModel.getNuminterests()));
            txtLocationsNum.setText(String.valueOf(mProfileModel.getNumlocations()));

            ImageButton btnDeleteConnection = findViewById(R.id.button_remove_connection);
            btnDeleteConnection.setOnClickListener(this);
            btnDeleteConnection.setVisibility(View.VISIBLE);
            ViewProfile_Fragment viewFragment = ViewProfile_Fragment.newInstance(mUserModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_profilefragment,viewFragment)
                    .commit();
        } else {
            Log.w(TAG,"We are NOT passed a profile model as well as user.");
            txtName.setText(mUserID);
            txtConnectionsNum.setText(String.valueOf(mUserModel.getNumconnections()));
            txtInterestsNum.setText(String.valueOf(mUserModel.getNuminterests()));
            txtLocationsNum.setText(String.valueOf(mUserModel.getNumlocations()));

            ImageButton btnLogout = findViewById(R.id.button_logout);
            btnLogout.setOnClickListener(this);
            btnLogout.setVisibility(View.VISIBLE);
            ProfileSettings_Fragment profileFragment = ProfileSettings_Fragment.newInstance(mUserModel);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_profilefragment,profileFragment)
                    .commit();
        }

        ImageButton btnReturn = findViewById(R.id.button_return);
        btnReturn.setOnClickListener(this);

        btnConnections = findViewById(R.id.button_connections);
        btnConnections.setOnClickListener(this);
        btnLocations = findViewById(R.id.button_locations);
        btnLocations.setOnClickListener(this);
        btnInterests = findViewById(R.id.button_interests);
        btnInterests.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.button_return:
                onBackPressed();
                break;
            case R.id.button_logout:
                mViewModel.signOut();
                Intent loginIntent = new Intent(this, Login_Activity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
                break;
            case R.id.button_remove_connection:
                Log.w(TAG,"REMOVE THE CONNECTION WITH THIS USER!");
                break;
            case R.id.profile_pic:
                //updateProfilePicture();
                break;
            case R.id.button_connections:
                Connections_Fragment fragment = Connections_Fragment.newInstance(mUserModel);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_down,R.anim.slide_out_up,R.anim.slide_in_down,R.anim.slide_out_up)
                        .replace(R.id.frame_profile,fragment,CONNECTIONS_FRAG)
                        .addToBackStack(CONNECTIONS_FRAG)
                        .commit();
                break;
            case R.id.button_locations:
                Log.i(TAG," " + getSupportFragmentManager().getFragments().toString());
                break;
            case R.id.button_interests:
                break;
        }
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

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Log.w(TAG,"THE RESULT IS OK!");
            switch (requestCode){
                case REQUEST_PROFILE_PICTURE:
                    Log.i(TAG,"Successfully fetched photo.");
                    //setProfilePic();
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    profilePic.setImageBitmap(imageBitmap);
                    break;
            }
        }
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
