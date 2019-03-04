package apps.raymond.friendswholift.Groups;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class Groups_ViewModel extends ViewModel {
    private MutableLiveData<List<GroupBase>> groups = new MutableLiveData<>();

    public void setGroups(List<GroupBase> groups){
        this.groups.setValue(groups);
    }

    public LiveData<List<GroupBase>> getGroups(){
        return groups;
    }

}
