package apps.raymond.friendswholift;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LiftAddFragment extends Fragment implements View.OnClickListener {
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    private EditText dateText;
    private EditText weightText;
    private Button saveButton;
    private Button cancelButton;

    private LiftDAO liftDAO;
    private AddLiftTask task;

    DatePickerDialog datePickerDialog;
    Calendar dateCalendar;

    Lift lift = null;


    public static final String ARG_ITEM_ID = "lift_add_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        liftDAO = new LiftDAO(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_addlift, container,false);

        dateText = rootView.findViewById(R.id.input_date);
        weightText = rootView.findViewById(R.id.input_weight);
        saveButton = rootView.findViewById(R.id.save_button);
        cancelButton = rootView.findViewById(R.id.cancel_button);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        dateText.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateCalendar = Calendar.getInstance();
                dateCalendar.set(year, month, dayOfMonth);
                dateText.setText(formatter.format(dateCalendar.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR),newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        if (savedInstanceState != null) {
            dateCalendar = Calendar.getInstance();
            if (savedInstanceState.getLong("dateCalendar") != 0)
                dateCalendar.setTime(new Date(savedInstanceState
                        .getLong("dateCalendar")));
        }


        return rootView;
    }

    @Override
    public void onClick(View view){
        if(view == dateText){
            datePickerDialog.show();
        } else if (view == saveButton){
            setLift();
            Toast.makeText(getActivity(), "save",
                    Toast.LENGTH_LONG).show();
            task = new AddLiftTask(getActivity());
            task.execute((Void)null);
        } else if (view == cancelButton){
            //return 0;
            //How do we close the fragment?
            Toast.makeText(getActivity(), "LEAVE",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setLift(){
        lift = new Lift();
        lift.setWeight(weightText.getText().toString());
        if(dateCalendar != null)
            lift.setDate(dateCalendar.getTime());
    }


    public class AddLiftTask extends AsyncTask<Void, Void, Long> {

        private final WeakReference<Activity> activityWeakRef;

        public AddLiftTask(Activity context) {
            this.activityWeakRef = new WeakReference<Activity>(context);
        }

        @Override
        protected Long doInBackground(Void... arg0) {
            long result = liftDAO.save(lift);
            return result;
        }

        @Override
        protected void onPostExecute(Long result) {
            if (activityWeakRef.get() != null
                    && !activityWeakRef.get().isFinishing()) {
                if (result != -1)
                    Toast.makeText(activityWeakRef.get(), "Lift Saved",
                            Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume(){
        getActivity().setTitle(R.string.add_lift);
        //getActivity().getActionBar().setTitle(R.string.add_lift);
        super.onResume();
    }

}
