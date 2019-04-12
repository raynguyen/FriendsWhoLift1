package apps.raymond.kinect.Events;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerDialog extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {
    private static final String SEQUENCE_TAG = "Sequence";
    public static final String SEQUENCE_START = "StartDate";
    public static final String SEQUENCE_END = "EndDate";

    private FetchDate dateInterface;
    public interface FetchDate{
        void returnStartDate(int year, int month, int day);
        void returnEndDate(int year, int month, int day);
    }

    public static DatePickerDialog newInstance(String s){
        DatePickerDialog dialog = new DatePickerDialog();
        Bundle args = new Bundle();
        args.putString(SEQUENCE_TAG, s);
        dialog.setArguments(args);
        return dialog;
    }

    String s; //A flag to determine if we will be returning the start or end date for the event.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if(args !=null){
            this.s = args.getString(SEQUENCE_TAG);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dateInterface = (FetchDate) getTargetFragment();
        // Create a new instance of DatePickerDialog and return it
        return new android.app.DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        switch (s){
            case SEQUENCE_START:
                dateInterface.returnStartDate(year,month,dayOfMonth);
                break;
            case SEQUENCE_END:
                dateInterface.returnEndDate(year,month,dayOfMonth);
                break;
        }
    }




}
