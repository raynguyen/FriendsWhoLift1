/*
 * This activity will be launched when the user clicks on the "Search events near me" button in the
 * core activity. The algorithm used to filter events is by:
 * 1. Proximity,
 * 2. Users interest TAGS, and
 * 3. PLACES where the user previously attended events.
 * Priority is placed in this order.
 * Here we will house two views for searching new events:
 * 1) By event card views
 * 2) As place indicators in a map view.
 */

package apps.raymond.kinect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class Search_Events_Activity extends AppCompatActivity {
    private static final String TAG = "Search_Events_Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.);
    }


}
