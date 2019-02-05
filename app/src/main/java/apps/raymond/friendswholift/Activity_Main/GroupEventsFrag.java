package apps.raymond.friendswholift.Activity_Main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.raymond.friendswholift.R;

public class GroupEventsFrag extends Fragment {

    RecyclerView eventsRecycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_event_frag, container,false);

        eventsRecycler = view.findViewById(R.id.events_Recycler);

        return view;
    }
}
