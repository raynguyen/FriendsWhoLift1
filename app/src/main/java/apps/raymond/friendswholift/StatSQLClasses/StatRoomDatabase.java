package apps.raymond.friendswholift.StatSQLClasses;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {StatEntity.class}, version = 1)
public abstract class StatRoomDatabase extends RoomDatabase {

    //The DAO for this Room Database.
    public abstract StatDAO statDAO();

    //Singleton class to ensure there is only ever one instance of our database open for this app.
    private static volatile StatRoomDatabase INSTANCE;


    static StatRoomDatabase getDataBase(final Context context){
        if (INSTANCE == null){
            synchronized (StatRoomDatabase.class){
                if(INSTANCE == null){
                    //Instantiate an instance of the data base.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StatRoomDatabase.class, "stat_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
