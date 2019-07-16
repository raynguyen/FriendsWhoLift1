package apps.raymond.kinect.Interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface ExploreEventsInterface {
    void setItemPosition(int pos);
    int getItemPosition();
    void animateMap(LatLng latLng);

}
