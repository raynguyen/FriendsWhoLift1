package apps.raymond.kinect.Events;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimePickerDialog extends DialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {
    public static final String TIME_12HR = "12HrTime";
    public static final String TIMELONG = "TimeLong";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new android.app.TimePickerDialog(requireActivity(),this,12,0,false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String unformattedTime = String.format(Locale.getDefault(),"%02d:%02d",hourOfDay,minute);
        try{
            Intent data = new Intent();
            SimpleDateFormat _24HrFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat _12HrFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            Date _24HrDate = _24HrFormat.parse(unformattedTime);

            long dateLong = _24HrDate.getTime();
            String _12HrTime = _12HrFormat.format(_24HrDate);

            data.putExtra(TIME_12HR,_12HrTime);
            data.putExtra(TIMELONG,dateLong);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
        } catch (Exception e){
            Log.d("TimePicker","Error is : " + e);
        }

    }
}
