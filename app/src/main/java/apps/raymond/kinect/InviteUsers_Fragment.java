package apps.raymond.kinect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import apps.raymond.kinect.ObjectModels.User_Model;

/**
 * Currently only called in GroupDetail_Fragment to invite new users into the group.
 */
public class InviteUsers_Fragment extends Fragment implements ProfileRecyclerAdapter.ProfileClickListener {
    private final static String TAG = "InviteUsersFragment";
    private final static String USER_LIST = "UsersList";

    public static InviteUsers_Fragment newInstance(ArrayList<User_Model> userModelList){
        InviteUsers_Fragment fragment = new InviteUsers_Fragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(USER_LIST,userModelList);
        fragment.setArguments(args);
        return fragment;
    }

    public InviteUsers_Fragment(){
    }

    ArrayList<User_Model> userModelList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModelList = getArguments().getParcelableArrayList(USER_LIST);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.invite_users_fragment, container,false);
    }

    RecyclerView recyclerView;
    ProfileRecyclerAdapter pAdapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.simple_users_recycler);
        pAdapter = new ProfileRecyclerAdapter(this);
        recyclerView.setAdapter(pAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onProfileClick(User_Model userModel) {
        Log.i(TAG,"hello you clicked on a profile!");
    }

    @Override
    public void onProfileLongClick(User_Model userModel) {

    }
}
