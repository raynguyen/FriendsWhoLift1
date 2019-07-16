/*
 * ToDo:
 * Consider consolidating this Activity with the Explore_Fragment. Set the architecture such that
 * the Maps_Activity loads the map and requests permissions as required, but also overlay one of two
 * fragments depending on the startActivity intent.
 *
 * Fragment1: Search nearby events Fragment that loads data and passes back to the activity to populate
 * the event locations with markers.
 *
 * Fragment2: Search functionality to call the Geolocate function and return search results.
 *
 * ToDo:
 * Filter out events that the user is already in attendance or invited to.
 */
package apps.raymond.kinect.CoreFragments;
//https://github.com/google/android-transition-examples/tree/master/GridToPager
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.Interfaces.ExploreEventsInterface;
import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

/*
ToDo: Test changes when deleting the suggested event from the list held by the view model. We want
 to ensure that the RecyclerFragment and the PagerFragment behave properly.
 Update the views pertaining to the information of public events.
 *We do not need to animate the map when attending the event. Consider moving back to the RecyclerFragment
 *after the user hits the Attend button (should scroll to the item in the position below the accepted
 * event).
 */
public class Explore_Fragment extends BaseMap_Fragment implements OnMapReadyCallback,
        ExploreEventsInterface {
    private static final String TAG = "EventsSearchFragment";
    public static int mCurrentPosition;

    private Core_ViewModel mViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(Core_ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore,container,false);
        mMapView = view.findViewById(R.id.map_explore_fragment);

        Fragment recyclerFragment = new ExploreRecycler_Fragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container_explore_fragments, recyclerFragment, null)
                .commit();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getUserModel().observe(requireActivity(), (@Nullable User_Model userModel) -> {
            if(userModel !=null ){
                String userID = userModel.getEmail();
                mViewModel.loadUserConnections(userID); //Will need to determine events that are popular with friends.
                mViewModel.loadPublicEvents();
            }
        });

        mViewModel.getSuggestedEvents().observe(this,(@Nullable List<Event_Model> event_models)->{
            Log.w(TAG,"Change in suggested events.");
            if(event_models != null){
                addEventsToMap(event_models);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        View btnLocation = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent())
                .findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) btnLocation.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_END,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_START,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 40);

        mMap.setOnMarkerClickListener((Marker marker)->{
            Event_Model event = (Event_Model) marker.getTag();
            LatLng latLng = new LatLng(event.getLat(), event.getLng());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
            return true;
        });
    }

    /**
     * Method to create Markers on the google map. Each marker is set with a tag which corresponds
     * to the event of which it was created for.
     * @param events list of events that require a marker be created and placed on the map.
     */
    private void addEventsToMap(List<Event_Model> events){
        if(events != null){
            for(Event_Model event: events){
                LatLng latLng = new LatLng(event.getLat(), event.getLng());
                if(!mMarkersMap.containsKey(latLng)){

                    //Todo: Some error occurs here when the app is minimized and restarted.
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .title(event.getName()));
                    marker.setTag(event);
                    mMarkersMap.put(latLng, marker);
                }
            }
        }
    }

    @Override
    public void setItemPosition(int pos) {
        mCurrentPosition = pos;
    }

    @Override
    public int getItemPosition() {
        return mCurrentPosition;
    }

    @Override
    public void animateMapToLocation() {
        if(mViewModel.getSuggestedEvents() != null){
            Event_Model event = mViewModel.getSuggestedEvents().getValue().get(mCurrentPosition);
            LatLng latLng = new LatLng(event.getLat(), event.getLng());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        }
    }

    @Override
    public void attendEvent() {
        Log.w(TAG,"Run code to attend event in position: " + mCurrentPosition);
        Event_Model event = mViewModel.getSuggestedEvents().getValue().get(mCurrentPosition);
        mViewModel.addUserToEvent(event).addOnCompleteListener((@NonNull Task<Void> task) -> {
            mViewModel.deleteSuggestedEvent(mCurrentPosition);
        });
    }
}