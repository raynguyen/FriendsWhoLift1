package apps.raymond.friendswholift.HomeActFrags;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.StatSQLClasses.StatEntity;

public class AddStatFrag extends DialogFragment implements View.OnClickListener {

    public interface AddStat{
        StatEntity newStat();
    }

    Button cancel_Btn, save_Btn, type_Btn;
    private Spinner stat_Spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Add New Stat");
        View v = inflater.inflate(R.layout.add_stat_dialog, container, false);

        cancel_Btn = v.findViewById(R.id.cancel_btn);
        save_Btn = v.findViewById(R.id.save_btn);
        type_Btn = v.findViewById(R.id.new_type_btn);
        cancel_Btn.setOnClickListener(this);
        save_Btn.setOnClickListener(this);
        type_Btn.setOnClickListener(this);


        stat_Spinner = v.findViewById(R.id.stat_type_spinner);
        //Cursor typeCursor = ; // Query the Room db for all the possible 'Types' for this user.
        //Set spinner adapter
        //StatTypeCursorAdapter adapter = new StatTypeCursorAdapter(getActivity().getBaseContext(),
         //       R.layout.single_stat_type_item, typeCursor, );

        //Set recyclerView adapter

        return v;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.cancel_btn:
                break;
            case R.id.save_btn:
                break;
            case R.id.new_type_btn:
                break;
        }
    }


}
