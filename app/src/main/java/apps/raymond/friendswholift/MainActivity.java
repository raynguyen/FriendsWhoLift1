package apps.raymond.friendswholift;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements PRDialogClass.OnPRInputListener {
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String BenchPress = "Bench Press";
    public static final String DeadLift = "Dead Lift";
    public static final String Squat = "Squat";

    public String cursquatpr, curbenchpr, curdeadpr;


    TextView textview, squatview, benchview, deadview;
    Button promptPR, checkPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textview = findViewById(R.id.liftsSummary);
        squatview = findViewById(R.id.squatSummary);
        benchview = findViewById(R.id.benchSummary);
        deadview = findViewById(R.id.deadSummary);

        textview.setText(R.string.summary_title);

        sharedpreferences = getSharedPreferences("myprprogress", Context.MODE_PRIVATE);
        UpdateMainAct();

        promptPR = findViewById(R.id.promptPR);
        checkPrefs = findViewById(R.id.checkprefs);
        promptPR.setOnClickListener(prupdate_dialog);
        checkPrefs.setOnClickListener(checkprlistener);

    }

    public View.OnClickListener prupdate_dialog = new View.OnClickListener() {

        public void onClick(View v) {

            //Log.d(Html.TagHandler, "Attempting to create AlertDialog Fragment");
            PRDialogClass dialog = new PRDialogClass();
            dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
            dialog.show(getSupportFragmentManager(), "MyPRDialog");
        }
    };

    //ToDo: Use this button to open up another activity to view historical data!
    public View.OnClickListener checkprlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public void StorePR(String input, String prtype) {
        editor = sharedpreferences.edit();
        switch(prtype){
            //ToDo: Current case matching is not programmatic. Update this.
            case "Bench Press":
                editor.putString(BenchPress,input);
                break;
            case "Dead Lift":
                editor.putString(DeadLift, input);
                break;
            case "Squat":
                editor.putString(Squat,input);
                break;
        }
        //apply() changes the xml file in the background whereas commit() does it immediately
        editor.apply();
        UpdateMainAct();
    }

    public void UpdateMainAct(){
        //ToDo: Refactor updatemainact to determine what TextViews require updating.
        cursquatpr = getString(R.string.squat_line) +
                sharedpreferences.getString(Squat, "N/A");
        curbenchpr = getString(R.string.bench_line) +
                sharedpreferences.getString(BenchPress, "N/A");
        curdeadpr = getString(R.string.dead_line) +
                sharedpreferences.getString(DeadLift, "N/A");

        benchview.setText(curbenchpr);
        deadview.setText(curdeadpr);
        squatview.setText(cursquatpr);

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
/**
 * ToDo: Update PRDialogClass such that the TextView Header is uneditable.
 * ToDo: Add tabs to top of HomeActivity to indicate which Fragment user has active.
 **/