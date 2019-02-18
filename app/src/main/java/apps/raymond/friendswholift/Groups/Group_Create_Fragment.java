/*
 * ToDo:
 * When the user hits the confirm button, we want to send the user back to the MyGroups screen with
 * an updated RecyclerView that includes the new group.
 */

package apps.raymond.friendswholift.Groups;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import apps.raymond.friendswholift.Core_Activity;
import apps.raymond.friendswholift.DialogFragments.YesNoDialog;
import apps.raymond.friendswholift.R;

public class Group_Create_Fragment extends Fragment implements View.OnClickListener,
        YesNoDialog.YesNoInterface {
    private static final String TAG = "Group_Create_Fragment";
    private static final int IMAGE_REQUEST_CODE = 1;

    public String groupName, descText;
    public Uri imageUri;

    Spinner invite_Spinner;
    EditText desc_Txt;
    private TextInputEditText name_Txt;
    private FirebaseUser currentUser;
    private GroupsViewModel mGroupViewModel;
    private ImageView imageView;
    private AlertDialog imgAlert;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.group_new_frag,container,false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mGroupViewModel = ViewModelProviders.of(getActivity()).get(GroupsViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button discard_Btn = view.findViewById(R.id.discard_grp_btn);
        Button create_Btn = view.findViewById(R.id.create_grp_btn);
        discard_Btn.setOnClickListener(this);
        create_Btn.setOnClickListener(this);

        name_Txt = view.findViewById(R.id.group_name_txt);
        desc_Txt = view.findViewById(R.id.desc_txt);

        invite_Spinner = view.findViewById(R.id.invite_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.array_invite_authorize,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        invite_Spinner.setAdapter(adapter);

        ImageButton cameraBtn = view.findViewById(R.id.camera_button);
        cameraBtn.setOnClickListener(this);

        imageView = view.findViewById(R.id.image_view);

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i){
            case R.id.create_grp_btn:
                // Want to show a dialog with a recap of the group and get user to confirm.
                if(TextUtils.isEmpty(name_Txt.getText().toString())) {
                    name_Txt.setError("Must specify a name.");
                    return;
                }
                groupName = name_Txt.getText().toString();
                descText = desc_Txt.getText().toString();
                DialogFragment dialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.CONFIRM_GROUP);
                dialog.setTargetFragment(this, 0);
                dialog.show(getActivity().getSupportFragmentManager(),"confirmation_dialog");
                break;
            case R.id.discard_grp_btn:
                DialogFragment discardDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
                discardDialog.setTargetFragment(this, 0);
                discardDialog.show(getActivity().getSupportFragmentManager(),"confirmation_dialog");
                break;
            case R.id.camera_button:
                // Launch the camera activity here. The user should be allowed to choose from camera result or from storage.
                getImage();
                break;
            case R.id.camera_img:
                Log.i(TAG,"Starting intent for launch camera.");
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
                break;
            case R.id.gallery_img:
                Log.i(TAG,"Starting intent to load Gallery.");
                Intent getPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getPictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
                getPictureIntent.setType("image/*");
                startActivityForResult(getPictureIntent,IMAGE_REQUEST_CODE);
                break;
            case R.id.google_img:
                Log.i(TAG,"Loading google images for user.");
                break;
        }
    }

    private void getImage(){
        View imgDialogView;
        imgAlert = new AlertDialog.Builder(getContext())
                .setTitle("Image Selector")
                .setCancelable(true)
                .create();
        imgAlert.setCanceledOnTouchOutside(true);

        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            imgDialogView = getLayoutInflater().inflate(R.layout.image_alert_dialog,null);
        } else {
            // Prompt dialog without camera option.
            imgDialogView = getLayoutInflater().inflate(R.layout.image_alert_dialog_nc,null);
        }

        imgAlert.setView(imgDialogView);
        imgAlert.show();

        // Button set-up:
        ImageView camera_btn = imgDialogView.findViewById(R.id.camera_img);
        ImageView gallery_btn = imgDialogView.findViewById(R.id.gallery_img);
        ImageView google_btn = imgDialogView.findViewById(R.id.google_img);
        camera_btn.setOnClickListener(this);
        gallery_btn.setOnClickListener(this);
        google_btn.setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        imgAlert.dismiss();
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.i(TAG,"Image URI retrieved from user selection.");
            if(data != null){
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(imageView);
            }
        }
    }

    @Override
    public void positiveClick() {
        // Add option for photo, if photo is taken, create the group, add image to storage, when the
        // add image to storage is complete, take the uri and add it to the group's field.
        final GroupBase groupBase = new GroupBase(groupName, descText, currentUser.getUid(),"public","owner", null);
        if(imageUri!=null){
            //SAVE THE PHOTO INTO FIREBASESTORAGE.
            //IF THIS IS CALLED WE THEN NEED TO TAKE THE URI FROM STORAGE AND SAVE IT INTO THE GROUPBASE OBJECT.
            //Need a confirmation dialog before we add the photo to FireStore.
            mGroupViewModel.uploadImage(imageUri, groupName)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"Successfully uploaded image.");
                    //Want to return the storage URI not the device's document URI!!!!!!
                    groupBase.setImageURI(imageUri);
                    mGroupViewModel.createGroup(groupBase);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG,"Error uploading image.",e);
                    Toast.makeText(getContext(),"Error uploading image.",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mGroupViewModel.createGroup(groupBase);
        }
        Log.i(TAG,"Created a new GroupBase object with name " + groupBase.getName());
    }

    @Override
    public void negativeClick() {
        Toast.makeText(getContext(),"Clicked on the negative button", Toast.LENGTH_SHORT).show();
    }

}

