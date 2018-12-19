package apps.raymond.friendswholift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LiftDAO extends LiftsDBDAO{

    private static final SimpleDateFormat dateformat = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

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

    public ArrayList<Lift> getLifts() {
        ArrayList<Lift> lifts = new ArrayList<Lift>();

        Cursor cursor = database.query(DBHelper.TABLE_NAME,
                new String[]{DBHelper.ID_COL, DBHelper.WEIGHT_COL, DBHelper.DATE_COL, DBHelper.TYPE_COL},
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
