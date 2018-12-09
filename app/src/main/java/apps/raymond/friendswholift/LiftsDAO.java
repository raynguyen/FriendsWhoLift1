package apps.raymond.friendswholift;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LiftsDAO {

    protected SQLiteDatabase database;
    private DBHelper dbHelper;
    private Context mContext;

    public LiftsDAO(Context context){
        this.mContext=context;
        dbHelper = DBHelper.getHelper(mContext);
        open();
    }

    public void open() throws SQLException{
        if(dbHelper==null)
            dbHelper = DBHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }
}
