/*
 * ToDo:
 * Create the ViewModel for the Login and SignUp fragments in this context and access it through the
 * fragments.
 *
 * There is a suspicious event after signing up, there could be two activities starting somehow.
 */

package apps.raymond.kinect.Login;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import apps.raymond.kinect.Core_Activity;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class Login_Activity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceStance){
        super.onCreate(savedInstanceStance);
        setContentView(R.layout.activity_login);
        LoginPagerAdapter loginAdapter = new LoginPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.viewpager_login);
        mViewPager.setAdapter(loginAdapter);

        final Login_ViewModel mViewModel = ViewModelProviders.of(this).get(Login_ViewModel.class);

        /*
        When the login activity is first started, we listen to the User_Model live data held by the
        Login_ViewModel. This is because we expect User_Model to be null until a user successfully
        signs into their account. When a change in the User_Model is observed, the user has succeeded
        in logging into their account and the application has pulled the correct User_Model from the
        db.
         */
        mViewModel.getCurrentUser().observe(this, new Observer<User_Model>() {
            @Override
            public void onChanged(@Nullable User_Model user_model) {
                Log.w("LoginActivity","New user is: "+user_model.getEmail());
                signInTest(mViewModel.getCurrentUser().getValue());
            }
        });

    }

    /*Todo: Add checks to ensure that the User_Model!=null prior to starting the Core_Activity. Alternatively
        we can move this into Core_Activity.
    /**
     * Function that is called when a change in the ViewModel's User_Model is observed. It will pass
     * the observed User_Model object to the Core_Activity and then finish this activity instance.
     * @param user The observed User_Model.
     */
    private void signInTest(User_Model user) {
        SharedPreferences userPreferences = getPreferences(MODE_PRIVATE);
        Intent mainIntent = new Intent(Login_Activity.this, Core_Activity.class);
        mainIntent.putExtra(Core_Activity.USER,user);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
        finish();
    }

    public void signIn() {
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


    public class LoginPagerAdapter extends FragmentPagerAdapter {

        private LoginPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            switch(position){
                case 0:
                    return new Login_Fragment();
                case 1:
                    return new SignUp_Fragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}