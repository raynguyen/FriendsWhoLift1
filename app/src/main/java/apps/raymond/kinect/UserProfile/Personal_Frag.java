/*
ToDo:
When a mutable field is selected, compare the change to the original value and if they are different,
store the new value in the respective field. Other option is to compare the old user model object
to a new temporary one and if they are not equal in all aspects, overwrite the existing user model.
 */

package apps.raymond.kinect.UserProfile;

import android.Manifest;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.ImageBroadcastReceiver;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;

import static android.app.Activity.RESULT_OK;

public class Personal_Frag extends Fragment implements View.OnClickListener{
    private final static String TAG = "PersonalFragment";
    private final static String USER = "UserModel";
    private final static int REQUEST_PROFILE_PICTURE = 0;
    private final static int REQUEST_IMAGE_CAPTURE = 1;

    ProfileFragInt destroyInterface;
    public interface ProfileFragInt {
        void destroyProfileFrag();
        void signOut();
    }

    public static Personal_Frag newInstance(UserModel userModel){
        Personal_Frag frag = new Personal_Frag();
        Bundle args = new Bundle();
        args.putParcelable(USER,userModel);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Core_Activity){
            try {
                destroyInterface = (ProfileFragInt) context;
            } catch (ClassCastException e){
                Log.i(TAG,"Core_Group_Fragment does not implement UpdateGroupRecycler interface.");
            }
        }
    }

    Repository_ViewModel viewModel;
    UserModel userModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel = getArguments().getParcelable(USER);
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.personal_fragment,container,false);
    }

    TextView nameTxt, connectionsTxt, interestsTxt;
    ImageButton socialEditLock;
    ImageView profilePic;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton closeBtn = view.findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(this);
        ImageButton logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        profilePic = view.findViewById(R.id.profile_pic);
        profilePic.setOnClickListener(this);

        nameTxt = view.findViewById(R.id.name_txt);
        nameTxt.setText(userModel.getEmail());

        Button connectionsBtn = view.findViewById(R.id.connections_btn);
        connectionsBtn.setOnClickListener(this);
        Button interestsBtn = view.findViewById(R.id.interests_btn);
        interestsBtn.setOnClickListener(this);

        connectionsTxt = view.findViewById(R.id.connections_text);
        interestsTxt = view.findViewById(R.id.interests_text);

        fetchUserInfo();

        socialEditLock = view.findViewById(R.id.social_edit_lock);
        socialEditLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.close_btn:
                destroyInterface.destroyProfileFrag();
                break;
            case R.id.logout_btn:
                destroyInterface.signOut();
                break;
            case R.id.social_edit_lock:
                editSocialSettings();
                break;
            case R.id.connections_btn:
                Log.i(TAG,"Hello");
                break;
            case R.id.interests_btn:
                Log.i(TAG,"Goodbye");
                break;
            case R.id.profile_pic:
                updateProfilePicture();
                break;


        }
    }

    /*
    Consider retrieving all data required for this fragment in the calling activity and passing as
    a bundle to a static method newInstance().
     */
    List<UserModel> connectionsList = new ArrayList<>();
    List<String> interestsList = new ArrayList<>();
    private void fetchUserInfo(){
        //READ FROM THE SHARED PREFERENCES HERE!

        viewModel.fetchConnections().addOnCompleteListener(new OnCompleteListener<List<UserModel>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserModel>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        connectionsList.addAll(task.getResult());
                    }
                    connectionsTxt.setText(String.valueOf(connectionsList.size()));
                } else {
                    Toast.makeText(getContext(),"Error retrieving connections.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ToDo This has not been implemented. Need to structure the data in FireStore
        /*viewModel.fetchInterests().addOnCompleteListener(new OnCompleteListener<List<String>>() {
            @Override
            public void onComplete(@NonNull Task<List<String>> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        interestsList.addAll(task.getResult());
                    }
                    interestsTxt.setText(String.valueOf(interestsList.size()));
                } else {
                    Toast.makeText(getContext(),"Error retrieving user interests.",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    private void editSocialSettings(){
        socialEditLock.setImageResource(R.drawable.baseline_lock_open_black_18dp);
    }

    Uri photoUri;
    private void updateProfilePicture(){
        Intent receiver = new Intent(requireContext(), ImageBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(),
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
                case REQUEST_IMAGE_CAPTURE:
                    Log.i(TAG,"Successfully captured photo.");

                    setProfilePic();
                    // If we remove putExtra from the intent, we are returned a bitmap from the camera.
                /*try {
                    Bitmap imageBitMap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                    profilePic.setImageBitmap(imageBitMap);
                } catch (IOException | NullPointerException e){
                    Log.w(TAG,"Error retrieving bitmap.",e);
                }*/
                    break;
                case REQUEST_PROFILE_PICTURE:
                    Log.i(TAG,"HELLOHELHELHELHAHEARHAEHR");
                    break;
            }
        }
    }

    private void setProfilePic(){
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),photoUri);
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