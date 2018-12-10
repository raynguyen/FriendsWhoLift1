package apps.raymond.friendswholift;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LiftsDBDAO {

    protected SQLiteDatabase database;
    private DBHelper dbHelper;
    private Context mContext;

    public LiftsDBDAO(Context context){
        this.mContext=context;
        dbHelper = DBHelper.getHelper(mContext);
        open();
    }

    private void open() throws SQLException{
        if(dbHelper==null)
            dbHelper = DBHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
        database = null;
    }
}
