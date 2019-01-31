package apps.raymond.friendswholift.Groups;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Set;

import apps.raymond.friendswholift.R;

public class MyGroups_Fragment extends Fragment {
    private static final String TAG = "MyGroups_Fragment";

    FirebaseUserViewModel mData;
    FirebaseUser currentUser;
    DocumentReference userDoc;
    Set<String> myKeySet;

    /*
    Recycler view here with CardViews.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deletethiswhendone,container,false);
        // Change back to R.layout.groups_cardview_container

        //RecyclerView recyclerView = view.findViewById(R.id.groups_container);

        /*
        Use a ViewModel that takes data from firebase.

         */

        Button testButton1 = view.findViewById(R.id.button1);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userDoc = FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid()) ;

        testButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"forker",Toast.LENGTH_SHORT).show();
                testMethod();

            }
        });

        mData = new FirebaseUserViewModel();

        return view;
    }

    public void testMethod(){
        Log.i(TAG,"Inside testMethod.");

        //SnapshotListener once added, will continuously listen to the document for changes.
        //When there is change, the onEvent method is triggered.
        userDoc.addSnapshotListener(getActivity(),new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.w(TAG, "Listen failed.", e);
                }
                if (documentSnapshot !=null && documentSnapshot.exists()){
                    //Log.d(TAG,"Current data: " + documentSnapshot.getData().keySet());
                    //myKeySet = documentSnapshot.getData().keySet();
                    myKeySet = mData.getGroups();

                    Log.d(TAG,"Current data via repository call: " + myKeySet);
                    // NULL OBJECT HERE SO .getGroups IS NOT RETURNING ANYTHING.
                    /*for (String key : myKeySet){
                        Log.i(TAG, "pulled this from the repository: " + key);
                    }*/
                } else {
                    Log.d(TAG,"Current data is null");
                }
            }
        });
    }

}
