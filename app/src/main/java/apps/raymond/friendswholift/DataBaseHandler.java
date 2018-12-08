package apps.raymond.friendswholift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {

    public static final String LIFTSTABLE_NAME = "liftstable";
    public static final String LIFTTABLE_DATE_COL = "liftdate";

    public DataBaseHandler(Context context){
        super(context,"LiftsDataBase.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase liftsdb){
        liftsdb.execSQL(
                "create table liftstable" +
                        "(id integer primary key, type text, weight text)"
        );
    }

    public void onUpgrade(SQLiteDatabase liftsdb, int oldVersion, int newVersion){
        liftsdb.execSQL(
                "DROP TABLE IF EXISTS liftstable"
        );
        onCreate(liftsdb);
    }

    public boolean insertlift(String type, String weight){
        SQLiteDatabase liftsdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", type);
        contentValues.put("weight", weight);
        liftsdb.insert(LIFTSTABLE_NAME,null,contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase liftsdb = this.getReadableDatabase();
        Cursor res = liftsdb.rawQuery("select * from liftstable where id="+id+"",null);
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase liftsdb = this.getReadableDatabase();
        int numrows = (int) DatabaseUtils.queryNumEntries(liftsdb,LIFTSTABLE_NAME);
        return numrows;
    }

    public boolean updateLift(Integer id, String type, String weight){
        SQLiteDatabase liftsdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", type);
        contentValues.put("weight", weight);
        liftsdb.update(LIFTSTABLE_NAME,contentValues,"id = ? ",new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteLift(Integer id){
        SQLiteDatabase liftsdb = this.getWritableDatabase();
        return liftsdb.delete(LIFTSTABLE_NAME,"id = ? ",new String[]{ Integer.toString(id)});
    }

    public ArrayList<String> getLiftsHistory(){
        ArrayList<String> lifts_array = new ArrayList<>();

        SQLiteDatabase liftsdb = this.getReadableDatabase();
        Cursor res = liftsdb.rawQuery( "select * from liftstable", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            lifts_array.add(res.getString(res.getColumnIndex(LIFTTABLE_DATE_COL)));
            res.moveToNext();
        }
        return lifts_array;
    }

}
