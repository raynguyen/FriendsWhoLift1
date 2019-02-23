package apps.raymond.friendswholift.UserProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import apps.raymond.friendswholift.R;

public class ProfileFrag extends Fragment implements View.OnLayoutChangeListener {
    private final static String TAG = "ProfileFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_fragment,container,false);
    }

    private ViewFlipper viewFlipper;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewFlipper = view.findViewById(R.id.profile_name_flipper);
        viewFlipper.addOnLayoutChangeListener(this);
        if(viewFlipper.getDisplayedChild() == 0){
            TextView nameTxt = view.findViewById(R.id.profile_name_txt);
            ImageButton editBtn = view.findViewById(R.id.name_edit_btn);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Clicked edit name button.");
                    viewFlipper.showNext();
                }
            });
            nameTxt.setText("hello");
        }
        Log.i(TAG,"viewflipper child is currently: "+viewFlipper.getDisplayedChild());
    }

    private String name;
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        int i = viewFlipper.getDisplayedChild();
        switch (i){
            case 0:
                Log.i(TAG,"Current flipper view is: "+viewFlipper.getDisplayedChild());
                TextView nameField = v.findViewById(R.id.profile_name_txt);
                nameField.setText(name);
                break;
            case 1:
                Log.i(TAG,"Current flipper view is: "+viewFlipper.getDisplayedChild());

                final TextInputEditText newName = v.findViewById(R.id.name_txt_field);

                Button saveBtn = v.findViewById(R.id.name_edit_save_btn);
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(newName.getText().toString().isEmpty()){
                            Log.i(TAG,"Empty name field.");
                        } else{
                            name = newName.getText().toString();
                            viewFlipper.showPrevious();
                            Log.i(TAG,"Current name for user is: "+name);
                        }
                    }
                });
                break;
        }
    }
}