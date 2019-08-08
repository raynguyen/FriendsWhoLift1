package apps.raymond.kinect.Interfaces;

import java.util.List;

import apps.raymond.kinect.MapsPackage.Location_Model;

public interface LocationsListenerInterface {
    void setLocations(List<Location_Model> locations);
    void onLocationClick(Location_Model location);
}
