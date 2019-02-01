package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import apps.raymond.friendswholift.R;

public class MyGroupsFragment extends Fragment {

    private static final String TAG = "MygroupsFragment";
    GroupsViewModel mGroupViewModel;

    List<String> myGroupTags;
    ArrayList<GroupBase> myGroups;
    TextView testTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_cardview_container, container, false);

        RecyclerView cardRecycler = view.findViewById(R.id.card_container);
        cardRecycler.setItemAnimator(new DefaultItemAnimator());

        // When there is a change in data, we want to notify the Adapter by calling the GroupRecyclerAdapter.setData();
        final GroupRecyclerAdapter mAdapter = new GroupRecyclerAdapter(myGroups);

        cardRecycler.setAdapter(mAdapter);
        cardRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mGroupViewModel = new GroupsViewModel();

        Button createUserDocBtn = view.findViewById(R.id.testButton1);
        Button updateFieldBtn = view.findViewById(R.id.testButton2);
        Button getfieldsBtn = view.findViewById(R.id.testButton3);
        Button getGroupPojoBtn = view.findViewById(R.id.testButton4);
        testTextView = view.findViewById(R.id.testTextView);

        createUserDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //testMethod();
            }
        });

        createUserDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        updateFieldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        /*
         * When the fragment is created, we need to run the snippet inside this onclickListener.
         */
        getfieldsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getGroupTags() returns a task that queries FireStore for the User's fields.
                mGroupViewModel.getGroupTags()
                        .addOnCompleteListener(new OnCompleteListener<Set<String>>() {
                            @Override
                            public void onComplete(@NonNull Task<Set<String>> task) {
                                // Can only access the data once the task is complete.
                                myGroupTags = new ArrayList<>(task.getResult()); // Convert to a list object since I don't know how to handle Sets.
                            }
                        });
            }
        });

        getGroupPojoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testTextView.setText(myGroupTags.toString());
                // getGroups() will return a List<Tasks>. We will implement whenAllSuccess here to know when we retrieved all the groups.
                final List<Task<DocumentSnapshot>> myTasks = mGroupViewModel.getGroups(myGroupTags);
                Tasks.whenAllSuccess(myTasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        Log.i(TAG,"Successfully retrieved all the Groups.");
                        for(Object object : objects){
                            myGroups = new ArrayList<>();
                            myGroups.add(((DocumentSnapshot) object).toObject(GroupBase.class));
                            //GroupBase groupBase = ((DocumentSnapshot) object).toObject(GroupBase.class);
                            //Log.i(TAG,object.getName());
                        }
                        mAdapter.setData(myGroups);
                        // Test method to make sure we get POJO correctly
                        for(GroupBase base : myGroups){
                            Log.i(TAG, "The retrieval returned this object : " + base.getName());
                        }
                    }
                });
            }
        });
        return view;
    }
}
