package apps.raymond.friendswholift;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PRDialogClass extends DialogFragment {

    private EditText prinput;
    private Spinner prspinner;
    private Button cancelbut, prsavebut;

    public interface OnPRInputListener{
        //This method sends whatever arguments it has to the main activity!
        void storePr(String input);
    }

    public OnPRInputListener prInputListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pr_dialog, container, false);
        prinput = view.findViewById(R.id.prInput);
        prspinner = view.findViewById(R.id.liftsSpinner);
        cancelbut = view.findViewById(R.id.cancel_button);
        prsavebut = view.findViewById(R.id.prsave_button);

        prsavebut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String input = prinput.getText().toString();

                prInputListener.storePr(input);

                getDialog().dismiss();
            }
        });

        cancelbut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            prInputListener = (OnPRInputListener) getActivity();
        } catch (ClassCastException e){
            Log.e("PRDialogClass", "ClassCastException: " + e.getMessage() );
        }
    }
}
