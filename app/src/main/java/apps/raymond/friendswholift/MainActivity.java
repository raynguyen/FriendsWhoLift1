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

public class MainActivity extends AppCompatActivity implements PRDialogClass.OnPRInputInterface {

    DataBaseHelper dataBaseHelper;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String BenchPress = "Bench Press";
    public static final String DeadLift = "Dead Lift";
    public static final String Squat = "Squat";

    public String cursquatpr, curbenchpr, curdeadpr;

    TextView titletext, squatview, benchview, deadview;
    Button addPR, addBench, addDead, addSquat, checkPrefs;

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

        addPR.setOnClickListener(prupdate_dialog);
        checkPrefs.setOnClickListener(checkprlistener);
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

    @Override
    public void AddData(String weight, String prtype){
        dataBaseHelper.AddLift(weight, prtype);
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

    //REMAINS OF SQL ATTEMPT.
    /*public class MainActivity extends FragmentActivity implements
        CustomLiftDialogFrag.LiftDialogFragmentListener{

    private Fragment contentFragment;
    private LiftListFragment liftListFragment;
    private LiftAddFragment liftAddFragment;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);

        FragmentManager fragmentManager = getSupportFragmentManager();

        //On orientation change
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey("content")){
                String content= savedInstanceState.getString("content");
                if (content.equals(LiftAddFragment.ARG_ITEM_ID)){
                    if (fragmentManager.findFragmentByTag(LiftAddFragment.ARG_ITEM_ID) != null){
                        setFragmentTitle(R.string.add_lift);
                        contentFragment = fragmentManager.findFragmentByTag(
                                LiftAddFragment.ARG_ITEM_ID);
                    }
                }
            }
            if (fragmentManager.findFragmentByTag(LiftListFragment.ARG_ITEM_ID) != null){
                liftListFragment = (LiftListFragment) fragmentManager.findFragmentByTag(
                        LiftListFragment.ARG_ITEM_ID);
                contentFragment = liftListFragment;
            }
        } else {
            liftListFragment = new LiftListFragment();
            setFragmentTitle(R.string.app_name);
            switchContent(liftListFragment, LiftListFragment.ARG_ITEM_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        if (contentFragment instanceof LiftAddFragment){
            outState.putString("content", LiftAddFragment.ARG_ITEM_ID);
        } else {
            outState.putString("content",LiftListFragment.ARG_ITEM_ID);
        }
        super.onSaveInstanceState(outState);
    }

    //This menu will hold the action button to add a lift object to the database.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add:
                setFragmentTitle(R.string.add_lift);
                liftAddFragment = new LiftAddFragment();
                switchContent(liftAddFragment, LiftAddFragment.ARG_ITEM_ID);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Not entirely sure what is going on here....
    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.popBackStackImmediate())
            ;

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            transaction.replace(R.id.content_frame, fragment, tag);
            // Only EmpAddFragment is added to the back stack.
            if (!(fragment instanceof LiftListFragment)) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
            contentFragment = fragment;
        }
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0){
            super.onBackPressed();
        } else if (contentFragment instanceof LiftListFragment || fm.getBackStackEntryCount() ==0){
            onShowQuitDialog();
        }
    }

    public void onShowQuitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage("Do you want to quit?");
        builder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    protected void setFragmentTitle(int resourceId) {
        setTitle(resourceId);
        //getActionBar().setTitle(resourceId);
    }

    @Override
    public void onFinishDialog(){
        if (liftListFragment != null) {
            liftListFragment.updateView();
        }
    }
}*/