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
import android.widget.ImageView;
import android.widget.TextView;

import apps.raymond.friendswholift.R;


public class Event_Detail_Fragment extends Fragment {
    private static final String TAG = "Event_Detail_Fragment";
    private static final String EXTRA_EVENT_ITEM = "event_item";

    public Event_Detail_Fragment(){
    }

    public static Event_Detail_Fragment newInstance(){
        return new Event_Detail_Fragment();
    }

    GroupEvent event;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"Creating Event_Detail_Fragment.");
        Bundle args = this.getArguments();
        if(args !=null){
            this.event = args.getParcelable("EventObject");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_detail_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView eventName = view.findViewById(R.id.event_title);
        TextView eventDesc = view.findViewById(R.id.event_desc);
        TextView eventMonth = view.findViewById(R.id.event_month);
        TextView eventDay = view.findViewById(R.id.event_day);
        final ImageView groupPhoto = view.findViewById(R.id.event_banner);

        eventName.setText(event.getName());
        eventDesc.setText(event.getDesc());
        eventMonth.setText(event.getMonth());
        eventDay.setText(event.getDay());
    }
}
