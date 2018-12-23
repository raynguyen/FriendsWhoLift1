package apps.raymond.friendswholift;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Lifts_db.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "lifts_table";
    private static final String COL_1 = "_ID";
    private static final String COL_2 = "type";
    private static final String COL_3 = "weight";

    public DataBaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d ("DBCREATION","Create table in database.");
        String CREATE_TABLE_LIFTS = "CREATE TABLE "
                + TABLE_NAME + " (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_2 + " TEXT, " + COL_3 + " REAL)";
        db.execSQL(CREATE_TABLE_LIFTS);
    }

    //This method is only called when the db version is incremented.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            }
}
