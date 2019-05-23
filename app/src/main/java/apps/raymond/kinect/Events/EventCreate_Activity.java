package apps.raymond.kinect.Events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import apps.raymond.kinect.R;

public class EventCreate_Activity extends AppCompatActivity {
    private static final String TAG ="EventCreateActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_create_frag);
    }
}
