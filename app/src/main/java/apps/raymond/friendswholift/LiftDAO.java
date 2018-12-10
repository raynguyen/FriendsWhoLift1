package apps.raymond.friendswholift;

import android.content.ContentValues;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LiftDAO extends LiftsDBDAO{

    private static final SimpleDateFormat dateformat = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.ENGLISH);

    public LiftDAO(Context context){
        super(context);
    }

    public long save(Lift lift){
        ContentValues values = new ContentValues();
        values.put(DBHelper.DATE_COL, dateformat.format(lift.getDate()));
        values.put(DBHelper.WEIGHT_COL, lift.getWeight());
        return database.insert(DBHelper.TABLE_NAME, null, values);
    }
}
