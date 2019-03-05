/*
 * Instead of deleting an existing document when the name changes, added another field to GroupBase
 * class that is unchangeable and is equal to the value of the Group name when it is first created.
 * This original name is the field that will be used to query fire store for the group.
 *
 * ToDo: Control back button: if editing, should inflate the confirmation dialog saying all changes will be erased.
 */

package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.friendswholift.Core_Activity;
import apps.raymond.friendswholift.R;

public class Group_Detail_Fragment extends Fragment implements View.OnClickListener{
    public static final String TAG = "Group_Detail_Fragment";
    private static final String TRANSITION_NAME = "transition_name";
    private static final String GROUP_BASE = "group_base";

    private GroupsViewModel mGroupViewModel;

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

    ActionBar actionBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"New instance of Detailed Group Fragment");
        setHasOptionsMenu(true);
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        mGroupViewModel = ViewModelProviders.of(requireActivity()).get(GroupsViewModel.class);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.group_detail_frag,container,false);
    }

    GroupBase groupBase;
    String owner, currUser;
    ViewFlipper viewFlipper;
    Spinner inviteSpinner;
    RadioGroup privacyGroup;
    RadioButton publicBtn,discoverBtn,exclusiveBtn;
    TextInputEditText nameEdit, descEdit;
    ArrayAdapter<CharSequence> adapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        groupBase = getArguments().getParcelable(GROUP_BASE);
        String transitionName = getArguments().getString(TRANSITION_NAME);
        owner = groupBase.getOwner();
        currUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try{
            actionBar.setTitle(groupBase.getName());
            actionBar.setDisplayShowTitleEnabled(true);
        } catch (NullPointerException npe){
            Log.i(TAG,"Error setting title of fragment.",npe);
        }

        Button saveEditsBtn = view.findViewById(R.id.save_group_btn);
        saveEditsBtn.setOnClickListener(this);

        viewFlipper = view.findViewById(R.id.group_edit_flipper);

        if(owner.equals(currUser)){
            nameEdit = view.findViewById(R.id.group_name_edit);
            descEdit = view.findViewById(R.id.group_desc_edit);

            publicBtn = view.findViewById(R.id.public_btn);
            discoverBtn = view.findViewById(R.id.discoverable_btn);
            exclusiveBtn = view.findViewById(R.id.exclusive_btn);

            privacyGroup = view.findViewById(R.id.privacy_buttons);

            inviteSpinner = view.findViewById(R.id.invite_spinner);
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.array_invite_authorize,
                            android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            inviteSpinner.setAdapter(adapter);
        }

        TextView name = view.findViewById(R.id.detail_group_name_txt);

        name.setText(groupBase.getName());
        name.setTransitionName(transitionName);
        //transitionScheduler.scheduleStartTransition(name);

        TextView desc = view.findViewById(R.id.group_desc_txt);
        desc.setText(groupBase.getDescription());

        final ImageView image = view.findViewById(R.id.group_image);

        if(groupBase.getImageURI()!=null && groupBase.getBytes()==null){
            Log.i(TAG,"Fetching the photo for this group.");
            mGroupViewModel.getImage(groupBase.getImageURI())
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
            case R.id.save_group_btn:
                Log.i(TAG,"Overwriting the Group object.");
                groupBase.setName(nameEdit.getText().toString());
                groupBase.setDescription(descEdit.getText().toString());

                RadioButton privacyBtn = privacyGroup.findViewById(privacyGroup.getCheckedRadioButtonId());
                groupBase.setVisibility(privacyBtn.getText().toString());
                groupBase.setInvite(inviteSpinner.getSelectedItem().toString());

                mGroupViewModel.updateGroup(groupBase);

                //Todo: Have to add the ability to modify the image.
                break;
        }
    }

    MenuItem editItem;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.i(TAG,"onCreateOptionsMenu of detailed group fragment.");
        menu.clear();
        inflater.inflate(R.menu.home_actionbar,menu);
        editItem = menu.findItem(R.id.action_edit);
    }

    // This is called every time the Menu opens.
    // We are overriding this activity callback, not inflating a new menu.
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.i(TAG,"onPrepareOptionsMenu of detailed group fragment.");
        menu.findItem(R.id.action_profile).setVisible(false);
        menu.findItem(R.id.action_profile).setEnabled(false);
        if(owner.equals(currUser)){
            menu.findItem(R.id.action_edit).setEnabled(true);
            menu.findItem(R.id.action_edit).setVisible(true);
        } else {
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_edit).setEnabled(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    //Going to handle events in the Activity because not sure why it doesn't work in the fragment.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i){
            case R.id.action_edit:
                item.setVisible(false);
                item.setEnabled(false);
                editGroup();
                return true;
        }
        return false;
    }

    private void editGroup(){
        Log.i(TAG,"Entering edit mode for: "+groupBase.getName());
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
        actionBar.setDisplayShowTitleEnabled(false);
        super.onDestroy();
    }


    /*
    TransitionScheduler transitionScheduler;
    public interface TransitionScheduler{
        void scheduleStartTransition(View sharedView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            //We need to get an instance of the Class that will execute our interface method.
            transitionScheduler = (TransitionScheduler) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG,"Class cast exception." + e.getMessage());
        }
    }*/




}
