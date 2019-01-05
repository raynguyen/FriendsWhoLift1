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
import apps.raymond.friendswholift.MyInterfaces.MyInterface;

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
                R.layout.lift_details, data, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(customLiftAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    /*
    onItemLongClick will open a dialog that will prompt user for Removal of the item from table.
    OnItemClick will open a dialog (the same input_pr dialog) for the user to edit the item.
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long arg3){
        Toast.makeText(LiftsList.this, "You clicked an item for short time.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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

        newFragment.dismiss();
    }

    public void TestMethod(Context context){
        Toast.makeText(context,"test",Toast.LENGTH_SHORT).show();
        newFragment.dismiss();
    }
}

/*
onItemLongClick will open a dialog, depending on input received in dialog, we have to delete item or not.
Therefore, the dialog needs an interface to call the delete method on the item. The databasehelper will have have delete method.
 */