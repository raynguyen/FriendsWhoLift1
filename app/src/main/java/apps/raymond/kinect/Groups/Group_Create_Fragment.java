/*
 * ToDo:
 * When the user hits the confirm button, we want to send the user back to the MyGroups screen with
 * an updated RecyclerView that includes the new group.
 */

package apps.raymond.kinect.Groups;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Add_Users_Adapter;
import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.DialogFragments.SearchUsersDialog;
import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;
import apps.raymond.kinect.UserProfile.User_Model;

public class Group_Create_Fragment extends Fragment implements
        View.OnClickListener, BackPressListener, Add_Users_Adapter.CheckProfileInterface {
    public static final String TAG = "Group_Create_Fragment";
    private static final int IMAGE_REQUEST_CODE = 11;
    private static final int CAMERA_REQUEST_CODE = 12;

    private AddGroup addGroupInterface;
    public interface AddGroup{
        void addToGroupRecycler(GroupBase groupBase);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            addGroupInterface = (AddGroup) context;
            Log.i(TAG,"Successfully bound AddGroup interface to activity.");
        } catch (ClassCastException e){
            Log.i(TAG,"Error.",e);
        }
    }

    private FragmentManager fm;
    List<User_Model> usersList;
    private Repository_ViewModel viewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            fm = getActivity().getSupportFragmentManager();
        } catch (NullPointerException npe){
            Log.e(TAG,"Unable to get fragment manager for fragment.",npe);
        }
        viewModel = ViewModelProviders.of(getActivity()).get(Repository_ViewModel.class);
        usersList = new ArrayList<>();
        fetchUsersList();
    }

    private FirebaseUser currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.group_create_frag,container,false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return view;
    }

    private EditText desc_Txt;
    private TextInputEditText name_Txt;
    private ImageView imageView;
    private AlertDialog imgAlert;
    RadioButton privacyBtn;
    RadioGroup privacyGroup;
    Spinner invite_Spinner;
    ProgressBar progressBar;
    TextView progressText;
    Add_Users_Adapter userAdapter;
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
                .createFromResource(requireActivity(), R.array.array_invite_authorize,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        invite_Spinner.setAdapter(adapter);

        ImageButton cameraBtn = view.findViewById(R.id.camera_button);
        cameraBtn.setOnClickListener(this);

        imageView = view.findViewById(R.id.image_view);
        privacyGroup = view.findViewById(R.id.privacy_buttons);

        Button testBtn = view.findViewById(R.id.test_btn);
        testBtn.setOnClickListener(this);

        RecyclerView usersRecycler = view.findViewById(R.id.add_users_recycler);
        usersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new Add_Users_Adapter(usersList, this);
        usersRecycler.setAdapter(userAdapter);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i){
            case R.id.create_grp_btn:
                if(fieldsCheck()) {
                    Toast.makeText(getContext(),"Mandatory fields must be completed.",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
                createGroup();
                break;
            case R.id.discard_grp_btn:
                getActivity().onBackPressed();
                break;
            case R.id.camera_button:
                getImage();
                break;
            case R.id.test_btn:
                Log.i(TAG,"Button to open uses search clicked. implement this");
                //expandUserRecycler();
                break;
        }
    }

    /*
     * Creates a dialog for the user to determine how they want to retrieve an image. If device is
     * equipped with a camera, the dialog shown will provide UI with a camera option.
     */
    private void getImage(){
        View imgDialogView;
        if(requireActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
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
                if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
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
                Toast.makeText(getContext(),"Not yet implemented; allow inviteUsersList to google an image for result.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    List<User_Model> inviteUsersList;
    private void fetchUsersList(){
        Log.i(TAG,"Fetching all users from store.");
        viewModel.fetchUsers().addOnCompleteListener(new OnCompleteListener<List<User_Model>>() {
            @Override
            public void onComplete(@NonNull Task<List<User_Model>> task) {
                if(task.isSuccessful()){
                    inviteUsersList = new ArrayList<>();
                    Log.i(TAG,"Successfully fetched suggested inviteUsersList list.");
                    Log.i(TAG,"List composed of: "+task.getResult().toString());
                    usersList.addAll(task.getResult());
                    userAdapter.notifyDataSetChanged();
                } else {
                    Log.i(TAG,"Error retrieving suggested inviteUsersList list. " + task.getException());
                    Toast.makeText(getContext(),"Error retrieving suggested invitees.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void expandUserRecycler(){
        Log.i(TAG,"Creating search inviteUsersList recycler dialog");
        SearchUsersDialog usersDialog = SearchUsersDialog.newInstance();
        usersDialog.show(fm,"Search_Users_Dialog");
    }

    @Override
    public void addToCheckedList(User_Model clickedUser) {
        Log.i(TAG,"Adding user to list to invite: "+clickedUser.getEmail());
        inviteUsersList.add(clickedUser);
        for(User_Model user : inviteUsersList){
            Log.i(TAG,"Inviting: "+user.getEmail());
        }
    }

    @Override
    public void removeFromCheckedList(User_Model clickedUser) {
        Log.i(TAG,"Removing user from list to invite: "+clickedUser.getEmail());
        inviteUsersList.remove(clickedUser);
        for(User_Model user : inviteUsersList){
            Log.i(TAG,"Inviting: "+user.getEmail());
        }
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

    private void createGroup(){
        String groupName = name_Txt.getText().toString();
        String descText = desc_Txt.getText().toString();
        privacyBtn = privacyGroup.findViewById(privacyGroup.getCheckedRadioButtonId());
        String privacy = privacyBtn.getText().toString();
        String inviteText = invite_Spinner.getSelectedItem().toString();
        final GroupBase newGroup = new GroupBase(groupName, descText, currentUser.getEmail(),privacy,inviteText, null);

        if(imageUri!=null){
            viewModel.uploadImage(imageUri, groupName)
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG,"Successfully uploaded image to storage.");
                                viewModel.createGroup(newGroup,inviteUsersList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.i(TAG,"Successfully created Group document.");
                                            viewModel.sendGroupInvites(newGroup,inviteUsersList);
                                        }
                                    }
                                });
                            }
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newGroup.setImageURI(uri.toString());
                            viewModel.createGroup(newGroup, inviteUsersList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    progressText.setVisibility(View.INVISIBLE);
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(),"Successfully created "+newGroup.getName(),Toast.LENGTH_SHORT).show();
                                        viewModel.sendGroupInvites(newGroup,inviteUsersList);
                                        addGroupInterface.addToGroupRecycler(newGroup);
                                        fm.popBackStack();
                                    } else {
                                        Toast.makeText(getContext(),"Error creating "+newGroup.getName(),Toast.LENGTH_SHORT).show();
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
            viewModel.createGroup(newGroup, inviteUsersList)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.INVISIBLE);
                            progressText.setVisibility(View.INVISIBLE);
                            if(task.isSuccessful()){
                                Log.i(TAG,"THIS SHOULD BE AT THE END!");
                                Toast.makeText(getContext(),"Successfully created "+newGroup.getName(),Toast.LENGTH_SHORT).show();
                                viewModel.sendGroupInvites(newGroup,inviteUsersList);
                                addGroupInterface.addToGroupRecycler(newGroup);
                                fm.popBackStack();
                            } else {
                                Toast.makeText(getContext(),"Error creating "+newGroup.getName(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPress() {
        YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
        yesNoDialog.setCancelable(false);
        yesNoDialog.setTargetFragment(this, Core_Activity.YESNO_REQUEST);
        yesNoDialog.show(fm,null);
    }

    public Uri imageUri;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case Core_Activity.YESNO_REQUEST:
                if(resultCode == YesNoDialog.POS_RESULT){
                    fm.popBackStack();
                }
                break;
            case CAMERA_REQUEST_CODE:
                imgAlert.dismiss();
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
    }

}

