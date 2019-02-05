package apps.raymond.friendswholift.Activity_Main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import apps.raymond.friendswholift.R;

public class ButtonFrag extends Fragment {

    //We can handle button clicks in the host activity if preferred.
    private Button cancelBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceBundle){
        super.onCreateView(inflater, container, savedInstanceBundle);
        View view = inflater.inflate(R.layout.main_buttomfrag,container,false);

        return view;
    }

}
