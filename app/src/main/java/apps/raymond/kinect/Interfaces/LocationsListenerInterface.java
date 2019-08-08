package apps.raymond.kinect.Interfaces;

import java.util.List;

import apps.raymond.kinect.MapsPackage.Location_Model;

public interface LocationsListenerInterface {
    void setLocations(List<Location_Model> locations);
    void onLocationClick(Location_Model location);
}

/*
ToDo:
 Need to determine a pattern that allows the LocationsRecycler_Fragment to update observe a specific
 ViewModel without directly requiring the Fragment class to distinguish between which ViewModel is
 being observed (i.e. we want the parent activity to observe a ViewModel and cascade changes in the
 Locations data set to any children LocationsRecycler_Fragments.
 */