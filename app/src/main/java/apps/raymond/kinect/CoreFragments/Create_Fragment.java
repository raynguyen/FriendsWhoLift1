package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import apps.raymond.kinect.ProfileRecyclerAdapter;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class Create_Fragment extends Fragment implements ProfileRecyclerAdapter.ProfileClickListener {
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
        List<User_Model> myConnections = mViewModel.getUserConnections().getValue();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_create_invite);
        ProfileRecyclerAdapter adapter = new ProfileRecyclerAdapter(this);

        if(myConnections == null){
            //mViewModel.lo
        }
    }

    @Override
    public void onProfileClick(User_Model userModel) {

    }

    @Override
    public void onProfileLongClick(User_Model userModel) {

    }
}