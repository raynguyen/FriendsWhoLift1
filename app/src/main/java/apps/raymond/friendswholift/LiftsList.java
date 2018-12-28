package apps.raymond.friendswholift;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ListView;

public class LiftsList extends AppCompatActivity {
    final String[] from = {"type", "weight"};
    final int[] to = {R.id.type_text, R.id.weight_text};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lift_list);

        //This cursor contains all the data from our SQLite database lifts table.
        Log.d("Tag","Populating cursor.");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        Cursor data = dataBaseHelper.getAllLifts();

        ListView listView = (ListView) findViewById(R.id.list);

        CustomLiftAdapter customLiftAdapter = new CustomLiftAdapter(this,
                R.layout.lift_details,data, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        listView.setAdapter(customLiftAdapter);










        //We can overwrite/update the old cursor with a new cursor via:
        /*
        customLiftAdapter.changeCursor(data);
        */


        /*
        ArrayList<String> liftList = new ArrayList<>();

        if(data.getCount() == 0){
            Toast.makeText(this, "The database is empty.", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()){
                liftList.add(data.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<>(
                        this,android.R.layout.simple_list_item_1,liftList);
                listView.setAdapter(listAdapter);
            }
        }*/

    }
}
