package apps.raymond.friendswholift;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import apps.raymond.friendswholift.StatsRecycler.StatRecyclerAdapter;

public class ManageStatsActivity extends AppCompatActivity {
    private static final String TAG = "ManageStatsAct";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"Creating activity for the first time.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allstats_container);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.allStats_recycler);
        final StatRecyclerAdapter adapter = new StatRecyclerAdapter(ManageStatsActivity.this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ManageStatsActivity.this));

    }
}
