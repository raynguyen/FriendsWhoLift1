package apps.raymond.kinect.CoreFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import apps.raymond.kinect.EventDetail.EventDetail_Activity;
import apps.raymond.kinect.ObjectModels.Event_Model;
import apps.raymond.kinect.R;
import apps.raymond.kinect.ObjectModels.User_Model;
import apps.raymond.kinect.Core_ViewModel;

/**
 * Fragment that is inflated as required by the ExplorePager_Adapter. This is a simple Fragment that
 * displays the details in point form whenever the user clicks on an event from the Explore_Events
 * Fragment's recycler view.
 */
public class PagerEvent_Fragment extends Fragment {
    private static final String TAG = "PagerEventFragment";
    private static final String EVENT_MODEL = "event_model";
    private static final String POSITION = "position"; //Adapter position of this fragment.

    public interface PagerEventInterface{
        void attendEvent();
        void ignoreEvent();
        void observeEvent();
    }

    public static PagerEvent_Fragment newInstance(Event_Model event, int position){
        PagerEvent_Fragment fragment = new PagerEvent_Fragment();
        Bundle args = new Bundle();
        args.putParcelable(EVENT_MODEL, event);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    private Core_ViewModel mViewModel;
    private PagerEventInterface mInterface;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mInterface = (PagerEventInterface) getParentFragment();
        } catch (ClassCastException e){}
    }

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

        TextView textName = view.findViewById(R.id.text_event_name);
        textName.setText(event.getName());
        String transitionName = "transition_name_" + position;
        textName.setTransitionName(transitionName);

        TextView textAddress = view.findViewById(R.id.text_lookup);
        TextView textHost = view.findViewById(R.id.text_host);
        TextView textDate = view.findViewById(R.id.text_month_day);
        TextView textTime = view.findViewById(R.id.text_time);
        TextView textDesc = view.findViewById(R.id.text_event_description);


        textAddress.setText(event.getAddress());
        textHost.setText(event.getCreator());

        textDesc.setText(event.getDesc());

        Button btnAttend = view.findViewById(R.id.button_attend_event);
        Button btnIgnore = view.findViewById(R.id.button_ignore_event);
        btnAttend.setOnClickListener((View v) -> mInterface.attendEvent());

        btnIgnore.setOnClickListener((View v)->{
            Toast.makeText(getContext(), "Not yet implemented.", Toast.LENGTH_LONG).show();
        });

        view.setOnClickListener((View v) -> {
            Intent detailActivity = new Intent(getContext(), EventDetail_Activity.class);
            User_Model userModel = mViewModel.getUserModel().getValue();
            detailActivity.putExtra("user",userModel).putExtra("event_name",event.getName());
            startActivity(detailActivity);
            //Todo: shared element transition.
        });

    }
}
