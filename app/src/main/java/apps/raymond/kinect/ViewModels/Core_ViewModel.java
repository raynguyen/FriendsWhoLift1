package apps.raymond.kinect.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import apps.raymond.kinect.Events.Event_Model;
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
    private MutableLiveData<List<Event_Model>> mMyEvents = new MutableLiveData<>();
    private static MutableLiveData<List<Event_Model>> mNewEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event_Model>> mPopularFeed = new MutableLiveData<>();

    public Core_ViewModel(){
        mRepository = new Core_FireBaseRepo();
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
        return mRepository.addUserToEvent(userID, userModel, eventID)
                .addOnCompleteListener((@NonNull Task<Void> task) ->
                        mRepository.addEventToUser(userID, event));

    }

    public void deleteEventInvitation(String userID, String eventName){
        mRepository.deleteEventInvitation(userID, eventName);
    }

    /**
     * Query the database for a list of public events (limit currently set to 50). Upon successful
     * retrieval, the View_Model instance sets the mNewEvents with the query's result.
     */
    public void loadNewEvents(){
        mRepository.loadNewEvents().addOnCompleteListener((@NonNull Task<List<Event_Model>> task)->{
            if(task.getResult()!=null){
                List<Event_Model> acceptedEvents = mMyEvents.getValue();
                List<Event_Model> invitedEvents = mEventInvitations.getValue();
                if(acceptedEvents !=null){
                    new FilterEventsTask(task.getResult(), acceptedEvents).execute();
                }

                if(invitedEvents !=null){
                    //Have to delete an event invitation if the user joins it from explore.
                }

            }
        });
    }

    public MutableLiveData<List<Event_Model>> getNewEvents(){
        return mNewEvents;
    }

    public MutableLiveData<List<Event_Model>> getPopularFeed(){
        return mPopularFeed;
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

    public void deletePendingRequest(String userID, String profileID){
        mRepository.deletePendingRequest(userID, profileID);
    }

    //*-------------------------------------------ETC--------------------------------------------*//
    public Task<byte[]> getImage(String uri){
        return mRepository.getImage(uri);
    }
    public Task<Uri> uploadImage(Uri uri, String name){
        return mRepository.uploadImage(uri, name);
    }

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
                if(mInput.contains(event)){
                    Log.w(TAG,"Found an event in both the input and filter: "+event.getName());
                    mInput.remove(event);
                }
            }
            return mInput;
        }

        @Override
        protected void onPostExecute(List<Event_Model> result) {
            super.onPostExecute(result);
            mNewEvents.setValue(result);
        }
    }


}


