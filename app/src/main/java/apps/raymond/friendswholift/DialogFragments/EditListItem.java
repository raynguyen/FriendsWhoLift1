package apps.raymond.friendswholift.DialogFragments;

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
import android.widget.Toast;

import apps.raymond.friendswholift.MyInterfaces.MyInterface;
import apps.raymond.friendswholift.R;

/*
This DialogFragment will hold two buttons; a delete or an edit button.
Depending on button clicked the user will be allowed to modify the database lifts table.
 */
public class EditListItem extends DialogFragment {

    public MyInterface myInterface;
    public Context context;

    /*
    Using this object of EditListItem, you can pass arguments from the Activity from which it is
    created.
     */
    public static EditListItem newInstance(){
        EditListItem editListItem = new EditListItem();
        Bundle args = new Bundle();
        //args.putInt(); //This takes arguments from the method call that we can use to create our dialog.
        editListItem.setArguments(args);
        return editListItem;
    }

    //ToDo: Research about the onAttach method. Why is this required to use Interfaces?
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            myInterface = (MyInterface) getActivity();
        } catch (ClassCastException e){
            Log.e("MyInterface", "ClassCastException: " + e.getMessage() );
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_item_dialog,container,false);
        Button cancelBut = v.findViewById(R.id.cancel_button);
        Button deleteBut = v.findViewById(R.id.delete_button);

        cancelBut.setOnClickListener(cancelClick);
        deleteBut.setOnClickListener(deleteClick);

        return v;
    }

    private View.OnClickListener cancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myInterface.TestMethod(context);
        }
    };

    public View.OnClickListener deleteClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(),"Clicked delete",Toast.LENGTH_SHORT).show();
            myInterface.DeleteItem(context);
        }
    };
}