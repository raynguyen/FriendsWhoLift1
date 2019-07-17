/*
RESEARCH:
Consider adding LiveData objects in the Repository class. On changes, we want to observe the repo
LiveData and trigger transformations on changes. This way, the repository can fetch information via
a DAO from the backend and filter as required. This way we separate the ViewModel to simply holding
the data for the View's to observe (rather than having the ViewModel do both).

By moving the filtering of data to the Repository class, we are closer to following the MVVM architecture
as we would ideally be able to perform those queries on our database as opposed to extracting all
relevant information and filtering.
 */


package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Event_Model;
import apps.raymond.kinect.FireBaseRepo.Core_FireBaseRepo;
import apps.raymond.kinect.UserProfile.User_Model;

/**
 * ViewModel class for the core application. When an instance of this ViewModel is first created, we
 * call on the Repository to fetch the active user's accepted events, groups, and mEventModel/group
 * invitations and set them respectively to their LiveData counterparts.
 *
 * This ViewModel will also be responsible for cascading data updates to the Repository.
 *
 * ToDo: Observe the required FireStore collections in the repository. On change detections,
 *  we want to emit updates upstream to the ViewModel's LiveData via Transformations (not sure how
 *  to yet).
 *
 * ToDo: When we want to add/delete a single item from a list, we want to determine the difference
 *  between the old list and the new and update the views accordingly. This way we reduce the number
 *  of recycler view items that have to be recreated.
 *
 * ToDo: The ViewModel should expose LiveData objects to it's observers, not the Mutable form.
 */
public class Core_ViewModel extends ViewModel {
    private static final String TAG = "Core_ViewModel";
    private Core_FireBaseRepo mRepository;
    private MutableLiveData<String> mUserID = new MutableLiveData<>();
    private MutableLiveData<User_Model> mUserModel = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mConnections = new MutableLiveData<>();

    private MutableLiveData<List<Event_Model>> mEventInvitations = new MutableLiveData<>();
    private MutableLiveData<List<User_Model>> mConnectionRequests = new MutableLiveData<>();

    private MutableLiveData<List<Event_Model>> mMyEvents = new MutableLiveData<>(); //AcceptedEvents
    private MutableLiveData<List<Event_Model>> mPublicEvents = new MutableLiveData<>(); //50 public events
    private MediatorLiveData<List<Event_Model>> mPopularEvents = new MediatorLiveData<>(); //Public events filtered to only contain events your connections are attending.
    private static MutableLiveData<List<Event_Model>> mSuggestedEvents = new MutableLiveData<>(); //Public events Todo: Filter events to only contain events that primes/tags you're following.

    public Core_ViewModel(){
        mRepository = new Core_FireBaseRepo();

        //MediatorLiveData should be moved to the Repository class.
        mPopularEvents.addSource(mPublicEvents, (List<Event_Model> events) -> checkListForUser());
        mPopularEvents.addSource(mConnections, (List<User_Model> connections) -> checkListForUser());
    }

    public void setUserID(String userID){
        Log.w(TAG,"We are setting the userID for this view model instance");
        mUserID.setValue(userID);
    }

    public void setUserDocument(User_Model user_model){
        Log.w(TAG,"We have set a user document for this view model instance.");
        mUserModel.setValue(user_model);
    }

