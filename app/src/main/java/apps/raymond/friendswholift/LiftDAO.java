package apps.raymond.friendswholift;

import android.arch.persistence.room.Dao;
import android.content.Context;

@Dao
public class LiftDAO {
    private Context mContext;
    private static LiftDAO mLiftDAOInstance = null;

    public static LiftDAO newInstance(Context context){
        if (mLiftDAOInstance == null) {
            mLiftDAOInstance = new LiftDAO(context.getApplicationContext());
        }
        return mLiftDAOInstance;
    }

    private LiftDAO(Context context){
        this.mContext = context.getApplicationContext();
    }
}
