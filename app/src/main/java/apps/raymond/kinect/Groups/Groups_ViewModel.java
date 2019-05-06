package apps.raymond.kinect.Groups;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class Groups_ViewModel extends ViewModel {
    private MutableLiveData<List<Group_Model>> groups = new MutableLiveData<>();

    public void setGroups(List<Group_Model> groups){
        this.groups.setValue(groups);
    }

    public LiveData<List<Group_Model>> getGroups(){
        return groups;
    }
}
