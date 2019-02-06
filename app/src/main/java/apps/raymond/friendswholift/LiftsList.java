package apps.raymond.friendswholift;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import apps.raymond.friendswholift.DialogFragments.EditListItem;
import apps.raymond.friendswholift.Interfaces.MyInterface;

/*
Class that generates the ListView view when 'Log' button is clicked.
ToDo: Convert activity to a fragment.
 */
public class LiftsList extends AppCompatActivity implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, MyInterface {

    private String itemID;
    final String[] from = {"type", "weight"};
    final int[] to = {R.id.type_text, R.id.weight_text};
    private DialogFragment newFragment;
    private DataBaseHelper dataBaseHelper;
    private CustomLiftAdapter customLiftAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lift_list);

        //This cursor contains all the data from our SQLite database lifts table.
        Log.d("Tag","Instantiating databasehelper.");
        this.dataBaseHelper = new DataBaseHelper(this);
        Cursor data = dataBaseHelper.getAllLifts();

        ListView listView = findViewById(R.id.list);

        customLiftAdapter = new CustomLiftAdapter(this,
                R.layout.lift_details, data, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(customLiftAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long arg3){
        Toast.makeText(LiftsList.this, "You clicked an item for short time.",Toast.LENGTH_SHORT).show();
    }

    /*
    Opens a dialog that will prompt the user if they want to delete an entry to the database.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //We retrieve the id of the item in order to delete it from the database.
        itemID = ((TextView) view.findViewById(R.id.id_text)).getText().toString();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("EditItemDialog");
        if (prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        newFragment = EditListItem.newInstance();
        newFragment.show(ft, "EditItemDialog");

        Toast.makeText(LiftsList.this, "You clicked an item for long time.",Toast.LENGTH_SHORT).show();
        return false;
    }

    /*
    Once an entry is deleted from the database, we need to instantiate a new cursor to reflect the
    change in the database. The old cursor simply contains a copy of the data at the time of its instantiation so it does
    not know when the database is updated.
    Calling changecursor on our adapter will replace the old cursor and close it.
     */
    public void UpdateListView(){
        //We need to create a new Cursor and recollect the data, then switch the old cursor with the new.
        Cursor newcursor = dataBaseHelper.getAllLifts();
        customLiftAdapter.changeCursor(newcursor);
        Toast.makeText(LiftsList.this, "Ran UpdateScreen",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void DeleteItem(Context context){
        //The id is passed from ListView via Interface.
        Log.d("Tag","Attempting to delete SQLite table item id: " + itemID);

        //Why do I have to instantiate a new DataBaseHelper here?
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        boolean deleteLift = dataBaseHelper.RemoveLift(context, itemID);

        if (deleteLift){
            Toast.makeText(LiftsList.this, "Entry deleted.",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LiftsList.this, "Error occurred.",Toast.LENGTH_SHORT).show();
        }
        UpdateListView();

        newFragment.dismiss();
    }

    public void TestMethod(Context context){
        Toast.makeText(context,"test",Toast.LENGTH_SHORT).show();
        newFragment.dismiss();
    }
}
