package apps.raymond.friendswholift;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        PRDialogClass.OnPRInputInterface, View.OnClickListener {

    DataBaseHelper dataBaseHelper;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String BenchPress = "Bench Press";
    public static final String DeadLift = "Dead Lift";
    public static final String Squat = "Squat";

    public String cursquatpr, curbenchpr, curdeadpr;

    TextView titletext, squatview, benchview, deadview;
    Button addPR, addBench, addDead, addSquat, checkPrefs, liftsLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate liftsdb here.
        dataBaseHelper = new DataBaseHelper(this);
        dataBaseHelper.getWritableDatabase();

        Toast.makeText(this,"made database",Toast.LENGTH_LONG).show();

        FindViews();

        titletext.setText(R.string.summary_title);

        sharedpreferences = getSharedPreferences("myprprogress", Context.MODE_PRIVATE);
        UpdateMainAct();
    }

    public void FindViews(){
        titletext = findViewById(R.id.liftsSummary);
        squatview = findViewById(R.id.squatSummary);
        benchview = findViewById(R.id.benchSummary);
        deadview = findViewById(R.id.deadSummary);

        addPR = findViewById(R.id.addPR);
        checkPrefs = findViewById(R.id.checkprefs);
        addBench = findViewById(R.id.addbench);
        addDead = findViewById(R.id.adddead);
        addSquat = findViewById(R.id.addsquat);
        liftsLog = findViewById(R.id.Lift_History);

        liftsLog.setOnClickListener(this);
        addPR.setOnClickListener(this);
        checkPrefs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        switch (b.getId()){
            case R.id.Lift_History:
                Intent listintent = new Intent(MainActivity.this, LiftsList.class);
                startActivity(listintent);
                break;
            case R.id.addPR:
                PRDialogClass PRdialog = new PRDialogClass();
                PRdialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                PRdialog.show(getSupportFragmentManager(), "MyPRDialog");
                break;
            case R.id.checkprefs:
                Intent homeintent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeintent);
                break;
        }
    }

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

    @Override
    public void AddData(String weight, String prtype){
        boolean addData = dataBaseHelper.AddLift(weight, prtype);
        if (addData == true){
            Toast.makeText(this, "New Lift stored!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    public void UpdateMainAct(){
        //ToDo: Refactor updatemainact to determine what TextViews require updating.
        curbenchpr = getString(R.string.bench_line) +
                sharedpreferences.getString(BenchPress, "N/A");
        curdeadpr = getString(R.string.dead_line) +
                sharedpreferences.getString(DeadLift, "N/A");
        cursquatpr = getString(R.string.squat_line) +
                sharedpreferences.getString(Squat, "N/A");

        benchview.setText(curbenchpr);
        deadview.setText(curdeadpr);
        squatview.setText(cursquatpr);

    }

}
