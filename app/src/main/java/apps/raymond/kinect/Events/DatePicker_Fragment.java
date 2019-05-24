package apps.raymond.kinect.Events;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePicker_Fragment extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {
    public static final String DAY = "DayOfMonth";
    public static final String MONTH = "MonthOfYear";
    public static final String YEAR = "Year";
    public static final String DATESTRING = "DateString";
    public static final String DATELONG = "DateLong";
    private static final String MINDATE = "MinDate";

    public static DatePicker_Fragment init(long minDate){
        DatePicker_Fragment fragment = new DatePicker_Fragment();
        Bundle args = new Bundle();
        args.putLong(MINDATE,minDate);
        fragment.setArguments(args);
        return fragment;
    }

    long minDate;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Bundle args = getArguments();
            minDate = args.getLong(MINDATE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i("DatePicker","In the oncreateDIALOG.");
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePicker_Fragment and return it
        DatePickerDialog dialog = new DatePickerDialog(requireActivity(), this, year, month, day);
        if(getTargetRequestCode()== EventCreate_Details_Fragment.START_DATE_REQUEST){
            dialog.getDatePicker().setMinDate(c.getTimeInMillis());
        }
        if(getTargetRequestCode()== EventCreate_Details_Fragment.END_DATE_REQUEST){
            dialog.getDatePicker().setMinDate(minDate);
        }
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(getTargetFragment()!=null){

            int correctedMonth = monthOfYear + 1;
            Intent intent = new Intent();
            Bundle data = new Bundle();
            data.putInt(MONTH,monthOfYear);
            data.putInt(DAY,dayOfMonth);
            data.putInt(YEAR,year);
            try {
                String dateString = dayOfMonth + "." + correctedMonth + "." + year;
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                Date _date = sdf.parse(dateString);
                data.putString(DATESTRING,dateString);
                data.putLong(DATELONG,_date.getTime());
            } catch (Exception e){
                Log.w("DatePicker_Fragment","Unexpected error: ",e);
            }
            intent.putExtras(data);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        }
    }

}
