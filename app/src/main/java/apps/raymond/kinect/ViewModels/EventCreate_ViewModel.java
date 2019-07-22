package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

/**
 * ViewModel class that holds the information pertaining to a new Event the user is creating.
 */
public class EventCreate_ViewModel extends ViewModel {
    private Core_FireBaseRepo mRepo = new Core_FireBaseRepo();
    private MutableLiveData<List<User_Model>> mPublicUsers = new MutableLiveData<>();

    //TodO: figure out how to bind the user input to these fields.
    //These variables pertain to the user input that is updated via the views.

}
