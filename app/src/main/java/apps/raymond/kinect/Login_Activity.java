/*
 * ToDo:
 * Create the ViewModel for the Login and SignUp fragments in this context and access it through the
 * fragments.
 *
 * There is a suspicious event after signing up, there could be two activities starting somehow.
 */

package apps.raymond.kinect;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import apps.raymond.kinect.Login.LoginFrag;
import apps.raymond.kinect.Login.LoginPagerAdapter;
import apps.raymond.kinect.Login.SignUpFrag;

public class Login_Activity extends AppCompatActivity implements LoginFrag.SignIn, SignUpFrag.SignIn {
    private static final String TAG = "Login_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceStance){
        super.onCreate(savedInstanceStance);
        setContentView(R.layout.login_activity);

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
        Log.d(TAG, "Finishing Login Activity.");
        //Intent mainIntent = new Intent(Login_Activity.this,Core_Activity.class);
        //startActivity(mainIntent);
        //finish();
    }

}
