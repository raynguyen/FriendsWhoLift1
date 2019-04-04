package apps.raymond.kinect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;

public class ImageBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ImageBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            for (String key : intent.getExtras().keySet()) {
                Log.d(TAG, " " + intent.getExtras().get(key));
            }
        } else {
            Log.d(TAG,"Intent is null.");
        }

    }
}
