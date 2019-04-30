package apps.raymond.kinect.Events;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerDialog extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {
    public static final String DAY = "DayOfMonth";
    public static final String MONTH = "MonthOfYear";
    public static final String YEAR = "Year";
    public static final String DATESTRING = "DateString";
    public static final String DATELONG = "DateLong";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new android.app.DatePickerDialog(requireActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(getTargetFragment()!=null){
            String month = new DateFormatSymbols().getMonths()[monthOfYear];
            Log.i("DatePicker", "monthOfYear returned: "+ monthOfYear);
            Log.i("DatePicker","Returned month name: "+month);

            Intent intent = new Intent();
            Bundle data = new Bundle();
            data.putInt(MONTH,monthOfYear);
            data.putInt(DAY,dayOfMonth);
            data.putInt(YEAR,year);
            try {
                String dateString = dayOfMonth + "." + monthOfYear + "." + year; //String formatted in dd/mm/yyyy
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                Date _date = sdf.parse(dateString);
                data.putString(DATESTRING,dateString);
                data.putLong(DATELONG,_date.getTime());
            } catch (Exception e){
                Log.w("DatePickerDialog","Unexpected error: ",e);
            }
            intent.putExtras(data);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        }
    }
}
