/*
 * Fragment class that displays the details of the Event when user clicks on an Event in the Main's
 * RecyclerView.
 * https://mikescamell.com/shared-element-transitions-part-4-recyclerview/
 * https://github.com/mikescamell/shared-element-transitions/blob/master/app/src/main/java/com/mikescamell/sharedelementtransitions/recycler_view/recycler_view_to_fragment/AnimalDetailFragment.java
 */

package apps.raymond.friendswholift.Events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.acl.Group;

import apps.raymond.friendswholift.R;

public class EventDetailFragment extends Fragment {
    private static final String TAG = "EventDetailFragment";
    private static final String EXTRA_EVENT_ITEM = "event_item";
    private static final String EXTRA_TRANSITION = "transition_name";

    public EventDetailFragment(){
    }

    public static EventDetailFragment newInstance(GroupEvent groupEvent){
        EventDetailFragment eventDetailFragment = new EventDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_EVENT_ITEM,groupEvent);
        eventDetailFragment.setArguments(bundle);
        return eventDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"Creating EventDetailFragment.");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_detail_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GroupEvent groupEvent = getArguments().getParcelable(EXTRA_EVENT_ITEM);

        TextView eventName = view.findViewById(R.id.event_title);
        TextView eventDesc = view.findViewById(R.id.event_desc);
        TextView eventMonth = view.findViewById(R.id.event_month);
        TextView eventDay = view.findViewById(R.id.event_day);

        eventName.setText(groupEvent.getName());
        eventDesc.setText(groupEvent.getDesc());
        eventMonth.setText(groupEvent.getMonth());
        eventDay.setText(groupEvent.getDay());
    }
}
