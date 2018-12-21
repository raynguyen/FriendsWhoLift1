package apps.raymond.friendswholift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LiftDAO extends LiftsDBDAO{

    private static final SimpleDateFormat dateformat = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    public static final String WHERE_ID_EQUALS = DBHelper.ID_COL + " =?";


    public LiftDAO(Context context){
        super(context);
    }

    //The id of the object in the database is a datatype long/
    public long save(Lift lift){
        ContentValues values = new ContentValues();
        values.put(DBHelper.DATE_COL, dateformat.format(lift.getDate()));
        values.put(DBHelper.WEIGHT_COL, lift.getWeight());
        return database.insert(DBHelper.TABLE_NAME, null, values);
    }

    //This method will update a particular lift entry in the database.
    public long update(Lift lift){
        ContentValues values = new ContentValues();
        values.put(DBHelper.DATE_COL, dateformat.format(lift.getDate()));
        values.put(DBHelper.WEIGHT_COL, lift.getWeight());

        long result = database.update(DBHelper.TABLE_NAME, values, WHERE_ID_EQUALS,
                new String[] {String.valueOf(lift.getId())});

        Log.d("Update Result:", "=" + result);
        return result;
    }

    //This method is called on long click event on LiftListFragment
    public int delete(Lift lift){
        return database.delete(DBHelper.TABLE_NAME, WHERE_ID_EQUALS,
                new String[] {lift.getId() + ""});
    }

    //Will retrieve a single lift object from the database depending on id passed when called.
    public Lift getLift(long id){
        Lift lift = null;

        String sql = "SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE " + DBHelper.ID_COL + " =?";

        Cursor cursor = database.rawQuery(sql, new String[] {id + "" });

        if(cursor.moveToNext()){
            lift = new Lift();
            lift.setId(cursor.getInt(0));
            try { lift.setDate(dateformat.parse(cursor.getString(1)));}
            catch (ParseException e){
                lift.setDate(null);
            }
            lift.setWeight(cursor.getString(2));
        }
        return lift;
    }

    public ArrayList<Lift> getLifts() {
        ArrayList<Lift> lifts = new ArrayList<Lift>();

        Cursor cursor = database.query(DBHelper.TABLE_NAME,
                new String[]{
                        DBHelper.ID_COL, DBHelper.DATE_COL, DBHelper.WEIGHT_COL },
                null, null, null, null, null);

        while(cursor.moveToNext()){
        Lift lift = new Lift();
        lift.setId(cursor.getInt(0));
        lift.setWeight(cursor.getString(1));
        try {
            lift.setDate(dateformat.parse(cursor.getString(2)));
        } catch (ParseException e) {
            lift.setDate(null);
        }
        lifts.add(lift);
        }

        return lifts;
    }
}
