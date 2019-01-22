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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityOld extends AppCompatActivity implements
        PRDialogClass.OnPRInputInterface, View.OnClickListener {

    DataBaseHelper dataBaseHelper;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String BenchPress = "Bench Press";
    public static final String DeadLift = "Dead Lift";
    public static final String Squat = "Squat";

    public String curSquatPR, curBenchPR, curDeadPR;

    TextView titleText, squatView, benchView, deadView;
    Button addPR, addBench, addDead, addSquat, checkPrefs, liftsLog;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainold);

        FirebaseApp.initializeApp(this);
        //Firebase Authenticator
        mAuth = FirebaseAuth.getInstance();

        //Instantiate liftsdb here.
        dataBaseHelper = new DataBaseHelper(this);
        dataBaseHelper.getWritableDatabase();

        Toast.makeText(this,"made database",Toast.LENGTH_LONG).show();

        FindViews();

        titleText.setText(R.string.summary_title);

        sharedpreferences = getSharedPreferences("myprprogress", Context.MODE_PRIVATE);
        UpdateMainAct();
    }

    @Override
    public void onStart(){
        super.onStart();

        //Check if user is already signed in (i.e. non-null) and launch the login Activity if not.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent loginIntent = new Intent(MainActivityOld.this, LoginAct.class);
            startActivity(loginIntent);
        }
    }

    public void FindViews(){
        titleText = findViewById(R.id.liftsSummary);
        squatView = findViewById(R.id.squatSummary);
        benchView = findViewById(R.id.benchSummary);
        deadView = findViewById(R.id.deadSummary);

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
                Intent listIntent = new Intent(MainActivityOld.this, LiftsList.class);
                startActivity(listIntent);
                break;
            case R.id.addPR:
                PRDialogClass dialogPR = new PRDialogClass();
                dialogPR.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                dialogPR.show(getSupportFragmentManager(), "MyPRDialog");
                break;
            case R.id.checkprefs:
                Intent homeIntent = new Intent(MainActivityOld.this, MainActivity.class); //CHANGE MainActivity back to HomeActivity
                startActivity(homeIntent);
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
        if (addData){
            Toast.makeText(this, "New Lift stored!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    public void UpdateMainAct(){
        //ToDo: Refactor updatemainact to determine what TextViews require updating.
        curBenchPR = getString(R.string.bench_line) +
                sharedpreferences.getString(BenchPress, "N/A");
        curDeadPR = getString(R.string.dead_line) +
                sharedpreferences.getString(DeadLift, "N/A");
        curSquatPR = getString(R.string.squat_line) +
                sharedpreferences.getString(Squat, "N/A");

        benchView.setText(curBenchPR);
        deadView.setText(curDeadPR);
        squatView.setText(curSquatPR);

    }

}
