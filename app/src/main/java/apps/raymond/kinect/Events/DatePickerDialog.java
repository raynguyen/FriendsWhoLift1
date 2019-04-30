package apps.raymond.kinect.Events;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerDialog extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {
    public static final String MONTH = "Month";
    public static final String DAY = "Day";
    public static final String YEAR = "Year";
    public static final String DATE = "Date";

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
        Calendar indate = new Calendar.Builder()
                .setDate(year, monthOfYear, dayOfMonth).build();
        String dayOfWeek = DateFormat.format("EE",indate).toString(); //Day of week string
        String date = String.format(Locale.getDefault(),"%s",dayOfMonth); //The numeric day
        String month = new DateFormatSymbols().getMonths()[monthOfYear];

        if(getTargetFragment()!=null){
            Intent intent = new Intent();
            Bundle data = new Bundle();
            data.putString(DAY,dayOfWeek);
            data.putString(DATE,date);
            data.putString(MONTH,month);
            data.putInt(YEAR,year);
            intent.putExtras(data);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        }

    }
}
