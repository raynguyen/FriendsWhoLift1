package apps.raymond.kinect.DialogFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class SearchUsersDialog extends DialogFragment {
    private static final String TAG = "SearchUsersDialog";

    public SearchUsersDialog(){}

    public static SearchUsersDialog newInstance(){
        SearchUsersDialog dialog =  new SearchUsersDialog();
        //Some arguments for dialog here?
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    List<User_Model> usersList;
    RecyclerView usersRecycler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_users_dialog,container,false);

        List<String> testList = new ArrayList<>();
        usersRecycler = view.findViewById(R.id.recycler_invite_users);
        usersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //AddUsers_Adapter adapter = new AddUsers_Adapter(SOME_LIST_OF_USERMODELS);
        //usersRecycler.setAdapter(adapter);

        //When an item inside the usersRecycler is clicked, we need to check it and add to arraylist.
        return view;
    }
}
