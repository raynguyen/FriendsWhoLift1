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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.MapsPackage.BaseMap_Fragment;
import apps.raymond.kinect.R;
import apps.raymond.kinect.UserProfile.User_Model;
import apps.raymond.kinect.ViewModels.Core_ViewModel;

public class Explore_Fragment extends BaseMap_Fragment implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, Events_Adapter.EventClickListener,
        ExplorePager_Fragment.ExploreEventListener {
    private static final String TAG = "EventsSearchFragment";
    private static final int NUM_PAGES = 2;

    private Core_ViewModel mViewModel;
    private Event_Model focusedEvent; //Event retrieved from the tag of a marker the user clicked.

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

    private ViewGroup detailsCardView;
    private TextView textEventName, textDesc, textThoroughfare, textMonth, textDate, textTime;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tab_explore_events);
        ViewPager viewPager = view.findViewById(R.id.viewpager_explore);
        ExplorePagerAdapter adapter = new ExplorePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        mViewModel.getUserModel().observe(requireActivity(), (@Nullable User_Model userModel) -> {
            if(userModel !=null ){
                String userID = userModel.getEmail();
                mViewModel.loadUserConnections(userID); //Will need to determine events that are popular with friends.
                mViewModel.loadSuggestedEvents();
            }
        });

        mViewModel.getNewEvents().observe(this,(@Nullable List<Event_Model> event_models)->{
            if(event_models != null){
                addEventsToMap(event_models);
            }
        });

        mViewModel.getPopularFeed().observe(this,(@Nullable List<Event_Model> event_models)->{
            if(event_models != null){
                addEventsToMap(event_models);
            }
        });

        detailsCardView = view.findViewById(R.id.card_event_explore);
        textEventName = view.findViewById(R.id.text_event_name);
        textDesc = view.findViewById(R.id.text_description);
        textThoroughfare = view.findViewById(R.id.text_thoroughfare);
        textMonth = view.findViewById(R.id.text_month_day);
        textDate = view.findViewById(R.id.text_date);
        textTime = view.findViewById(R.id.text_time);
        ProgressBar progressAttendBtn = view.findViewById(R.id.progress_event_attend);

        Button btnAttend = view.findViewById(R.id.button_attend);
        btnAttend.setOnClickListener((View v)->{
            progressAttendBtn.setVisibility(View.VISIBLE);
            btnAttend.setEnabled(false);
            if(focusedEvent!=null){
                mViewModel.addUserToEvent(focusedEvent).addOnCompleteListener((Task<Void> task)->{
                    detailsCardView.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"Attending "+ focusedEvent.getName(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener((LatLng latlng)->{
            if(detailsCardView.getVisibility()==View.VISIBLE){
                detailsCardView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Method to create Markers on the google map. Each marker is set with a tag which corresponds
     * to the event of which it was created for.
     * @param events list of events that require a marker be created and placed on the map.
     */
    private void addEventsToMap(List<Event_Model> events){
        if(events != null){
            mMarkersMap.clear();
            //For each event in public events, if the event is not also held by the acceptedEvents
            // list, add the event to the map.
            for(Event_Model event: events){
                LatLng latLng = new LatLng(event.getLat(), event.getLng());
                Marker marker=  mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(event.getName()));
                marker.setTag(event);
                mMarkersMap.put(latLng, marker);
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

    @Override
    public void onEventDetails(Event_Model event) {
        Log.w(TAG,"Clicked on an event from one of the view pager fragments.");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),17.0f));

        focusedEvent = (Event_Model) marker.getTag();

        textEventName.setText(focusedEvent.getName());
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
        detailsCardView.setVisibility(View.VISIBLE);
        return true;
    }

    private class ExplorePagerAdapter extends FragmentStatePagerAdapter {

        private ExplorePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return ExplorePager_Fragment.newInstance(ExplorePager_Fragment.SUGGESTED);
                case 1:
                    return ExplorePager_Fragment.newInstance(ExplorePager_Fragment.POPULAR);
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}