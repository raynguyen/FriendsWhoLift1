package apps.raymond.friendswholift;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class LiftsList extends AppCompatActivity {
    private ListView listView;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lift_list);

        listView = (ListView) findViewById(R.id.list_lifts);

        dataBaseHelper = new DataBaseHelper(this);
        ArrayList<String> liftList = new ArrayList<>();

        Cursor data = dataBaseHelper.getAllLifts();

        if(data.getCount() == 0){
            Toast.makeText(this, "The database is empty.", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()){
                liftList.add(data.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<>(
                        this,android.R.layout.simple_list_item_1,liftList);
                listView.setAdapter(listAdapter);
            }
        }


    }
}
