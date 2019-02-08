/*
 * On back click, should prompt the user if they want to completely discard any progress.
 *
 * Need to decide on how to select the group to attach event to.
 */

package apps.raymond.friendswholift.Events;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import apps.raymond.friendswholift.DialogFragments.YesNoDialog;
import apps.raymond.friendswholift.R;

public class CreateEventFragment extends Fragment implements View.OnClickListener, YesNoDialog.YesNoInterface {
    private static final String TAG = "CreateEventFragment";
    EditText nameTxt;
    EditText descTxt;
    EditText dayTxt;
    EditText monthTxt;

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
        nameTxt = view.findViewById(R.id.event_name_txt);
        descTxt = view.findViewById(R.id.event_desc_txt);
        dayTxt = view.findViewById(R.id.event_day);
        monthTxt = view.findViewById(R.id.event_month);

        Button saveBtn = view.findViewById(R.id.save_btn);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.save_btn:
                GroupEvent newEvent = new GroupEvent(
                        nameTxt.getText().toString(),
                        descTxt.getText().toString(),
                        monthTxt.getText().toString(),
                        dayTxt.getText().toString());
                Log.i(TAG,"Created new GroupEvent of name: "+ newEvent.getName());

                EventViewModel eventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
                eventViewModel.createEvent(newEvent); //Adds a new Event to the Events collection.
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                break;

            case R.id.cancel_btn:
                DialogFragment dialog = new YesNoDialog();
                dialog.setTargetFragment(this, 0);
                dialog.show(getActivity().getSupportFragmentManager(),"yesno_dialog");
                break;
        }
    }

    @Override
    public void positiveClick() {
        Toast.makeText(getContext(),"clicked this pos btn",Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void negativeClick() {
        // Do nothing?
    }
}
