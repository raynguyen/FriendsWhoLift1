package apps.raymond.kinect.Events;

import android.support.v4.app.Fragment;

/**
 * Fragment class with interface for event control.
 * */
public class EventControl_Fragment extends Fragment {

    //Interfaces are inherently abstract.
    public interface TestInterface{
        void newEventCallback(Event_Model event);
        void acceptInviteCallback(Event_Model event);
        void declineInviteCallback(Event_Model event);
    }
}
