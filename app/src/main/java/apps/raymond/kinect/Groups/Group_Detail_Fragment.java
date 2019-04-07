/*
 * Instead of deleting an existing document when the name changes, added another field to GroupBase
 * class that is unchangeable and is equal to the value of the Group name when it is first created.
 * This original name is the field that will be used to query fire store for the group.
 *
 * ToDo: Control back button: if editing, should inflate the confirmation dialog saying all changes will be erased.
 */

package apps.raymond.kinect.Groups;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.Invite_Users_Fragment;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;
import apps.raymond.kinect.UIResources.VerticalTextView;
import apps.raymond.kinect.UserProfile.UserModel;

public class Group_Detail_Fragment extends Fragment implements View.OnClickListener, BackPressListener {
    public static final String TAG = "Group_Detail_Fragment";
    private static final String TRANSITION_NAME = "transition_name";
    private static final String GROUP_BASE = "group_base";
    private static final String MEMBERS_FRAG = "Members_Fragment";
    private static final int DETAIL_READ = 0;
    private static final int DETAIL_WRITE = 1;

    public Group_Detail_Fragment(){
    }

    public static Group_Detail_Fragment newInstance(GroupBase groupBase, String transitionName){
        Group_Detail_Fragment _group_Detail_fragment = new Group_Detail_Fragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(GROUP_BASE, groupBase);
        bundle.putString(TRANSITION_NAME,transitionName);
        _group_Detail_fragment.setArguments(bundle);
        return _group_Detail_fragment;
    }

    private Repository_ViewModel viewModel;
    SearchView toolbarSearch;
    FragmentManager fm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        fm = requireActivity().getSupportFragmentManager();
        toolbarSearch = getActivity().findViewById(R.id.toolbar_search);
        toolbarSearch.setVisibility(View.GONE);

        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
        fetchUserList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.group_detail_frag,container,false);
    }

    TextView groupName;
    GroupBase groupBase;
    String owner, currUser;
    ViewFlipper viewFlipper;
    Spinner inviteSpinner;
    RadioGroup privacyGroup;
    RadioButton publicBtn,discoverBtn,exclusiveBtn;
    TextInputEditText nameEdit, descEdit;
    ArrayAdapter<CharSequence> adapter;
    VerticalTextView membersDrawer;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        groupBase = getArguments().getParcelable(GROUP_BASE);
        owner = groupBase.getOwner();

        //ToDo: This should be a call to a repository method and not to FirebaseAuth itself.
        currUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        membersDrawer = view.findViewById(R.id.members_vtxt);
        membersDrawer.setOnClickListener(this);

        groupName = view.findViewById(R.id.group_name);
        groupName.setText(groupBase.getName());

        final ImageView image = view.findViewById(R.id.group_image);
        if(groupBase.getImageURI()!=null && groupBase.getBytes()==null){
            Log.i(TAG,"Fetching the photo for this group.");
            viewModel.getImage(groupBase.getImageURI())
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            groupBase.setBytes(bytes);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            image.setImageBitmap(bitmap);
                        }
                    });
        }
        if(groupBase.getBytes()!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(groupBase.getBytes(),0,groupBase.getBytes().length);
            image.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.members_vtxt:
                Members_Panel_Fragment membersPanel = Members_Panel_Fragment.newInstance(groupBase);
                getChildFragmentManager().beginTransaction()
                        .addToBackStack(MEMBERS_FRAG)
                        .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right,R.anim.slide_in_right,R.anim.slide_out_right)
                        .replace(R.id.members_frame,membersPanel,MEMBERS_FRAG)
                        .commit();
                break;
        }
    }

    MenuItem saveAction, editAction;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG,"onCreateOptionsMenu of detailed group fragment.");
        menu.clear();
        inflater.inflate(R.menu.details_menu,menu);
        editAction = menu.findItem(R.id.action_edit);
        saveAction = menu.findItem(R.id.action_save);

        if(owner.equals(currUser)){
            editAction.setEnabled(true);
            editAction.setVisible(true);
        } else {
            editAction.setVisible(false);
            editAction.setEnabled(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Going to handle events in the Activity because not sure why it doesn't work in the fragment.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i){
            case R.id.action_edit:
                item.setVisible(false);
                item.setEnabled(false);
                saveAction.setEnabled(true);
                saveAction.setVisible(true);
                editGroup();
                return true;
            case R.id.action_save:
                Log.i(TAG,"Overwriting the Group object.");
                item.setVisible(false);
                item.setEnabled(false);
                editAction.setVisible(true);
                editAction.setEnabled(true);

                groupBase.setName(nameEdit.getText().toString());
                groupBase.setDescription(descEdit.getText().toString());

                RadioButton privacyBtn = privacyGroup.findViewById(privacyGroup.getCheckedRadioButtonId());
                groupBase.setVisibility(privacyBtn.getText().toString());
                groupBase.setInvite(inviteSpinner.getSelectedItem().toString());

                viewFlipper.showPrevious();
                //Todo: Return a task when updating so that we can display the progress bar.
                viewModel.updateGroup(groupBase);

                //Todo: Have to add the ability to modify the image.
                return true;
            case R.id.action_invite:
                Fragment usersInviteFragment = Invite_Users_Fragment.newInstance(userModelArrayList);
                getFragmentManager().beginTransaction()
                        .add(R.id.core_frame,usersInviteFragment,Core_Activity.INVITE_USERS_FRAG)
                        .addToBackStack(Core_Activity.INVITE_USERS_FRAG)
                        .commit();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPress() {
        fm.popBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Core_Activity.YESNO_REQUEST:
                if(resultCode == YesNoDialog.POS_RESULT){
                    Log.i(TAG,"yes clicked");
                    getActivity().invalidateOptionsMenu();
                    viewFlipper.setDisplayedChild(DETAIL_READ);
                } else {
                    Log.i(TAG,"Cancel clicked.");
                    // Do nothing.
                }
        }
    }

    private void editGroup(){
        nameEdit.setText(groupBase.getName(), TextView.BufferType.EDITABLE);
        descEdit.setText(groupBase.getDescription(),TextView.BufferType.EDITABLE);
        String groupVisibility = groupBase.getVisibility();

        if (groupVisibility.equals(publicBtn.getText().toString())) {
            publicBtn.setChecked(true);
        } else if (groupVisibility.equals(discoverBtn.getText().toString())){
            discoverBtn.setChecked(true);
        } else if (groupVisibility.equals(exclusiveBtn.getText().toString())){
            exclusiveBtn.setChecked(true);
        }

        int i = adapter.getPosition(groupBase.getInvite());
        inviteSpinner.setSelection(i);

        Log.i(TAG,"Invite power for group is: "+groupBase.getInvite());
        //inviteSpinner.setSelection();
        viewFlipper.showNext();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "DESTROYING GROUP DETAIL FRAGMENT.");
        toolbarSearch.setVisibility(View.VISIBLE);
        super.onDestroy();
    }

    ArrayList<UserModel> userModelArrayList;
    private void fetchUserList(){
        viewModel.fetchUsers().addOnCompleteListener(new OnCompleteListener<List<UserModel>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserModel>> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size()>0){
                        userModelArrayList = new ArrayList<>(task.getResult());
                    }
                }
            }
        });
    }
}
