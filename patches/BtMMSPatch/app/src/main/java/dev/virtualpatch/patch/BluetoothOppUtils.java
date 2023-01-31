package dev.virtualpatch.patch;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class BluetoothOppUtils {

    private static final String TAG = "BluetoothOppUtils";
    private static final ComponentName BluetoothOppLauncherActivity = new ComponentName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity");

    public static boolean isForbiddenContent(Uri uri) {
        if ("com.android.bluetooth.map.MmsFileProvider".equals(uri.getHost())) {
            return true;
        }
        return false;
    }

    public static boolean checkIntent(Intent i) {
        ComponentName comp = i.getComponent();
        if(comp != null && comp.equals(BluetoothOppLauncherActivity)) {
            final Uri stream = (Uri) i.getParcelableExtra(Intent.EXTRA_STREAM);
            Log.v(TAG, "uri: " + stream.toString());
            Log.v(TAG, "scheme: " + stream.getScheme() + " " + stream.getHost());
            if("content".equals(stream.getScheme()) && isForbiddenContent(stream)) {
                Log.e(TAG, "Content from forbidden URI is not allowed.");
                return false;
            }
        }
        Log.v(TAG, "component does not match bluetooth filter");
        return true;
    }
}