/*
This DAO is responsible for providing the API for our APP to access the data stored in the
underlying SQLite data for our app. In a graphical sense, the DAO resides between the Room database
and the underlying SQLite database.

The DAOs have to be interfaces or abstract classes because Room generates their implementation at
compile time.

Whenever we want to change the underlying data source, we can implement a new DAO without having to
modify the rest of our application classes (with the exception of our Repository?).
 */

package apps.raymond.friendswholift.StatSQLClasses;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface StatDAO {

    @Insert
    void insert(StatEntity statEntity);

    /*
    @Query("DELETE FROM stats_table")
    void deleteEntity(StatEntity statEntity);
     */

    @Delete
    void deleteStat(StatEntity statEntity);

    /*
    @Query("DELETE FROM stats_table")
    void deleteAll(StatEntity statEntity);
     */

    //Abstract method that returns a LiveData object composed of a List containing StatEntity objects.
    @Query("SELECT * from stats_table ORDER BY statValue ASC") //Retrieves list of Stats from the table.
    LiveData<List<StatEntity>> getAllStats();



}
