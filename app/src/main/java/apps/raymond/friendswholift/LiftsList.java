package apps.raymond.friendswholift;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

/*
Class that generates the ListView view when 'Log' button is clicked.
 */
public class LiftsList extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
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
        listView.setClickable(true);
        listView.setOnItemLongClickListener(this);
    }

    /*
    onItemLongClick will open a dialog that will prompt user for Edit or Removal of the item from table.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(LiftsList.this, "You clicked an item for long time.",Toast.LENGTH_SHORT).show();
        return false;
    }
}