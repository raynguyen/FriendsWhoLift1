package apps.raymond.friendswholift.Groups;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import apps.raymond.friendswholift.R;

public class MyGroupsFragment extends Fragment {

    private static final String TAG = "MygroupsFragment";
    GroupsViewModel mGroupViewModel;

    List<String> myGroupTags;

    TextView testTextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_cardview_container,container,false);
        RecyclerView cardRecycler = view.findViewById(R.id.card_container);

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
                mGroupViewModel.getGroupTags()
                        .addOnCompleteListener(new OnCompleteListener<Set<String>>() {
                            @Override
                            public void onComplete(@NonNull Task<Set<String>> task) {
                                // Can only access the data once the task is complete.
                                myGroupTags = new ArrayList<>(task.getResult()); // Convert to a list object since I don't know how to handle Sets.
                                //testTextView.setText(task.getResult().toString());
                            }
                        });
            }
        });

        getGroupPojoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGroupViewModel.getGroups(myGroupTags).
                        addOnCompleteListener(new OnCompleteListener<List<GroupBase>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<GroupBase>> task) {
                        if(task.isSuccessful()){
                            for(GroupBase group: task.getResult())
                                Log.i(TAG,"The getGroup returned groups with the names: " + group.getName());
                        }
                    }
                });

                testTextView.setText(myGroupTags.toString());



                /*
                ArrayList<String> mylist = new ArrayList<>();
                mylist.add("Dont touch the lemons");
                mylist.add("Fight me");
                mylist.add("Taco Friends");
                final ArrayList<GroupBase> myGroups = new ArrayList<>();

                for (String tag : mylist){
                    FirebaseFirestore.getInstance().collection("Groups").document(tag).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    myGroups.add(task.getResult().toObject(GroupBase.class));
                                    Log.i(TAG,"Retrieved the following: " + myGroups.toString());
                                }
                            });
                }*/
            }
        });

        return view;
    }

    /*
    public void testMethod(){
        Log.i(TAG,"Inside testMethod.");
        mUserViewModel.createGroup(new FireStoreCallBack() {
            @Override
            public void onCallBack(Set<String> keySet) {
                Log.i(TAG,"Inside testMethod of our Fragment and retrieved: " + keySet);
                testTextView.setText(keySet.toString());
            }
        });
    }*/

    public interface FireStoreCallBack {
        void onCallBack(Set<String> keySet);
    }
}
