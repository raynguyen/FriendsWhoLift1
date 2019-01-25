package apps.raymond.friendswholift.HomeActFrags;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import apps.raymond.friendswholift.R;
import apps.raymond.friendswholift.StatSQLClasses.StatDAO;
import apps.raymond.friendswholift.StatSQLClasses.StatEntity;
import apps.raymond.friendswholift.StatSQLClasses.StatRoomDatabase;
import apps.raymond.friendswholift.StatSQLClasses.StatViewModel;

public class TempAddStat extends DialogFragment implements View.OnClickListener {

    TextInputEditText typeTxt, valueTxt;
    StatViewModel mStatViewModel;

    private static final String TAG = "TEMP DIALOG FRAGMENT";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.temp_add_stat,container, false);
        Button saveBtn = view.findViewById(R.id.save_btn);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        typeTxt = view.findViewById(R.id.type_txt);
        valueTxt = view.findViewById(R.id.value_txt);

        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        mStatViewModel = ViewModelProviders.of(this).get(StatViewModel.class);

        return view;
    }

    StatEntity newStat;
    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.save_btn:
                Log.d(TAG, "Save button was clicked, we will attempt to save the StatEntity to room." );
                if(TextUtils.isEmpty(typeTxt.getText().toString()) ||
                        TextUtils.isEmpty(valueTxt.getText().toString())){
                    Toast.makeText(getContext(),"One field is empty.",Toast.LENGTH_SHORT).show();
                } else {
                    SaveStatToRoom();
                }
                break;
            case R.id.cancel_btn:
                Toast.makeText(getContext(),"Clicked Cancel Button",Toast.LENGTH_SHORT).show();
        }
    }

    private void SaveStatToRoom(){
        newStat = new StatEntity(typeTxt.getText().toString(),Double.parseDouble(valueTxt.getText().toString()));
        mStatViewModel.insert(newStat);
        Log.d(TAG, "New StatEntity saved of Type: " + newStat.getType() + " and Value= " + newStat.getValue());
    }

}
