package apps.raymond.friendswholift;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CustomLiftDialogFrag extends DialogFragment {

    private EditText liftDateText;
    private EditText liftWeightText;
    private LinearLayout submitLayout;

    private Lift lift;

    private static final SimpleDateFormat dateformat = new SimpleDateFormat(
        "yyyy-MM-dd", Locale.ENGLISH);

    LiftDAO liftDAO;

    public static final String ARG_ITEM_ID = "lift_dialog_fragment";

    public interface LiftDialogFragmentListener{
        void onFinishDialog();
    }

    public CustomLiftDialogFrag(){
        //Empty dialog fragment constructed
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        liftDAO = new LiftDAO(getActivity());

        Bundle bundle = this.getArguments();
        lift = bundle.getParcelable("selectedLift");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customDialogView = inflater.inflate(R.layout.fragment_addlift, null);
        builder.setView(customDialogView);

        liftDateText = (EditText) customDialogView.findViewById(R.id.input_date);
        liftWeightText = (EditText) customDialogView.findViewById(R.id.input_weight);
        submitLayout.setVisibility(View.GONE);

        setValue();

        builder.setTitle(R.string.update_lift);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    lift.setDate(dateformat.parse(liftDateText.getText().toString()));
                } catch (ParseException e){
                    Toast.makeText(getActivity(),"Invalid Date Format!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                lift.setWeight(liftWeightText.getText().toString());
                long result = liftDAO.save(lift);
                if(result > 0 ){
                    MainActivity activity = (MainActivity) getActivity();
                    activity.onFinishDialog();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();

        return alertDialog;
    }

    private void setValue(){
        if (lift !=null ){
            liftWeightText.setText(lift.getWeight());
            liftDateText.setText(dateformat.format(lift.getDate()));
        }
    }

}
