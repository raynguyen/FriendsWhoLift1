/*
 * ToDo:
 * 1)Implement the swiperefresher that updates the RecyclerView CardViews.
 */

package apps.raymond.friendswholift.Groups;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.R;

public class MyGroupsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MygroupsFragment";

    GroupsViewModel mGroupViewModel;
    ArrayList<GroupBase> myGroups;
    TextView testTextView;
    ImageView mImage;
    GroupRecyclerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_mygroups, container, false);

        Button getfieldsBtn = view.findViewById(R.id.testButton3);
        Button getGroupPojoBtn = view.findViewById(R.id.testButton4);
        getGroupPojoBtn.setOnClickListener(this);
        getfieldsBtn.setOnClickListener(this);

        RecyclerView cardRecycler = view.findViewById(R.id.card_container);
        SearchView groupSearchView = view.findViewById(R.id.groupSearchView);

        cardRecycler.setItemAnimator(new DefaultItemAnimator());
        cardRecycler.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        // When there is a change in data, we want to notify the Adapter by calling the GroupRecyclerAdapter.setData();
        mAdapter = new GroupRecyclerAdapter(myGroups);
        cardRecycler.setAdapter(mAdapter);
        cardRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        groupSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return false;
            }
        });


        mGroupViewModel = new GroupsViewModel();

        testTextView = view.findViewById(R.id.testTextView);
        mImage = view.findViewById(R.id.testImage);

        updateCardViews();
        return view;
    }

    /*
     * To truly follow SoC principle, the Fragment should not do the conversion from
     * DocumentSnapshot to GroupBase object, it should be dealt with by the repository.
     */
    private void updateCardViews(){
        mGroupViewModel.testMethod1().addOnCompleteListener(new OnCompleteListener<List<Task<DocumentSnapshot>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<DocumentSnapshot>>> task) {
                Log.i(TAG,"Finished retrieving a List of tasks.");
                Tasks.whenAllSuccess(task.getResult()).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
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
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.testButton4:
                Log.i(TAG, "Clicked on testButton4.");
                String testString = "dogpic";
                FirebaseStorage.getInstance().getReference().child("TestGroup1/"+testString+".jpg").getBytes(1024*1024)
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
            case R.id.testButton3:
                Log.i(TAG, "Clicked on testButton3.");
        }
    }


}