package apps.raymond.friendswholift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "liftsdb";
    public static final String TABLE_NAME = "LIFTSTABLE";

    public static final String TYPE_COL = "lifttype";
    public static final String DATE_COL = "liftdate";
    public static final String WEIGHT_COL = "liftweight";

    public static final String CREATE_LIFTS_TABLE = "CREATE TABLE " +
            TABLE_NAME + "(" + DATE_COL + " INTEGER PRIMARY KEY, " +
            WEIGHT_COL + " TEXT" + ")";

    private static DBHelper instance;

    public static synchronized DBHelper getHelper(Context context){
        if(instance==null){
            instance = new DBHelper(context);
        }
        return instance;
    }

    private DBHelper(Context context){
        //calls the constructor for DBHelper class.
        //super refers to the parent class.
        super(context, DATABASE_NAME, null, 1);
    }

    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //What exactly is does this String do in PHP?
        db.execSQL(CREATE_LIFTS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS liftstable");
        onCreate(db);
    }

    public boolean insertlift(String type, String date, String weight){
        SQLiteDatabase liftsdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", type);
        contentValues.put("date", date);
        contentValues.put("weight", weight);
        liftsdb.insert(TABLE_NAME,null,contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase liftsdb = this.getReadableDatabase();
        Cursor res = liftsdb.rawQuery("select * from liftstable where id="+id+"",null);
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase liftsdb = this.getReadableDatabase();
        int numrows = (int) DatabaseUtils.queryNumEntries(liftsdb,TABLE_NAME);
        return numrows;
    }

    public boolean updateLift(Integer id, String type, String date, String weight){
        SQLiteDatabase liftsdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", type);
        contentValues.put("date", date);
        contentValues.put("weight", weight);
        liftsdb.update(TABLE_NAME,contentValues,"id = ? ",new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteLift(Integer id){
        SQLiteDatabase liftsdb = this.getWritableDatabase();
        return liftsdb.delete(TABLE_NAME,"id = ? ",new String[]{ Integer.toString(id)});
    }

    public ArrayList<String> getLiftsHistory(){
        ArrayList<String> lifts_array = new ArrayList<>();

        SQLiteDatabase liftsdb = this.getReadableDatabase();
        Cursor res = liftsdb.rawQuery( "select * from liftstable", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            lifts_array.add(res.getString(res.getColumnIndex(DATE_COL)));
            res.moveToNext();
        }
        return lifts_array;
    }

}
