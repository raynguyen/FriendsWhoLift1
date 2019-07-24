package apps.raymond.kinect.Invitations;

import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.kinect.R;
import apps.raymond.kinect.ObjectModels.User_Model;
import apps.raymond.kinect.Core_ViewModel;

public class ConnectionRequests_Fragment extends Fragment implements
        ConnectionRequests_Adapter.ConnectionRequestListener {

    private Core_ViewModel mViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_recycler,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressBar progressBar = view.findViewById(R.id.progress_simple);
        TextView txtNullData = view.findViewById(R.id.text_null_simple);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_simple);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ConnectionRequests_Adapter adapter = new ConnectionRequests_Adapter(this);
        recyclerView.setAdapter(adapter);

        mViewModel.getConnectionRequests().observe(this,(@Nullable List<User_Model> requests)-> {
            progressBar.setVisibility(View.GONE);
            if(requests!=null && requests.size()>0) {
                adapter.setData(requests);
            } else {
                txtNullData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRequestResponse(User_Model profile, boolean response) {
        Log.w("ConReqFrag","Clicked to respond to a request!");
        if(mViewModel.getUserModel().getValue()!=null){
            String userID = mViewModel.getUserModel().getValue().getEmail();
            String profileID = profile.getEmail();

            if(response){
                mViewModel.createUserConnection(userID, profile)
                        .addOnCompleteListener((@NonNull Task<Void> task)->
                                Toast.makeText(getContext(),"Connected to "+profileID,Toast.LENGTH_LONG).show());
            } else {
                mViewModel.deleteConnectionRequest(userID, profileID)
                        .addOnCompleteListener((@NonNull Task<Void> task)->
                            mViewModel.deletePendingRequest(userID, profileID));
            }
        }
    }

    @Override
    public void onProfileDetail(User_Model profile) {
        Log.w("ConnRequest","INFLATE DETAIL FOR profile: "+profile.getEmail());
    }
}
