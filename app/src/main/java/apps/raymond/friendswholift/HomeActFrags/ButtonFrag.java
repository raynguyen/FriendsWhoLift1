package apps.raymond.friendswholift.HomeActFrags;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.zip.Inflater;

import apps.raymond.friendswholift.R;

public class ButtonFrag extends Fragment {

    private Button cancelBtn;
    private Button logBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceBundle){
        super.onCreateView(inflater, container, savedInstanceBundle);
        View view = inflater.inflate(R.layout.main_buttomfrag,container,false);
        return view;
    }
}
