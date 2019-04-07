package apps.raymond.kinect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import apps.raymond.kinect.UserProfile.UserModel;

public class Invite_Users_Fragment extends Fragment implements ProfileRecyclerAdapter.ProfileClickListener {
    private final static String TAG = "InviteUsersFragment";
    private final static String USER_LIST = "UsersList";

    public static Invite_Users_Fragment newInstance(ArrayList<UserModel> userModelList){
        Invite_Users_Fragment fragment = new Invite_Users_Fragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(USER_LIST,userModelList);
        fragment.setArguments(args);
        return fragment;
    }

    public Invite_Users_Fragment(){
    }

    ArrayList<UserModel> userModelList;
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
        pAdapter = new ProfileRecyclerAdapter(userModelList,this);
        recyclerView.setAdapter(pAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onProfileClick(UserModel userModel) {
        Log.i(TAG,"hello you clicked on a profile!");
    }
}
