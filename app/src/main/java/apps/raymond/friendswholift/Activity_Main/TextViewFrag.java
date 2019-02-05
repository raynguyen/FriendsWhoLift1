package apps.raymond.friendswholift.Activity_Main;

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
    private static String test1 = "Hello!";
    private static String test2 = "yeyeye!";
    private static String test3 = "Hhahaha!";
    private TextView benchTV;
    private TextView deadTV;
    private TextView squatTV;

    // This method will be invoked when the Fragment view object is created.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.main_textviewfrag, container, false);

        benchTV = view.findViewById(R.id.benchTxt);
        deadTV = view.findViewById(R.id.deadTxt);
        squatTV = view.findViewById(R.id.squatTxt);
        UpdateTV();

        return view;
    }

    private void UpdateTV(){
        benchTV.setText(test1);
        deadTV.setText(test2);
        squatTV.setText(test3);
        //Toast.makeText(getContext(),"Inside UpdateTV",Toast.LENGTH_SHORT).show();
    }
    /*
    The textviews should be updated with only the largest value stored for that type in the database.
     */
}
