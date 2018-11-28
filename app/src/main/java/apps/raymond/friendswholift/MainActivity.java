package apps.raymond.friendswholift;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


public class MainActivity extends AppCompatActivity {

    long squatPR, benchPR, deadPR;
    String newPR_type;
    Button promptPR;
    int pos;
    Boolean prflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.liftsSummary);
        TextView squatView = findViewById(R.id.squatSummary);
        TextView benchView = findViewById(R.id.benchSummary);
        TextView deadView = findViewById(R.id.deadSummary);

        squatPR = 245;//You will have to check the server for the correct value here.
        benchPR = 145;
        deadPR = 315;

        String squat_message = "Squat 1RM is : " + squatPR;
        String bench_message = "Benchpress 1RM is : " + benchPR;
        String dead_message = "Deadlift 1RM is : " + deadPR;

        textView.setText(R.string.summary_title);
        squatView.setText(squat_message);
        benchView.setText(bench_message);
        deadView.setText(dead_message);

        /*
        EditText prInput = findViewById(R.id.prInput);
        prInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView prInput, int inputDone, KeyEvent event) {
                if (inputDone == EditorInfo.IME_ACTION_DONE){
                    //Stores the value user inputs if user hits the done button
                    String inputString = prInput.getText().toString();
                    Toast.makeText(MainActivity.this, "Your pr input is: " + inputString, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });*/

        promptPR = findViewById(R.id.promptPR);
        promptPR.setOnClickListener(prupdate_dialog);

    }

    public View.OnClickListener prupdate_dialog = new View.OnClickListener() {

        public void onClick(View v) {
            final AlertDialog.Builder dBuilder = new AlertDialog.Builder(
                    MainActivity.this);

            View dView = getLayoutInflater().inflate(R.layout.pr_dialog, null);
            dBuilder.setTitle("Define your new PR");

            final EditText prEditText = findViewById(R.id.prInput);
            final String prinput = prEditText.getText().toString();

            prEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(TextUtils.isEmpty(prinput)) {
                        prEditText.setError("ARE YOU WEAK?");//You can attach a drawing to setError()
                        prflag = false;
                    } else {
                        prflag = true;
                    }
                }
            });

            final Spinner liftsSpinner = dView.findViewById(R.id.liftsSpinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    MainActivity.this,android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.array_liftsSpinner));

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            liftsSpinner.setAdapter(adapter);

            liftsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    pos = parent.getSelectedItemPosition();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });




            dBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                //When a button is clicked the common button handler forwards the click event to
                //whatever handler you passed in setButton() then calls dismisses the dialog.
                }
                });

            dBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dBuilder.setView(dView);
            //'dialog' here is not the same as those inside the onclick voids
            final AlertDialog dialog = dBuilder.create();
            dialog.show();

            // ToDo: Unable to construct the PROnClickListener object and pass prflag parameters. FIX
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new PROnClickListener(prflag));

            /*dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (pos){
                                case 0:
                                    Toast.makeText(MainActivity.this,
                                            liftsSpinner.getSelectedItem().toString(),
                                            Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:

                                    newPR_type = liftsSpinner.getSelectedItem().toString();
                                    //need a check before dialog is dismissed
                                    //dialog.dismiss();
                                    break;
                                case 2:
                                    newPR_type = liftsSpinner.getSelectedItem().toString();
                                    break;
                                case 3:
                                    newPR_type = liftsSpinner.getSelectedItem().toString();
                                    break;
                            }
                        }
                    });*/
        }
    };

    public class PROnClickListener implements View.OnClickListener {
        boolean prflag = false;

        public void PROnClickListener(boolean prflag) {
            this.prflag = false;
        }

        @Override
        public void onClick(View v){

        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}