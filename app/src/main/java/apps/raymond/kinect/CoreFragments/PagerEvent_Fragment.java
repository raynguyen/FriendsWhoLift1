package apps.raymond.kinect.CoreFragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apps.raymond.kinect.EventDetail.EventDetail_Activity;
import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;


/**
 * Fragment that is inflated as required by the ExplorePager_Adapter. This is a simple Fragment that
 * displays the details in point form whenever the user clicks on an event from the Explore_Events
 * Fragment's recycler view.
 */
public class PagerEvent_Fragment extends Fragment {
    private static final String TAG = "PagerEventFragment";
    private static final String EVENT_MODEL = "event_model";
    private static final String POSITION = "position"; //Adapter position of this fragment.

    public static PagerEvent_Fragment newInstance(Event_Model event, int position){
        PagerEvent_Fragment fragment = new PagerEvent_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(EVENT_MODEL, event);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

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
        return inflater.inflate(R.layout.cardview_explore_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Event_Model event = getArguments().getParcelable(EVENT_MODEL);
        int position = getArguments().getInt(POSITION);

        view.setOnClickListener((View v) -> {
            Intent detailActivity = new Intent(getContext(), EventDetail_Activity.class);
            User_Model userModel = mViewModel.getUserModel().getValue();
            detailActivity.putExtra("user",userModel).putExtra("event_name",event.getName());
            startActivity(detailActivity);
            //Todo: shared element transition.
        });


        TextView textName = view.findViewById(R.id.text_event_name);
        textName.setText(event.getName());
        String transitionName = "transition_name_" + position;
        textName.setTransitionName(transitionName);
        //GRAB HOLD OF VIEWS AND SET THE RELEVANT CONTENT HERE
    }
}
