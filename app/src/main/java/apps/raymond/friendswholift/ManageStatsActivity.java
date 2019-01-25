package apps.raymond.friendswholift;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import apps.raymond.friendswholift.StatSQLClasses.StatEntity;
import apps.raymond.friendswholift.StatSQLClasses.StatViewModel;
import apps.raymond.friendswholift.StatsRecycler.StatRecyclerAdapter;

public class ManageStatsActivity extends AppCompatActivity {
    private static final String TAG = "ManageStatsAct";

    private StatViewModel mStatViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"Creating activity for the first time.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allstats_container);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.allStats_recycler);
        final StatRecyclerAdapter adapter = new StatRecyclerAdapter(ManageStatsActivity.this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ManageStatsActivity.this));

        mStatViewModel = ViewModelProviders.of(this).get(StatViewModel.class);
        mStatViewModel.getAllStats().observe(this, new Observer<List<StatEntity>>() {
            @Override
            public void onChanged(@Nullable List<StatEntity> statEntities) {
                //When we observe data changes, we want to update the cached results.
                adapter.setStats(statEntities);
            }
        });
    }
}
