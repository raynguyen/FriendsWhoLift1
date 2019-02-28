package apps.raymond.friendswholift.Events;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerDialog extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {
    private FetchDate dateInterface;

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
        dateInterface.fetchDate(year,month,dayOfMonth);
        // DO SOMETHING WITH DATE HERE
    }

    public interface FetchDate{
        void fetchDate(int year, int month, int day);
    }
}
