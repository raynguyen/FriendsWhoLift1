/*
 * TODO:
 * Inflate the spinner in onViewCreated.
 * Add the appropriate views to strings in the save group button.
 * Call the Repository group update which in this case is probably just create new document?
 *
 * DO THE SAME FOR EVENTS
 */

package apps.raymond.friendswholift.Groups;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import apps.raymond.friendswholift.R;

public class Detailed_Group_Fragment extends Fragment implements View.OnLayoutChangeListener,
        View.OnClickListener {
    private static final String TAG = "Detailed_Group_Fragment";
    private static final String TRANSITION_NAME = "transition_name";
    private static final String GROUP_BASE = "group_base";

    TransitionScheduler transitionScheduler;
    public interface TransitionScheduler{
        void scheduleStartTransition(View sharedView);
    }

    private GroupsViewModel mGroupViewModel;

    public Detailed_Group_Fragment(){
    }

    public static Detailed_Group_Fragment newInstance(GroupBase groupBase, String transitionName){
        Detailed_Group_Fragment detailed_group_fragment = new Detailed_Group_Fragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(GROUP_BASE, groupBase);
        bundle.putString(TRANSITION_NAME,transitionName);
        detailed_group_fragment.setArguments(bundle);
        return detailed_group_fragment;
    }

    ActionBar actionBar;
    Toolbar toolbar;
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

        flip = true;
        viewFlipper = view.findViewById(R.id.group_edit_flipper);
        viewFlipper.addOnLayoutChangeListener(this);

        final TextView name = view.findViewById(R.id.detail_group_name_txt);

        name.setText(groupBase.getName());
        name.setTransitionName(transitionName);
        transitionScheduler.scheduleStartTransition(name);

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
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            //We need to get an instance of the Class that will execute our interface method.
            transitionScheduler = (TransitionScheduler) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG,"Class cast exception." + e.getMessage());
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        Log.i(TAG,"Layout changed.");
        int i = viewFlipper.getDisplayedChild();
        switch (i){
            case 0:
                Log.i(TAG,"Detail Group read layout");
                break;
            case 1:
                Log.i(TAG,"Detail Group edit layout");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG,"Clicked on view: "+v.toString());
        int i = v.getId();
        switch (i){
            case R.id.save_group_btn:
                Log.i(TAG,"Overwriting the Group object.");
                groupBase.setName();
                groupBase.setDescription();
                groupBase.setInvite();
                groupBase.setVisibility();
        }
    }

    MenuItem editItem, saveItem;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.i(TAG,"onCreateOptionsMenu of detailed group fragment.");
        menu.clear();
        inflater.inflate(R.menu.home_actionbar,menu);
        editItem = menu.findItem(R.id.action_edit_group);
        saveItem = menu.findItem(R.id.action_save_group);

    }

    // This is called every time the Menu opens.
    // We are overriding this activity callback, not inflating a new menu.
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.i(TAG,"onPrepareOptionsMenu of detailed group fragment.");
        menu.findItem(R.id.action_create_group).setVisible(false);
        menu.findItem(R.id.action_create_group).setEnabled(false);
        if(owner.equals(currUser)){
            menu.findItem(R.id.action_edit_group).setEnabled(true);
            menu.findItem(R.id.action_edit_group).setVisible(true);
        } else {
            menu.findItem(R.id.action_edit_group).setVisible(false);
            menu.findItem(R.id.action_edit_group).setEnabled(false);
        }
        super.onPrepareOptionsMenu(menu);
    }


    //Going to handle events in the Activity because not sure why it doesn't work in the fragment.
    boolean flip;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i){
            case R.id.action_edit_group:
                //Calling item.XXXXXXXXX works but using editItem.xxxxxxxxxxxxxxx does not!?!?!?!?
                Log.i(TAG,"Clicked on edit item: "+item.getItemId());
                Log.i(TAG,"Clicked on edit item: "+editItem.getItemId());
                item.setVisible(false);
                item.setEnabled(false);
                saveItem.setEnabled(true);
                saveItem.setVisible(true);
                editGroup();
                return true;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        actionBar.setDisplayShowTitleEnabled(false);
        super.onDestroy();
    }

    private void editGroup(){
        Log.i(TAG,"Entering edit mode for: "+groupBase.getName());
        viewFlipper.showNext();
    }
}
