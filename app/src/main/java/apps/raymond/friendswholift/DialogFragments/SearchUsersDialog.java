package apps.raymond.friendswholift.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.friendswholift.Add_Users_Adapter;
import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.UserProfile.UserModel;

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

    List<UserModel> usersList;
    RecyclerView usersRecycler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_users_dialog,container,false);

        List<String> testList = new ArrayList<>();
        usersRecycler = view.findViewById(R.id.add_users_recycler);
        usersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //Add_Users_Adapter adapter = new Add_Users_Adapter(SOME_LIST_OF_USERMODELS);
        //usersRecycler.setAdapter(adapter);

        //When an item inside the usersRecycler is clicked, we need to check it and add to arraylist.
        return view;
    }
}
