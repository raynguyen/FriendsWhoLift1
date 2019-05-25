package apps.raymond.kinect.EventCreate;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.Location_Model;

public class EventCreate_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepo = new Core_FireBaseRepo();
    private MutableLiveData<List<Location_Model>> mLocationsSet = new MutableLiveData<>();

    public void loadUserLocations(String userID){
        mRepo.getUsersLocations(userID)
                .addOnCompleteListener(new OnCompleteListener<List<Location_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Location_Model>> task) {
                        mLocationsSet.setValue(task.getResult());
                    }
                });
    }

    public MutableLiveData<List<Location_Model>> getLocationSet(){
        return mLocationsSet;
    }
}
