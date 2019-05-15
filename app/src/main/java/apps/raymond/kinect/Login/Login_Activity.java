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
import apps.raymond.kinect.Core_ViewModel;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;

public class Login_Activity extends AppCompatActivity implements
        SignInCallback_Fragment.SignInCallback{

    @Override
    protected void onCreate(Bundle savedInstanceStance){
        super.onCreate(savedInstanceStance);
        setContentView(R.layout.login_activity);
        LoginPagerAdapter loginAdapter = new LoginPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.viewpager_login);
        mViewPager.setAdapter(loginAdapter);
        mViewPager.setCurrentItem(0);

        final Login_ViewModel mViewModel = ViewModelProviders.of(this).get(Login_ViewModel.class);
        mViewModel.getCurrentUser().observe(this, new Observer<User_Model>() {
            @Override
            public void onChanged(@Nullable User_Model user_model) {
                Log.w("LoginActivity","There was a noticed change in the login observer.");
                Log.w("LoginActivity","New user is: "+user_model.getEmail());
                signInTest(mViewModel.getCurrentUser().getValue());
            }
        });

    }


    public void signInTest(User_Model user) {
        final SharedPreferences userPreferences = getPreferences(MODE_PRIVATE);
        Intent mainIntent = new Intent(Login_Activity.this, Core_Activity.class);
        mainIntent.putExtra(Core_Activity.USER,user);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
        finish();
    }

    @Override
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