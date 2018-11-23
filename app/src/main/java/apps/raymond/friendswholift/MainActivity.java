package apps.raymond.friendswholift;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    long squatPR, benchPR, deadPR;

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
        });

        /*The snippet below creates the spinner dropdown to select the lift type.
          The dropdown is populated from an array-string that is defined the in strings resource.
          ToDo: Need to learn more about adapters! An adapter needs to be created.
        */
        Spinner liftsSpinner = findViewById(R.id.liftsSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_liftsSpinner,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        liftsSpinner.setAdapter(adapter);


        //ToDo: Need to implement onItemSelectedListener for the Spinner and execute according to selection
        //The plan is to store and overwrite data according to selection.
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
