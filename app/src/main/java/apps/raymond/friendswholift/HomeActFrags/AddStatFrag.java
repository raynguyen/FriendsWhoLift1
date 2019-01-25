package apps.raymond.friendswholift.HomeActFrags;

import android.support.v4.app.DialogFragment;

import apps.raymond.friendswholift.StatSQLClasses.StatEntity;

public class AddStatFrag extends DialogFragment {

    public interface AddStat{
        StatEntity newStat();
    }


}
