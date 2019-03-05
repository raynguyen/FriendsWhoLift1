/*
 * Ray N - 02/03/2019
 * A generic DialogFragment with positive and negative buttons. The message body and title are
 * determined by the arguments when creating an instance of this dialog.
 *
 * The callback methods implemented are currently only set to pass data back to fragments and not to
 * activities.
 */

package apps.raymond.friendswholift.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class YesNoDialog extends DialogFragment{
    private static final String TAG = "YesNoDialog";
    public static final String DISCARD_CHANGES = "Leaving now will discard any changes you have made. \nAre you sure you want to cancel?";
    public static final String WARNING = "WARNING!";
    public static final String CONFIRM_GROUP = "Are you sure you want to create this Group?";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final int POS_RESULT = 21;
    public static final int NEG_RESULT = 22;


    public YesNoDialog(){
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String title = getArguments().getString(TITLE);
        String body = getArguments().getString(BODY);
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //The getTargetFragment snip is only when we implement the code for dialog result inside the fragment and not the host activity.
                        getTargetFragment().onActivityResult(getTargetRequestCode(),POS_RESULT,getActivity().getIntent());
                        //callback.positiveClick();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(),NEG_RESULT,getActivity().getIntent());
                        //callback.negativeClick();
                    }
                })
                .create();
    }

}
