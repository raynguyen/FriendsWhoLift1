package apps.raymond.kinect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.raymond.kinect.Events.Event_Model;
import apps.raymond.kinect.Groups.GroupBase;

public class Messages_Fragment extends Fragment {
    private static final String EVENT = "Event";
    private static final String GROUP = "Group";

    public static Messages_Fragment newInstance(Event_Model event){
        Messages_Fragment fragment = new Messages_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    public static Messages_Fragment newInstance(GroupBase groupBase){
        Messages_Fragment fragment = new Messages_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(GROUP,groupBase);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventusers_,container,false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
