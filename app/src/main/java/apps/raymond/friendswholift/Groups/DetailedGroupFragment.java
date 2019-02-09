package apps.raymond.friendswholift.Groups;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.raymond.friendswholift.R;

public class DetailedGroupFragment extends Fragment {
    private static final String TAG = "DetailedGroupFragment";

    public DetailedGroupFragment(){
    }

    public static DetailedGroupFragment newInstance(GroupBase groupBase){
        Log.i(TAG,"Created a new instance of the DetailedGroupFragment.");
        return new DetailedGroupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.focus_group_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



}
