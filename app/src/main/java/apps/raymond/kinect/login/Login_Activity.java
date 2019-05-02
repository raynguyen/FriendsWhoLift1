/*
 * ToDo:
 * Create the ViewModel for the Login and SignUp fragments in this context and access it through the
 * fragments.
 *
 * There is a suspicious event after signing up, there could be two activities starting somehow.
 */

package apps.raymond.kinect.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.R;
import apps.raymond.kinect.Repository_ViewModel;

public class Login_Activity extends AppCompatActivity implements Login_Fragment.SignIn, SignUpFrag.SignIn{
    private static final String TAG = "Login_Activity";

    Repository_ViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceStance){
        super.onCreate(savedInstanceStance);
        setContentView(R.layout.login_activity);

        viewModel = new Repository_ViewModel();
        LoginPagerAdapter loginAdapter = new LoginPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.login_container);
        mViewPager.setAdapter(loginAdapter);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"Login_Activity started.");
    }

    @Override
    public void signedIn() {
        final SharedPreferences userPreferences = getPreferences(MODE_PRIVATE);
        Intent mainIntent = new Intent(Login_Activity.this, Core_Activity.class);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            View v = getCurrentFocus();
            if(v instanceof EditText){
                v.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(),0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}