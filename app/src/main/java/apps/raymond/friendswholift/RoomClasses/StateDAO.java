package apps.raymond.friendswholift.RoomClasses;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface StateDAO {

    @Insert
    void insert(StatEntity statEntity);

    @Query("DELETE FROM stats_table")
    void deleteEntity(StatEntity statEntity);

    @Query("DELETE FROM stats_table")
    void deleteAll(StatEntity statEntity);

    @Query("SELECT * from stats_table ORDER BY statValue ASC")
    LiveData<List<StatEntity>> getAllStats();



}
