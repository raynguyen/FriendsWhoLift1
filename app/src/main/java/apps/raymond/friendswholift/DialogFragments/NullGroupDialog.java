/*
 * DialogFragment that is inflated when the Application attempts to start the Groups activity and
 * the user has no tags associated with them.
 *
 * Todo:
 * If the user checks the CheckBox, store the settings in the SharedPReferences of the device and
 * use it as a flag to determine if we want to show this prompt ever again.
 */

package apps.raymond.friendswholift.DialogFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import apps.raymond.friendswholift.R;

public class NullGroupDialog extends DialogFragment implements View.OnClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.group_null_dialog,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button positive = view.findViewById(R.id.positive_btn);
        Button negative = view.findViewById(R.id.negative_btn);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        boolean checked = ((CheckBox) v).isChecked();

        switch (i){
            case R.id.positive_btn:
                // Clicking this button should dismiss the DialogFragment and open the Tagging fragment/activity.
                break;
            case R.id.negative_btn:
                // Clicking this button should do nothing and just dismiss the DialogFragment.
                break;
            case R.id.show_checkBox:
                if(checked){
                    // MODIFY THE SHAREDPREFERENCES HERE
                }
        }
    }


}
