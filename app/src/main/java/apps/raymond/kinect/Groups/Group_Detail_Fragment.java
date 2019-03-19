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
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.DialogFragments.YesNoDialog;
import apps.raymond.kinect.Interfaces.BackPressListener;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;
import apps.raymond.kinect.VerticalTextView;

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
    ActionBar actionBar;
    SearchView toolbarSearch;
    FragmentManager fm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = requireActivity().getSupportFragmentManager();
        toolbarSearch = getActivity().findViewById(R.id.toolbar_search);
        toolbarSearch.setVisibility(View.GONE);

        setHasOptionsMenu(true);
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        viewModel = ViewModelProviders.of(requireActivity()).get(Repository_ViewModel.class);
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
    VerticalTextView membersTxt;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        groupBase = getArguments().getParcelable(GROUP_BASE);
        String transitionName = getArguments().getString(TRANSITION_NAME);
        owner = groupBase.getOwner();
        currUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        membersTxt = view.findViewById(R.id.members_vtxt);
        membersTxt.setOnClickListener(this);

        viewFlipper = view.findViewById(R.id.group_edit_flipper);

        if(owner.equals(currUser)){
            nameEdit = view.findViewById(R.id.group_name_edit);
            descEdit = view.findViewById(R.id.group_desc_edit);

            publicBtn = view.findViewById(R.id.public_btn);
            discoverBtn = view.findViewById(R.id.discoverable_btn);
            exclusiveBtn = view.findViewById(R.id.exclusive_btn);

            privacyGroup = view.findViewById(R.id.privacy_buttons);

            inviteSpinner = view.findViewById(R.id.invite_spinner);
            adapter = ArrayAdapter.createFromResource(requireActivity(), R.array.array_invite_authorize,
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
        }
        return false;
    }

    @Override
    public void onBackPress() {
        int i = viewFlipper.getDisplayedChild();
        Log.i(TAG,"Current state of detail fragment: "+i);
        switch (i){
            case DETAIL_READ:
                fm.popBackStack();
                break;
            case DETAIL_WRITE:
                YesNoDialog yesNoDialog = YesNoDialog.newInstance(YesNoDialog.WARNING,YesNoDialog.DISCARD_CHANGES);
                yesNoDialog.setCancelable(false);
                yesNoDialog.setTargetFragment(this, Core_Activity.YESNO_REQUEST);
                yesNoDialog.show(fm,null);
                break;
        }
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
        actionBar.setDisplayShowTitleEnabled(false);
        toolbarSearch.setVisibility(View.VISIBLE);
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
