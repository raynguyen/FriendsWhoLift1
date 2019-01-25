/*
Never pass context into ViewModel instances. Do not store Activity, Fragment, or View instances or
their Context in the ViewModel.An Activity can be destroyed and created many times during the
lifecycle of a ViewModel as the device is rotated. If you store a reference to the Activity in the
ViewModel, you end up with references that point to the destroyed Activity. This is a memory leak.
 */
package apps.raymond.friendswholift.StatSQLClasses;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class StatViewModel extends AndroidViewModel {

    //private member to hold reference to the Repository.
    private StatRepository mRepository;
    private LiveData<List<StatEntity>> mAllStats; //Used to cache a list of Stats.

    public StatViewModel(Application application){
        super(application);
        mRepository = new StatRepository(application);
        mAllStats = mRepository.getAllStats(); //We retrieve a list of Stats from the repository.
    }

    public LiveData<List<StatEntity>> getAllStats(){
        return mAllStats;
    }

    public void insert(StatEntity statEntity){
        mRepository.insert(statEntity);
    }
}
