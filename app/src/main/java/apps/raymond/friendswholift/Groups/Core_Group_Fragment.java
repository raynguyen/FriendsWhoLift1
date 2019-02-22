/*
 * ToDo:
 * 1)Implement the swiperefresher that updates the RecyclerView CardViews.
 */

package apps.raymond.friendswholift.Groups;

// IMPLEMENT CHANGING ACTION BAR FOR EACH VIEWPAGE
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Interfaces.GroupClickListener;
import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.TestTrans.Frag2;

public class Core_Group_Fragment extends Fragment implements View.OnClickListener, GroupClickListener {
    private static final String TAG = "Core_Group_Fragment";

    GroupsViewModel mGroupViewModel;
    ArrayList<GroupBase> myGroups;
    GroupRecyclerAdapter mAdapter;
    ProgressBar progressBar;

    //Required empty fragment. Not sure why it is needed.
    public Core_Group_Fragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.core_groups_frag, container, false);
        mGroupViewModel = ViewModelProviders.of(requireActivity()).get(GroupsViewModel.class); //new GroupsViewModel();
        updateCardViews();
        return view;
    }

    TextView headerTxt;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        headerTxt = view.findViewById(R.id.header_txt);
        progressBar = view.findViewById(R.id.progress_bar);

        RecyclerView cardRecycler = view.findViewById(R.id.card_container);
        SearchView groupSearchView = view.findViewById(R.id.groupSearchView);

        mAdapter = new GroupRecyclerAdapter(myGroups, this);
        //cardRecycler.setItemAnimator(new DefaultItemAnimator());
        // When there is a change in data, we want to notify the Adapter by calling the GroupRecyclerAdapter.setData();
        cardRecycler.setAdapter(mAdapter);
        cardRecycler.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
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

    }


    /*
     * To truly follow SoC principle, the Fragment should not do the conversion from
     * DocumentSnapshot to GroupBase object, it should be dealt with by the repository.
     *
     * Need to check to see what happens if there is no photoURI in the group.
     *
     *
     * Create the cards, then download the images is probably the cleaner approach. This prevents the
     * user from having to wait extended periods of time before viewing all their groups.
     */
    //This guy reads from the fields of the document. App crashes at this point because it is returning an object that is not a groupbase so groupbase.getname blows up!
    private void updateCardViews(){
        mGroupViewModel.getUsersGroups().addOnCompleteListener(new OnCompleteListener<List<Task<GroupBase>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<GroupBase>>> task) {
                if(task.getResult()!=null){
                    Tasks.whenAllSuccess(task.getResult()).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            Log.i(TAG,"Fetching user's groups returned a list of size: "+objects.size());
                            if(objects.size()>0){
                                myGroups = new ArrayList<>();
                                for(Object object:objects){
                                    myGroups.add((GroupBase) object);
                                }
                                Log.i(TAG,"MyGroups = "+myGroups.toString());
                                mAdapter.setData(myGroups);
                            } else {
                                // DISPLAY THE NO GROUPS ATTACHED TO USER IMAGE.
                                Log.i(TAG,"Current user has no Groups associated to them.");
                                TextView nullText = getView().findViewById(R.id.null_data_text);
                                nullText.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Log.i(TAG,"Fetching Groups for the user returned null.");
                }
            }
        });
    }

    @Override
    public void onGroupClick(int position, GroupBase groupBase, View sharedView) {
        Fragment detailedGroup = Detailed_Group_Fragment.newInstance(groupBase,
                sharedView.getTransitionName());

        detailedGroup.setSharedElementEnterTransition(TransitionInflater
                .from(getActivity()).inflateTransition(android.R.transition.move));
        //detailedGroup.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(sharedView,sharedView.getTransitionName())
                .replace(R.id.core_frame,detailedGroup)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
        }
    }

}