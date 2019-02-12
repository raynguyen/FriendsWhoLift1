/*
 * ToDo:
 * 1)Implement the swiperefresher that updates the RecyclerView CardViews.
 */

package apps.raymond.friendswholift.Groups;


import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Interfaces.GroupClickListener;
import apps.raymond.friendswholift.R;

public class MyGroupsFragment extends Fragment implements View.OnClickListener, GroupClickListener {
    private static final String TAG = "MyGroupsFragment";

    GroupsViewModel mGroupViewModel;
    ArrayList<GroupBase> myGroups;
    TextView testTextView;
    ImageView mImage;
    GroupRecyclerAdapter mAdapter;

    public MyGroupsFragment(){
        //Required empty fragment. Not sure why it is needed.
    }

    public MyGroupsFragment newInstance(){
        return new MyGroupsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_mygroups, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button getfieldsBtn = view.findViewById(R.id.testButton3);
        Button getGroupPojoBtn = view.findViewById(R.id.testButton4);
        getGroupPojoBtn.setOnClickListener(this);
        getfieldsBtn.setOnClickListener(this);

        RecyclerView cardRecycler = view.findViewById(R.id.card_container);
        SearchView groupSearchView = view.findViewById(R.id.groupSearchView);

        mAdapter = new GroupRecyclerAdapter(myGroups, this);
        //cardRecycler.setItemAnimator(new DefaultItemAnimator());
        // When there is a change in data, we want to notify the Adapter by calling the GroupRecyclerAdapter.setData();
        cardRecycler.setAdapter(mAdapter);
        cardRecycler.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
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

        mGroupViewModel = ViewModelProviders.of(getActivity()).get(GroupsViewModel.class);//new GroupsViewModel();

        testTextView = view.findViewById(R.id.testTextView);
        mImage = view.findViewById(R.id.testImage);

        updateCardViews();
    }


    /*
     * To truly follow SoC principle, the Fragment should not do the conversion from
     * DocumentSnapshot to GroupBase object, it should be dealt with by the repository.
     *
     * Need to check to see what happens if there is no photoURI in the group.
     */
    //This guy reads from the fields of the document. App crashes at this point because it is returning an object that is not a groupbase so groupbase.getname blows up!
    private void updateCardViews(){
        mGroupViewModel.getUsersGroups().addOnCompleteListener(new OnCompleteListener<List<Task<GroupBase>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<GroupBase>>> task) {
                Log.i(TAG,"Finished retrieving a List of tasks.");
                Tasks.whenAllSuccess(task.getResult()).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        if(objects.size()>0){
                            myGroups = new ArrayList<>();
                            for(Object object:objects){
                                myGroups.add((GroupBase) object);
                                Log.i(TAG,"Added this group to our data set: "+((GroupBase) object).getName());
                            }
                        }
                        Log.i(TAG,"????????????????????");
                        mAdapter.setData(myGroups);
                    }
                });
            }
        });
    }

    @Override
    public void onGroupClick(int position, GroupBase groupBase) {
        Log.i(TAG,"Clicked on a Group inside the RecyclerView at position: "+ position);
        Fragment detailedGroup = DetailedGroupFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable("GroupObject",groupBase);
        detailedGroup.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.groups_FrameLayout,detailedGroup)
                .addToBackStack(null)
                .show(detailedGroup)
                .commit();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.testButton3:
                Log.i(TAG, "Clicked on testButton3.");
                break;
            case R.id.testButton4:
                Log.i(TAG, "Clicked on testButton4.");
                break;
        }
    }


}