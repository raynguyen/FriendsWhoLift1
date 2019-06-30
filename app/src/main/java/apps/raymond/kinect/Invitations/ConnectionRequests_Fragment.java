package apps.raymond.kinect.Invitations;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import apps.raymond.kinect.R;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class ConnectionRequests_Fragment extends Fragment {

    private Core_ViewModel mViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
    }

    private RecyclerView mRecycler;
    private TextView txtNullData;
    private ProgressBar pbLoading;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_simple_recycler,container,false);
        mRecycler = v.findViewById(R.id.recycler_invitations);
        pbLoading = v.findViewById(R.id.progress_invitations);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtNullData = view.findViewById(R.id.text_null_data);
        txtNullData.setVisibility(View.VISIBLE);
    }


    private class ConnectionRequests_Adapter extends FragmentPagerAdapter{

        private ConnectionRequests_Adapter (FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }
}
