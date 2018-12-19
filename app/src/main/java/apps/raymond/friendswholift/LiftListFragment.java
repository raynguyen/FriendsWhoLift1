package apps.raymond.friendswholift;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LiftListFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    public static final String ARG_ITEM_ID = "lift_list";

    Activity activity;
    ListView liftListView;
    ArrayList<Lift> lifts;

    LiftListAdapter liftListAdapter;
    LiftDAO liftDAO;

    private GetLiftTask task;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activity = getActivity();
        liftDAO = new LiftDAO(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_addlift,container,false);
        liftListView = (ListView) view.findViewById(R.id.list_lifts);

        task = new GetLiftTask(activity);
        task.execute((Void) null);

        liftListView.setOnItemClickListener(this);
        liftListView.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3){
    Lift lift = (Lift) parent.getItemAtPosition(position);

    //Use AsyncTask to delete from database.
    //Have not implemented delete method for LiftDAO.
    //liftDAO.delete(lift);
    liftListAdapter.remove(lift);
    return true;
    }

    @Override
    public void onItemClick(AdapterView<?> list, View arg1, int position, long arg3){
        Lift lift = (Lift) list.getItemAtPosition(position);

        if (lift != null){
            Bundle arguments = new Bundle();
            arguments.putParcelable("selectedLift",lift);
            CustomLiftDialogFrag customLiftDialogFragment = new CustomLiftDialogFrag();
            customLiftDialogFragment.setArguments(arguments);
            customLiftDialogFragment.show(getFragmentManager(),CustomLiftDialogFrag.ARG_ITEM_ID);
        }
    }

    @Override
    public void onResume(){
        getActivity().setTitle(R.string.app_name);
        getActivity().getActionBar().setTitle(R.string.app_name);
        super.onResume();
    }

    public class GetLiftTask extends AsyncTask<Void, Void, ArrayList<Lift>>{

        private final WeakReference<Activity> activityWeakReference;

        public GetLiftTask(Activity context){
            this.activityWeakReference = new WeakReference<Activity>(context);
        }

        //What does Void... do????
        @Override
        protected ArrayList<Lift> doInBackground(Void... arg0){
            ArrayList<Lift> liftList = liftDAO.getLifts();
            return liftList;
        }

        @Override
        protected void onPostExecute(ArrayList<Lift> liftList){
            if (activityWeakReference.get() !=null && !activityWeakReference.get().isFinishing()){
                Log.d("lifts", liftList.toString());
                lifts = liftList;
                if (liftList!=null) {
                    if (liftList.size() !=0){
                        liftListAdapter = new LiftListAdapter(activity, liftList);
                        liftListView.setAdapter(liftListAdapter);
                    } else {
                        Toast.makeText(activity, "No Lift Records!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void updateView(){
        task = new GetLiftTask(activity);
        task.execute((Void) null);
    }

}
