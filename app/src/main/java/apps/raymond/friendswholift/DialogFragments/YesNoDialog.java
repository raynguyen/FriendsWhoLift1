/*
 * A generic DialogFragment with positive and negative buttons, message body to be passed by the
 * caller, optional title?
 */

package apps.raymond.friendswholift.DialogFragments;

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
    private static final String TAG = "YesNoDialog";

    YesNoInterface yesNoInterface;

    public interface YesNoInterface {
        void positiveClick();
        void negativeClick();
    }

    public YesNoDialog(){
        //Empty Constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "Creating a YesNoDialog.");

        Bundle args = getArguments();

        return new AlertDialog.Builder(getActivity())
                .setTitle("Note!")
                .setMessage("shit")
                .setPositiveButton("Positive", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yesNoInterface.positiveClick();
                    }
                })
                .setNegativeButton("Negative", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yesNoInterface.negativeClick();
                    }
                })
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            yesNoInterface = (YesNoInterface) getActivity();
        } catch (ClassCastException e) {
        Log.e(TAG,"Class cast exception:" + e.getMessage());
        }
    }
}
