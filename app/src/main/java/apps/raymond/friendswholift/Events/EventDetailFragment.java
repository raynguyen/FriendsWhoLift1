/*
 * Fragment class that displays the details of the Event when user clicks on an Event in the Main's
 * RecyclerView.
 * https://mikescamell.com/shared-element-transitions-part-4-recyclerview/
 * https://github.com/mikescamell/shared-element-transitions/blob/master/app/src/main/java/com/mikescamell/sharedelementtransitions/recycler_view/recycler_view_to_fragment/AnimalDetailFragment.java
 */

package apps.raymond.friendswholift.Events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

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
        Log.i(TAG,"Creating view for GroupEvent details.");
        return inflater.inflate(R.layout.event_detail_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG,"View successfully created.");
        GroupEvent groupEvent = getArguments().getParcelable(EXTRA_EVENT_ITEM);

        TextView eventName = view.findViewById(R.id.event_title);
        TextView eventDesc = view.findViewById(R.id.event_desc);
        TextView eventMonth = view.findViewById(R.id.event_month);
        TextView eventDay = view.findViewById(R.id.event_day);
        final ImageView groupPhoto = view.findViewById(R.id.event_banner);

        Log.i(TAG,"Description of Card: " + groupEvent.getDesc());
        Log.i(TAG,"Month of card" + groupEvent.getMonth());
        Log.i(TAG,"Day of card" + groupEvent.getDay());

        eventName.setText(groupEvent.getName());
        eventDesc.setText(groupEvent.getDesc());
        eventMonth.setText(groupEvent.getMonth());
        eventDay.setText(groupEvent.getDay());
        //Test to see how photo looks in the Fragment.
        FirebaseStorage.getInstance().getReference().child("TestGroup1/dogpic.jpg").getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.i(TAG,"Attaching testImage ImageView with downloaded file");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        groupPhoto.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Unable to retrieve the requested filed.", e);
            }
        });
    }
}
