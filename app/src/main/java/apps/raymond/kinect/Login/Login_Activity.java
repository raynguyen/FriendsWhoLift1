/*
 * Login/Sign-up activity. This activity contains a ViewPager that allows the user to alternate from
 * logging into the application via existing credentials with the Firebase project or registering to
 * the backend.
 *
 * When the login activity is first started, we listen to the User_Model live data held by the
 * Login_ViewModel. This is because we expect User_Model to be null until a user successfully signs
 * into their account. When the observer of the user LiveData held by the ViewModel detects a change
 * we can be sure** that a user has logged into the application as we now have their respective
 * User_Model object available. The onChange of the observer will then trigger the application to
 * finish this activity and start the Core_Activity.
 *
 * Note that we transition to the Core_Activity ONLY when the User_Model is retrieved from the DB.
 * This may not necessarily be the best algorithm as a true Login_Activity should only detect when
 * the user successfully validates their credentials into the application.
 *
 * **Not that sure :)
 */

package apps.raymond.kinect.Login;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

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

        Login_ViewModel mViewModel = ViewModelProviders.of(this).get(Login_ViewModel.class);
        mViewModel.getCurrentUser().observe(this, new Observer<User_Model>() {
            @Override
            public void onChanged(@Nullable User_Model user_model) {
                Intent mainIntent = new Intent(Login_Activity.this, Core_Activity.class);
                mainIntent.putExtra("user",user_model);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
                finish();
            }
        });

    }

    /**
     * Close the keyboard whenever the user clicks on the root view of the activity.
     * @param ev Key-press event
     *
     * ToDo: This can be refined. Have to research the best method to close the keyboard.
     */
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

    /**
     * Adapter class that inflates the Login_Fragment and SignUp_Fragment to the Login_Activity's
     * ViewPager.
     */
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