package apps.raymond.kinect;

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
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.UserProfile.ProfileSettings_Fragment;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.Login.Login_Activity;

/**
 * Activity class that is loaded when we want to view a User's profile. If the activity is loaded
 * with a User_Model belonging to someone other than the currently logged in user, we have to disable
 * the editing function.
 *
 * ToDo:
 *  Add ability to create/delete a connection with the current profile.
 *  Edit the profile picture if the current profile is the currently logged in user.
 */
public class Profile_Activity extends AppCompatActivity implements View.OnClickListener,
        Connections_Fragment.LoadConnections {
    private static final String TAG = "ProfileActivity";
    private static final String CONNECTIONS_FRAG = "ConnectionsFrag";
    private final static int REQUEST_PROFILE_PICTURE = 0;
    private final static int REQUEST_IMAGE_CAPTURE = 1;

    TextView nameTxt, connectionsTxt, interestsTxt;
    ImageButton socialEditLock;
    ImageView profilePic;
    Core_ViewModel viewModel;
    private User_Model mUserModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().hasExtra("personal")){
            Log.w(TAG,"Layout should be user settings layout.");
            setContentView(R.layout.activity_profile_);
            mUserModel = getIntent().getExtras().getParcelable("personal");
            ProfileSettings_Fragment profileFragment = ProfileSettings_Fragment.newInstance(mUserModel);
        } else if(getIntent().hasExtra("user")){
            Log.w(TAG,"Layout should be view public user layout.");
            setContentView(R.layout.activity_view_profile);
            mUserModel = getIntent().getExtras().getParcelable("user");
        }

        viewModel = ViewModelProviders.of(this).get(Core_ViewModel.class);

        ImageButton closeBtn = findViewById(R.id.return_btn);
        closeBtn.setOnClickListener(this);
        ImageButton logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        profilePic = findViewById(R.id.profile_pic);
        profilePic.setOnClickListener(this);

        nameTxt = findViewById(R.id.name_txt);
        nameTxt.setText(mUserModel.getEmail());

        Button connectionsBtn = findViewById(R.id.connections_btn);
        connectionsBtn.setOnClickListener(this);
        Button locationsBtn = findViewById(R.id.locations_btn);
        locationsBtn.setOnClickListener(this);
        Button interestsBtn = findViewById(R.id.interests_btn);
        interestsBtn.setOnClickListener(this);

        connectionsTxt = findViewById(R.id.connections_txt);
        interestsTxt = findViewById(R.id.interests_txt);

        fetchUserInfo();

        socialEditLock = findViewById(R.id.social_edit_lock);
        socialEditLock.setOnClickListener(this);
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
            case R.id.return_btn:
                onBackPressed();
                break;
            case R.id.logout_btn:
                viewModel.signOut(this);
                Intent loginIntent = new Intent(this, Login_Activity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
                break;
            case R.id.profile_pic:
                updateProfilePicture();
                break;
            case R.id.connections_btn:
                Connections_Fragment fragment = new Connections_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.profile_frame,fragment,CONNECTIONS_FRAG)
                        .addToBackStack(CONNECTIONS_FRAG)
                        .commit();
                break;
            case R.id.locations_btn:
                Log.i(TAG," " + getSupportFragmentManager().getFragments().toString());
                break;
            case R.id.interests_btn:
                break;
            case R.id.social_edit_lock:
                break;
        }
    }

    List<User_Model> connectionsList = new ArrayList<>();
    List<String> interestsList = new ArrayList<>();
    private void fetchUserInfo(){
        //READ FROM THE SHARED PREFERENCES HERE!
        /*mViewModel.getConnections().addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<User_Model>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        connectionsList.addAll(task.getResult());
                    }
                    connectionsTxt.setText(String.valueOf(connectionsList.size()));
                } else {
                    Toast.makeText(getBaseContext(),"hello",Toast.LENGTH_LONG).show();
                }
            }
        });*/
        //ToDo This has not been implemented. Need to structure the data in FireStore
        /*mViewModel.fetchInterests().addOnCompleteListener(new OnCompleteListener<List<String>>() {
            @Override
            public void onComplete(@NonNull Task<List<String>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        interestsList.addAll(task.getResult());
                    }
                    interestsTxt.setText(String.valueOf(interestsList.size()));
                } else {
                    Toast.makeText(getContext(),"Error retrieving mUser interests.",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
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

    @Override
    public List<User_Model> loadConnections() {
        return connectionsList;
    }
}
