/*
 * On back click, should prompt the user if they want to completely discard any progress.
 */

package apps.raymond.friendswholift.Events;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import apps.raymond.friendswholift.R;

public class CreateEventFragment extends Fragment {
    private static final String TAG = "CreateEventFragment";
    public CreateEventFragment(){
    }

    public static CreateEventFragment newInstance(){
        return new CreateEventFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //Need the layout CreateEventFragment.
        return inflater.inflate(R.layout.event_create_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText nameTxt = view.findViewById(R.id.event_name_txt);
        final EditText descTxt = view.findViewById(R.id.event_desc_txt);
        final EditText dayTxt = view.findViewById(R.id.event_day);
        final EditText monthTxt = view.findViewById(R.id.event_month);

        Button saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need to save this new GroupEvent to the database!
                GroupEvent newEvent = new GroupEvent(
                        nameTxt.getText().toString(),
                        descTxt.getText().toString(),
                        monthTxt.getText().toString(),
                        dayTxt.getText().toString());
                Log.i(TAG,"Created new GroupEvent of name: "+ newEvent.getName());

                // When clicking create, call the repository method to add the event to the group document.
            }
        });
    }
}
