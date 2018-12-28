package apps.raymond.friendswholift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import apps.raymond.friendswholift.LiftObject.LiftObject;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Lifts_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "lifts_table";
    private static final String COL_ID = "_id";
    private static final String COL_TYPE = "type";
    public static final String COL_WEIGHT = "weight";
    private static final String COL_DATE = "date";

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d ("DBCREATION","Create table in database.");
        String CREATE_TABLE_LIFTS = "CREATE TABLE "
                + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TYPE + " TEXT, " + COL_WEIGHT + " REAL, " + COL_DATE + " STRING)";
        db.execSQL(CREATE_TABLE_LIFTS);
    }

    public boolean AddLift(String weight, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TYPE,type);
        contentValues.put(COL_WEIGHT, weight);
        //if db.insert returns an error, the function call returns a '-1'
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1){
            return false;
        } else {
            return true;
        }
    }

    public LiftObject getLift(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,new String[]{COL_ID,COL_TYPE,COL_WEIGHT},
                COL_ID + "=?", new String[]{String.valueOf(id)},
                null,null,null,null);

        if (cursor == null){
            cursor.moveToFirst();
        }
        LiftObject lift = new LiftObject(cursor.getInt(cursor.getColumnIndex(COL_ID)),
                cursor.getString(cursor.getColumnIndex(COL_TYPE)),
                cursor.getDouble(cursor.getColumnIndex(COL_WEIGHT)));

        return lift;
    }

    public Cursor getAllLifts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return res;
    }

    //This method is only called when the db version is incremented.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        }
}
