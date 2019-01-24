/*
A Repository provides a layer of abstract between the App and the Data Source(s) and provides
a clean API for the rest of the app when the app needs to access its data.

When the application wants to read or write to the SQLite source, instead of direct access, we
are simply reading a 'copy' stored as member variables in the Repository. The repository data is
a LiveData object so when the source is changed the change is reflected in on DAO and therefore
the repository.

When instantiating our repository, we tell the DAO to collect all the data from our SQLite
source so that our Repository now contains it.
 */
package apps.raymond.friendswholift.StatSQLClasses;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import apps.raymond.friendswholift.StatSQLClasses.StatDAO;
import apps.raymond.friendswholift.StatSQLClasses.StatEntity;
import apps.raymond.friendswholift.StatSQLClasses.StatRoomDatabase;

public class StatRepository {

    private StatDAO mStatDAO;
    private LiveData<List<StatEntity>> mAllStats;
    /*
    We pass the application as the context when connecting this Repository to the StatRoomDatabase
    so that all components of this application may use this repository to access the StatRoomDB.
     */
    StatRepository(Application application){
        StatRoomDatabase db = StatRoomDatabase.getDataBase(application);
        mStatDAO = db.statDAO();
        mAllStats = mStatDAO.getAllStats(); //Retrieve a list of Stats from the DAO.
    }

    LiveData<List<StatEntity>> getAllStats(){
        return mAllStats; //This member variable is the data provided by our DAO.
    }

    public void insert(StatEntity statEntity){
        new insertAsyncTask(mStatDAO).execute(statEntity);
    }

    private static class insertAsyncTask extends AsyncTask<StatEntity, Void, Void>{
        private StatDAO mAsyncTaskDao;
        insertAsyncTask(StatDAO dao){
            mAsyncTaskDao = dao;
        }

        //The ellipses allow any number of StatEntity objects to be parameters for the method.
        @Override
        protected Void doInBackground(final StatEntity... params){
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

}
