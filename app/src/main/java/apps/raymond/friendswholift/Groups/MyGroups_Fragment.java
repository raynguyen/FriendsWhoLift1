package apps.raymond.friendswholift.Groups;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.raymond.friendswholift.R;

public class MyGroups_Fragment extends Fragment {
    private static final String TAG = "MyGroups_Fragment";
    /*
    Recycler view here with CardViews.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.groups_cardview_container,container,false);

        RecyclerView recyclerView = view.findViewById(R.id.groups_container);

        /*
        Use a ViewModel that takes data from firebase.

         */


        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
