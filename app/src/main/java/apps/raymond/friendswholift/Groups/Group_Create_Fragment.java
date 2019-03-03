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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import apps.raymond.friendswholift.Core_Activity;
import apps.raymond.friendswholift.DialogFragments.YesNoDialog;
import apps.raymond.friendswholift.R;

public class Group_Create_Fragment extends Fragment implements View.OnClickListener,
        Core_Activity.BackPressInterface {
    public static final String TAG = "Group_Create_Fragment";
    private static final int IMAGE_REQUEST_CODE = 11;
    private static final int CAMERA_REQUEST_CODE = 12;
    private static final int DIALOG_REQUEST_CODE = 21;

    public Uri imageUri;

    private EditText desc_Txt;
    private TextInputEditText name_Txt;
    private FirebaseUser currentUser;
    private GroupsViewModel mGroupViewModel;
    private ImageView imageView;
    private AlertDialog imgAlert;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.group_create_frag,container,false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mGroupViewModel = ViewModelProviders.of(getActivity()).get(GroupsViewModel.class);
        return view;
    }

    RadioButton privacyBtn;
    RadioGroup privacyGroup;
    Spinner invite_Spinner;
    ProgressBar progressBar;
    TextView progressText;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button discard_Btn = view.findViewById(R.id.discard_grp_btn);
        Button create_Btn = view.findViewById(R.id.create_grp_btn);
        discard_Btn.setOnClickListener(this);
        create_Btn.setOnClickListener(this);

        progressBar = view.findViewById(R.id.creation_progress);
        progressText = view.findViewById(R.id.creation_txt);

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
        privacyGroup = view.findViewById(R.id.privacy_buttons);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i){
            case R.id.create_grp_btn:
                // Want to show a dialog with a recap of the group and get user to confirm.
                if(fieldsCheck()) {
                    Toast.makeText(getContext(),"Mandatory fields must be completed.",Toast.LENGTH_SHORT).show();
                    return;
                }
                createGroup();
                break;
            case R.id.discard_grp_btn:
                getActivity().onBackPressed();
                break;
            case R.id.camera_button:
                getImage();
                break;
        }
    }

    /*
     * Creates a dialog for the user to determine how they want to retrieve an image. If device is
     * equipped with a camera, the dialog shown will provide UI with a camera option.
     */
    private void getImage(){
        View imgDialogView;
        if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            imgDialogView = getLayoutInflater().inflate(R.layout.image_alert_dialog,null);
        } else {
            // Prompt dialog without camera option.
            imgDialogView = getLayoutInflater().inflate(R.layout.image_alert_dialog_nc,null);
        }

        imgAlert = new AlertDialog.Builder(getContext())
                .setTitle("Image Selector")
                .setCancelable(true)
                .create();
        imgAlert.setCanceledOnTouchOutside(true);

        imgAlert.setView(imgDialogView);
        imgAlert.show();

        ImageView camera_btn = imgDialogView.findViewById(R.id.camera_img);
        ImageView gallery_btn = imgDialogView.findViewById(R.id.gallery_img);
        ImageView google_btn = imgDialogView.findViewById(R.id.google_img);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Log.i(TAG,"Starting intent for launch camera.");
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }

            }
        });
        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Starting intent to load Gallery.");
                Intent getPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getPictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
                getPictureIntent.setType("image/*");
                startActivityForResult(getPictureIntent,IMAGE_REQUEST_CODE);
            }
        });
        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Loading google images for user.");
                Toast.makeText(getContext(),"Not yet implemented; allow users to google an image for result.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * Method called to check if the group creation form is filled correctly. If there are any
     * errors, the method returns true.
     */
    private boolean fieldsCheck(){
        boolean check = false;
        if(name_Txt.getText() == null || name_Txt.length()<=1){
            Log.i(TAG,"Empty or single character event names are not allowed.");
            name_Txt.setError("Must be at minimum 2 characters long.");
            check = true;
        }
        if(privacyGroup.getCheckedRadioButtonId() == -1){
            //some sort of red highlighting if no checked button
            Log.i(TAG,"No button checked for privacy.");
            check = true;
        }
        if(check){
            Toast.makeText(getContext(),"Group creation form has not been completed.",Toast.LENGTH_SHORT).show();
        }
        return check;
    }

    @Override
    public void backPress() {
        Log.i(TAG,"BACK PRESSED");
        YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
        yesNoDialog.setTargetFragment(this, DIALOG_REQUEST_CODE);
        yesNoDialog.show(getActivity().getSupportFragmentManager(),null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case DIALOG_REQUEST_CODE:
                if(resultCode == YesNoDialog.POS_RESULT){
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Log.i(TAG,"Resuming group creation.");
                }
                break;
            case CAMERA_REQUEST_CODE:
                imgAlert.dismiss();
                Log.i(TAG,"BIGGITOY");
                if(resultCode == Activity.RESULT_OK){
                    if(data!=null){
                        Toast.makeText(getContext(),"Camera completion has not been implemented.",Toast.LENGTH_SHORT).show();
                        Log.i(TAG,"Camera intent returned with data!");
                    } else {
                        Log.i(TAG,"Camera returned with no data.");
                    }
                }
                break;
            case IMAGE_REQUEST_CODE:
                imgAlert.dismiss();
                Log.i(TAG,"Image URI retrieved from user selection.");
                if(data != null){
                    imageUri = data.getData();
                    Picasso.get().load(imageUri).into(imageView);
                }
                break;
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void createGroup(){
        String groupName = name_Txt.getText().toString();
        String descText = desc_Txt.getText().toString();
        privacyBtn = privacyGroup.findViewById(privacyGroup.getCheckedRadioButtonId());
        String privacy = privacyBtn.getText().toString();
        String inviteText = invite_Spinner.getSelectedItem().toString();

        final GroupBase groupBase = new GroupBase(groupName, descText, currentUser.getUid(),privacy,inviteText, null);
        if(imageUri!=null){
            mGroupViewModel.uploadImage(imageUri, groupName)
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            groupBase.setImageURI(uri.toString());
                            mGroupViewModel.createGroup(groupBase).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    progressText.setVisibility(View.INVISIBLE);
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(),"Successfully created "+groupBase.getName(),Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    } else {
                                        Toast.makeText(getContext(),"Error creating "+groupBase.getName(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG,"Error uploading image.",e);
                            Toast.makeText(getContext(),"Error uploading image.",Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            mGroupViewModel.createGroup(groupBase)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        progressText.setVisibility(View.INVISIBLE);
                        if(task.isSuccessful()){
                            Log.i(TAG,"THIS SHOULD BE AT THE END!");
                            Toast.makeText(getContext(),"Successfully created "+groupBase.getName(),Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(),"Error creating "+groupBase.getName(),Toast.LENGTH_SHORT).show();
                        }
                    }
            });
        }
    }


}

