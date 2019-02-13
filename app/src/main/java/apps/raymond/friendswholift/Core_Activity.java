package apps.raymond.friendswholift;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


public class Core_Activity extends AppCompatActivity {
    private static final String TAG = "Core_Activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        ViewPager viewPager = findViewById(R.id.core_ViewPager);
        Core_Activity_Adapter pagerAdapter = new Core_Activity_Adapter(getSupportFragmentManager());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


}
