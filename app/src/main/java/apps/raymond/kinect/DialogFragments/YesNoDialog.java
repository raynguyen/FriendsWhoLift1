/*
 * Ray N - 02/03/2019
 * A generic DialogFragment with positive and negative buttons. The message body and title are
 * determined by the arguments when creating an instance of this dialog.
 *
 * The callback methods implemented are currently only set to pass data back to fragments and not to
 * activities.
 */

package apps.raymond.kinect.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class YesNoDialog extends DialogFragment{
    public static final String DISCARD_CHANGES = "Leaving now will discard any changes you have made. \nAre you sure you want to cancel?";
    public static final String DELETE_CONNECTION = "Delete connection with";
    public static final String WARNING = "WARNING!";
    public static final String CONFIRM_GROUP = "Are you sure you want to create this Group?";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final int POS_RESULT = 21;
    public static final int NEG_RESULT = 22;

    private YesNoCallback callback;
    public interface YesNoCallback{
        void onPositiveClick();
    }

    public static YesNoDialog newInstance(String title, String body){
        YesNoDialog dialog = new YesNoDialog();
        Bundle args = new Bundle();
        args.putString(TITLE,title);
        args.putString(BODY,body);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        Log.w("YESNODIALOG","in the onAttach of this");
        super.onAttach(context);
        try{
            callback = (YesNoCallback) context;
        } catch (ClassCastException e){}
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String title = getArguments().getString(TITLE);
        String body = getArguments().getString(BODY);
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(getTargetFragment()==null){
                            callback.onPositiveClick();
                        } else {
                            getTargetFragment().onActivityResult(getTargetRequestCode(),POS_RESULT,getActivity().getIntent());
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(getTargetFragment()!=null){
                            getTargetFragment().onActivityResult(getTargetRequestCode(),NEG_RESULT,getActivity().getIntent());
                        }
                    }
                })
                .create();
    }

}