    public void loadUserDocument(String userID){
        Log.w(TAG,"We have to load a user document for this view model instance.");
        mRepository.getUserModel(userID).addOnCompleteListener((Task<User_Model> task)->{
            if(task.isSuccessful()){
                mUserModel.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<User_Model> getUserModel(){
        return mUserModel;
    }

    public void loadUserConnections(String userID){
        mRepository.getUserConnections(userID).addOnCompleteListener((@NonNull Task<List<User_Model>> task)->{
            if(task.isSuccessful()){
                mConnections.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<User_Model>> getUserConnections(){
        return mConnections;
    }

    /**
     * Query the data base to retrieve a list of the user's invitations.
     * @param userID User for whom we are retrieving the invitation sets
     */
    public void loadEventInvitations(String userID){
        mRepository.getEventInvitations(userID).addOnCompleteListener((Task<List<Event_Model>> task)->{
            if(task.isSuccessful()){
                mEventInvitations.setValue(task.getResult());
            }
        });
    }

    public MutableLiveData<List<Event_Model>> getEventInvitations(){
        return mEventInvitations;
    }

    public void loadConnectionRequests(String userID){
        mRepository.getConnectionRequests(userID)
                .addOnCompleteListener((@NonNull Task<List<User_Model>> task)-> {
                    if(task.isSuccessful()){
                        mConnectionRequests.setValue(task.getResult());
                    }
                });
    }

    public MutableLiveData<List<User_Model>> getConnectionRequests(){
        return mConnectionRequests;
    }

    public void setEventInvitations(List<Event_Model> newList){
        mEventInvitations.setValue(newList);
    }

    public void updateEventInviteDeclined(String userID, String eventName, User_Model user){
        mRepository.declineEventInvitation(eventName,userID,user);
    }

    //*------------------------------------------EVENTS------------------------------------------*//
    /**
     * Fetch the user's Event collection from the database and set the result to mMyEvents.
     * Ideally, we would be able to remove the onCompleteListener via Transformations in the Repo.
     */
    public void loadMyEvents(){
        String userID = mUserID.getValue();
        if(userID != null){
            mRepository.getAcceptedEvents(userID).addOnCompleteListener((Task<List<Event_Model>> task)->{
                if(task.isSuccessful()&& task.getResult()!=null){
                    mMyEvents.setValue(task.getResult());
                }
            });
        }
    }

    public void setMyEvents(List<Event_Model> newList) {
        mMyEvents.setValue(newList);
    }

    public MutableLiveData<List<Event_Model>> getMyEvents(){
        return mMyEvents;
    }

    /**
     * Add a user to an event document.
     * @param event The name of the event in which a user is being added to.
     * @return a task to determine if the CRUD operation was successful.
     */
    public Task<Void> addUserToEvent(Event_Model event){
        User_Model userModel = mUserModel.getValue();
        String userID = userModel.getEmail();
        String eventID = event.getName();
        return mRepository.addUserToEvent(userID, userModel, eventID, event);
    }

    /**
     * Temporary method call to add a single event object to the mMyEvents LiveData. Ideally, we
     * would simply add the event to the database and observe a change which gets emitted to this
     * view model class from the repository class.
     *
     * @param event an event being added to the user's event list.
     */
    public void addEventLiveData(Event_Model event){
        List<Event_Model> myEvents = mMyEvents.getValue();
        myEvents.add(event);
        mMyEvents.setValue(myEvents);
    }

    public void deleteEventInvitation(String userID, String eventName){
        mRepository.deleteEventInvitation(userID, eventName);
    }

    /**
     * Query the database for a list of public events (limit currently set to 50). Upon successful
     * retrieval, the View_Model instance sets the mSuggestedEvents with the query's result.
     */
    public void loadPublicEvents(){
        mRepository.getPublicEvents().addOnCompleteListener((@NonNull Task<List<Event_Model>> task)->{
            if(task.getResult()!=null){
                mPublicEvents.setValue(task.getResult());
                List<Event_Model> acceptedEvents = mMyEvents.getValue();
                if(acceptedEvents !=null){
                    new FilterEventsTask(task.getResult(), acceptedEvents).execute();
                }
            }
        });
    }

    public MutableLiveData<List<Event_Model>> getSuggestedEvents(){
        return mSuggestedEvents;
    }

    public void deleteSuggestedEvent(int position){
        List<Event_Model> suggestedEvents = mSuggestedEvents.getValue();
        if(suggestedEvents != null){
            suggestedEvents.remove(position);
        }
        mSuggestedEvents.setValue(suggestedEvents);
    }


    public Task<Boolean> checkForUser(String userID, String eventName){
        return mRepository.checkForUser(userID, eventName);
    }

    public Task<Void> createUserConnection(String userID, User_Model profile){
        return mRepository.createUserConnection(userID, profile)
                .addOnCompleteListener((@NonNull Task<Void> task)->{
                    List<User_Model> connections = mConnections.getValue();
                    if(connections!=null){
                        connections.add(profile);
                    }

                    String profileID = profile.getEmail();
                    deletePendingRequest(userID, profileID);
                    deleteConnectionRequest(userID, profileID);
                });
    }

    public Task<Void> deleteConnectionRequest(String userID, String profileID){
        return mRepository.deleteConnectionRequest(userID, profileID)
                .addOnCompleteListener((@NonNull Task<Void> task)-> {
                    List<User_Model> requests = mConnectionRequests.getValue();
                    if(requests!=null){
                        for(User_Model user : requests){
                            if(user.getEmail().equals(profileID)){
                                requests.remove(user);
                                break;
                            };
                        }
                        mConnectionRequests.setValue(requests);
                    }
                });
    }

    public Task<Void> deletePendingRequest(String userID, String profileID){
        return mRepository.deletePendingRequest(userID, profileID);
    }

    //*-------------------------------------------ETC--------------------------------------------*//
    public Task<byte[]> getImage(String uri){
        return mRepository.getImage(uri);
    }
    public Task<Uri> uploadImage(Uri uri, String name){
        return mRepository.uploadImage(uri, name);
    }

    private void checkListForUser(){
        if(mPublicEvents.getValue() == null || mConnections.getValue() == null){
            return;
        }

        List<Event_Model> results = new ArrayList<>();
        for(Event_Model event : mPublicEvents.getValue()){
            String eventID = event.getName();
            for(User_Model profileModel : mConnections.getValue()){
                String profileID = profileModel.getEmail();
                checkForUser(profileID, eventID).addOnCompleteListener((@NonNull Task<Boolean> task) -> {
                    if(task.getResult()){
                        results.add(event);
                        mPopularEvents.postValue(results);
                    }
                });
            }
        }
    }

    /**
     *
     */
    protected static class FilterEventsTask extends AsyncTask<Void, Void, List<Event_Model>>{
        private List<Event_Model> mInput;
        private List<Event_Model> mFilter;

        /**
         * Creates an AsyncTask to filter two lists of events against each other.
         * @param input list that we want to filter.
         * @param filter the list used to filter our input.
         */
        FilterEventsTask(List<Event_Model> input, List<Event_Model> filter){
            mInput = input;
            mFilter = filter;
        }

        @Override
        protected List<Event_Model> doInBackground(Void... v) {
            for(Event_Model event : mFilter){
                mInput.remove(event);
            }
            return mInput;
        }

        @Override
        protected void onPostExecute(List<Event_Model> result) {
            super.onPostExecute(result);
            mSuggestedEvents.setValue(result);
        }
    }

}


