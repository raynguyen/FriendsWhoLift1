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

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UIResources.Margin_Decoration_RecyclerView;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;
//ExplorePager_FragmentOLD.ExploreEventListener,
public class Explore_Fragment extends BaseMap_Fragment implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, Events_Adapter.EventClickListener,
         ExplorePager_Fragment.PagerFragmentInterface {
    private static final String TAG = "EventsSearchFragment";
    public static int currentPos;

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
        View view = inflater.inflate(R.layout.fragment_explore_events,container,false);
        mMapView = view.findViewById(R.id.map_explore_fragment);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_suggested_events);
        ExploreEvents_Adapter adapter = new ExploreEvents_Adapter(this);
        Margin_Decoration_RecyclerView decorationItem = new Margin_Decoration_RecyclerView();
        recyclerView.addItemDecoration(decorationItem);
        recyclerView.setAdapter(adapter);

        mViewModel.getUserModel().observe(requireActivity(), (@Nullable User_Model userModel) -> {
            if(userModel !=null ){
                String userID = userModel.getEmail();
                mViewModel.loadUserConnections(userID); //Will need to determine events that are popular with friends.
                mViewModel.loadPublicEvents();
            }
        });

        mViewModel.getSuggestedEvents().observe(this,(@Nullable List<Event_Model> event_models)->{
            if(event_models != null){
                addEventsToMap(event_models);
                adapter.setData(event_models);
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


        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener((LatLng latlng)->{
            //NOTHING ATM
            /*if(detailsCardView.getVisibility()==View.VISIBLE){
                detailsCardView.setVisibility(View.GONE);
            }*/
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
                    Log.w(TAG,"Making a marker for event: "+event.getName());
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
    public void onEventClick(Event_Model event) {
        Log.w(TAG,"Equivalent of clicking on an event marker?");
        LatLng eventLatLng = new LatLng(event.getLat(),event.getLng());
        Marker eventMarker = mMarkersMap.get(eventLatLng);
        if(eventMarker != null){
            onMarkerClick(eventMarker);
        }
    }

    /*@Override
    public void onEventDetails(Event_Model event) {
        Log.w(TAG,"Clicked on an event from one of the view pager fragments.");
        LatLng latLng = new LatLng(event.getLat(), event.getLng());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
    }*/

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),17.0f));

        //focusedEvent = (Event_Model) marker.getTag();
        /*textEventName.setText(focusedEvent.getName());
        textDesc.setText(focusedEvent.getDesc());

        if(focusedEvent.getAddress()!=null){
            textThoroughfare.setText(focusedEvent.getAddress());
        } else {
            textThoroughfare.setText("HUH");
        }

        Calendar c = Calendar.getInstance();
        Date date = new Date(focusedEvent.getLong1());
        c.setTimeInMillis(focusedEvent.getLong1());
        textMonth.setText(new DateFormatSymbols().getMonths()[c.get(Calendar.MONTH)]);
        textDate.setText(String.valueOf(c.get(Calendar.DATE)));
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a",Locale.getDefault());
        textTime.setText(sdf.format(date));
        detailsCardView.setVisibility(View.VISIBLE);*/
        return true;
    }

    @Override
    public void setCurrentPosition(int position) {
        currentPos = position;
    }

    @Override
    public int getCurrentPosition() {
        return currentPos;
    }

    /*
    private class ExplorePagerAdapter extends FragmentStatePagerAdapter {

        private ExplorePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return ExplorePager_FragmentOLD.newInstance(ExplorePager_FragmentOLD.SUGGESTED);
                case 1:
                    return ExplorePager_FragmentOLD.newInstance(ExplorePager_FragmentOLD.POPULAR);
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Suggested";
                case 1:
                    return "Popular w/ Connections";
            }
            return null;
        }
    }*/

}