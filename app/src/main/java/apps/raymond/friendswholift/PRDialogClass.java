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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PRDialogClass extends DialogFragment {

    private EditText prinput;
    private Spinner prspinner;
    private Button cancelbut, prsavebut;
    public String prtype;
    public OnPRInputListener prInputListener;

    public interface OnPRInputListener {
        //This method sends whatever arguments it has to the main activity!
        void storePr(String input, String prtype);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pr_dialog, container, false);
        prinput = view.findViewById(R.id.prInput);
        prspinner = view.findViewById(R.id.liftsSpinner);
        cancelbut = view.findViewById(R.id.cancel_button);
        prsavebut = view.findViewById(R.id.prsave_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
          getActivity().getBaseContext(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.array_liftsSpinner)
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        prspinner.setAdapter(adapter);

        prsavebut.setOnClickListener(saveprclicked);
        cancelbut.setOnClickListener(cancelprclicked);
        prspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id){
                prtype = prspinner.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView parent){
            }
        });
        return view;
    }

    public View.OnClickListener saveprclicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch(prtype){
                case "":
                    Toast.makeText(getActivity().getBaseContext(),"SELECT A TYPE",
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    String input = prinput.getText().toString();
                    prInputListener.storePr(input, prtype);
                    getDialog().dismiss();
            }
        }
    };

    public View.OnClickListener cancelprclicked = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            getDialog().dismiss();
        }

    };


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
