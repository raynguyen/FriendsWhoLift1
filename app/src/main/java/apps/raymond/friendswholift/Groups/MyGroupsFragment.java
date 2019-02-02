package apps.raymond.friendswholift.Groups;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import apps.raymond.friendswholift.R;

public class MyGroupsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MygroupsFragment";
    GroupsViewModel mGroupViewModel;

    List<String> myGroupTags;
    ArrayList<GroupBase> myGroups;
    TextView testTextView;
    ImageView mImage;
    GroupRecyclerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_mygroups, container, false);

        RecyclerView cardRecycler = view.findViewById(R.id.card_container);
        cardRecycler.setItemAnimator(new DefaultItemAnimator());
        cardRecycler.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        // When there is a change in data, we want to notify the Adapter by calling the GroupRecyclerAdapter.setData();
        mAdapter = new GroupRecyclerAdapter(myGroups);

        cardRecycler.setAdapter(mAdapter);
        cardRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mGroupViewModel = new GroupsViewModel();

        Button getfieldsBtn = view.findViewById(R.id.testButton3);
        Button getGroupPojoBtn = view.findViewById(R.id.testButton4);
        getGroupPojoBtn.setOnClickListener(this);


        testTextView = view.findViewById(R.id.testTextView);
        mImage = view.findViewById(R.id.testImage);

        /*
         * When the fragment is created, we need to run the snippet inside this onclickListener.
         */
        mGroupViewModel.getGroupTags().addOnCompleteListener(new OnCompleteListener<Set<String>>() {
            @Override
            public void onComplete(@NonNull Task<Set<String>> task) {
                // Can only access the data once the task is complete.
                myGroupTags = new ArrayList<>(task.getResult()); // Convert to a list object since I don't know how to handle Sets.
                createGroupCards(myGroupTags);
            }
        });
        return view;
    }

    private void createGroupCards(List<String> myGroupTags){

        // getGroups() will return a List<Tasks>. We will implement whenAllSuccess here to know when we retrieved all the groups.
        final List<Task<DocumentSnapshot>> myTasks = mGroupViewModel.getGroups(myGroupTags);
        Tasks.whenAllSuccess(myTasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                myGroups = new ArrayList<>();
                Log.i(TAG,"Successfully retrieved all the Groups.");
                for(Object object : objects){
                    myGroups.add(((DocumentSnapshot) object).toObject(GroupBase.class));
                }
                mAdapter.setData(myGroups);
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.testButton4:
                Log.i(TAG, "Clicked on testButton4.");
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                storageRef.child("TestGroup1/dogpic.jpg").getBytes(1024*1024)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Log.i(TAG,"Attaching testImage ImageView with downloaded file");
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                mImage.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Unable to retrieve the requested filed.", e);
                    }
                });
                break;
        }
    }
}
