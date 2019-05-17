package apps.raymond.kinect.Events;

import android.support.v4.app.Fragment;

/**
 * Fragment class with interface for event control.
 * */
public class EventControl_Fragment extends Fragment {
    public static final int INVITATION = 1;
    //Interfaces are inherently abstract.
    public interface EventControlInterface {
        void onAttendEvent(Event_Model event, int flag);
        void onDeclineEvent(Event_Model event);
    }
}
