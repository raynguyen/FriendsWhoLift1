package apps.raymond.kinect.Events;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TimePicker;

public class TimePickerDialog extends DialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new android.app.TimePickerDialog(requireActivity(),this,12,0,false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.i("TimePickerdialog","The set time returned: "+hourOfDay + ":"+minute);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,null);
    }
}
