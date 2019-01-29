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

    YesNoInterface callback;

    public interface YesNoInterface {
        void positiveClick();
        void negativeClick();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        callback.positiveClick();
                    }
                })
                .setNegativeButton("Negative", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.negativeClick();
                    }
                })
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (YesNoInterface) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG,"Class cast exception:" + e.getMessage());
        }
    }
}
