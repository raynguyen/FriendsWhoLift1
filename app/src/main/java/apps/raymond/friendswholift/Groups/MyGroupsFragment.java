package apps.raymond.friendswholift.Groups;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

import apps.raymond.friendswholift.R;

public class MyGroupsFragment extends Fragment {

    private static final String TAG = "MygroupsFragment";
    GroupsViewModel mUserViewModel;

    TextView testTextView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_cardview_container,container,false);
        RecyclerView cardRecycler = view.findViewById(R.id.card_container);

        mUserViewModel = new GroupsViewModel();

        Button createUserDocBtn = view.findViewById(R.id.testButton1);
        Button updateFieldBtn = view.findViewById(R.id.testButton2);
        Button getfieldsBtn = view.findViewById(R.id.testButton3);
        Button getGroupPojoBtn = view.findViewById(R.id.testButton4);

        createUserDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //testMethod();
            }
        });

        createUserDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        updateFieldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        getfieldsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        getGroupPojoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        testTextView = view.findViewById(R.id.testTextView);

        return view;
    }

    /*
    public void testMethod(){
        Log.i(TAG,"Inside testMethod.");
        mUserViewModel.createGroup(new FireStoreCallBack() {
            @Override
            public void onCallBack(Set<String> keySet) {
                Log.i(TAG,"Inside testMethod of our Fragment and retrieved: " + keySet);
                testTextView.setText(keySet.toString());
            }
        });
    }*/

    public interface FireStoreCallBack {
        void onCallBack(Set<String> keySet);
    }
}
