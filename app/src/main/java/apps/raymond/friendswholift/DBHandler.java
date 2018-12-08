package apps.raymond.friendswholift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "LIFTSTABLE";
    public static final String DATE_COL = "liftdate";
    public static final String TYPE_COL = "lifttype";
    public static final String WEIGHT_COL = "liftweight";

    public DBHandler(Context context){
        super(context,"LiftsDataBase.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase liftsdb){
        //What exactly is does this String do in PHP?
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TODO TEXT, DETAIL TEXT)";
        liftsdb.execSQL(createTable);
    }

    public void onUpgrade(SQLiteDatabase liftsdb, int oldVersion, int newVersion){
        liftsdb.execSQL("DROP TABLE IF EXISTS liftstable");
        onCreate(liftsdb);
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
