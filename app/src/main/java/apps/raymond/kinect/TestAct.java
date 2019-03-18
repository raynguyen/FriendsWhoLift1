package apps.raymond.kinect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import apps.raymond.kinect.TestTrans.Frag1;

public class TestAct extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act);

        Frag1 frag1 = new Frag1();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.test_frame,frag1)
                .addToBackStack(null)
                .commit();
    }
}
