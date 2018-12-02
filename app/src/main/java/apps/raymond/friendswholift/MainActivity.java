package apps.raymond.friendswholift;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements PRDialogClass.OnPRInputListener {
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String BenchPress = "Bench Press";
    public static final String DeadLift = "Dead Lift";
    public static final String Squat = "Squat";
    public String cursquatpr = R.string.squat_line +
            sharedpreferences.getString(Squat,"N/A");
    public String curbenchpr = R.string.bench_line +
            sharedpreferences.getString(BenchPress,"N/A");
    public String curdeadpr = R.string.dead_line +
            sharedpreferences.getString(DeadLift,"N/A");

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
        squatview.setText(cursquatpr);
        benchview.setText(curbenchpr);
        deadview.setText(curdeadpr);
        sharedpreferences = getSharedPreferences("myprprogress", Context.MODE_PRIVATE);

        promptPR = findViewById(R.id.promptPR);
        checkPrefs = findViewById(R.id.checkprefs);
        promptPR.setOnClickListener(prupdate_dialog);
        checkPrefs.setOnClickListener(checkprlistener);

    }

    public View.OnClickListener prupdate_dialog = new View.OnClickListener() {

        public void onClick(View v) {

            //Log.d(Html.TagHandler, "Attempting to create AlertDialog Fragment");
            PRDialogClass dialog = new PRDialogClass();
            dialog.show(getSupportFragmentManager(), "MyPRDialog");

            /*
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



            /*

            dBuilder.setView(dView);
            //'dialog' here is not the same as those inside the onclick voids
            final AlertDialog dialog = dBuilder.create();
            dialog.show();

            //ToDo: Unable to construct the PROnClickListener object and pass prflag parameters. FIX
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new PROnClickListener());

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

    public View.OnClickListener checkprlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Maybe just open another fragment that reads from the SharedPreferences xml?
            Toast.makeText(MainActivity.this,"Hello",Toast.LENGTH_LONG).show();
        }
    };

    //storePr is defined on the DialogFragment
    @Override
    public void storePr(String input, String prtype) {
        editor = sharedpreferences.edit();
        switch(prtype){
            case "Bench Press":
                Toast.makeText(MainActivity.this, input, Toast.LENGTH_SHORT).show();
                editor.putString(BenchPress,input);
                benchview.setText(curbenchpr);
                break;
            case "Dead Lift":
                editor.putString(DeadLift, input);
                deadview.setText(curdeadpr);
                break;
            case "Squat":
                editor.putString(Squat,input);
                squatview.setText(cursquatpr);
                break;
        }
        //apply() changes the xml file in the background whereas commit() does it immediately
        editor.apply();
    }



    /*
    public class PROnClickListener implements DialogInterface.OnClickListener {
        boolean prflag = false;

        @Override
        public void onClick(DialogInterface dialog, int BUTTON_POSITIVE){

        }

        public void PROnClickListener(boolean prflag) {
            this.prflag = false;
        }
    }*/

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