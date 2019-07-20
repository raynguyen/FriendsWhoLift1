package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class Create_Fragment extends Fragment implements
    AddUsers_Adapter.CheckProfileInterface{
    private static final String TAG = "CreateFragment: ";
    private Core_ViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_create_invite);
        AddUsers_Adapter adapter = new AddUsers_Adapter(this);
        recyclerView.setAdapter(adapter);

        mViewModel.getPublicUsers().observe(requireActivity(), (List<User_Model> publicUsers) -> {
            if(publicUsers != null){
                adapter.setData(publicUsers);
            }
        });
    }

    @Override
    public void addToCheckedList(User_Model clickedUser) {
        Log.w(TAG,"ADD THE USER TO THE LIST TO INVITE..");
    }

    @Override
    public void removeFromCheckedList(User_Model clickedUser) {
        Log.w(TAG,"REMOVE USER FROM INVITE LIST..");
    }
}