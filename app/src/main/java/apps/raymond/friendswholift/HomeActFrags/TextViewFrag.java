package apps.raymond.friendswholift.HomeActFrags;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import apps.raymond.friendswholift.R;

public class TextViewFrag extends Fragment {

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.main_textviewfrag,container, false);
        TextView benchTV = (TextView) view.findViewById(R.id.benchTxt);
        TextView deadTV = (TextView) view.findViewById(R.id.deadTxt);
        TextView squatTV = (TextView) view.findViewById(R.id.squatTxt);

        return view;
    }

    /*
    The textviews should be updated with only the largest value stored for that type in the database.
     */
}