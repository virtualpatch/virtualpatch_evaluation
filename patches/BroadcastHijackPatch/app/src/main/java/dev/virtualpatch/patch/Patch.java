package dev.virtualpatch.patch;

import android.content.Intent;
import android.util.Log;

public class Patch extends PatchInstaller.BasePatch {
    @Override
    public void onServerCreate() throws Throwable {
        PatchInstaller.init();
        Log.v("patch-broadcast", "adding broadcast sanitization");
        PatchInstaller.sanitizeIntent(getClass().getDeclaredMethod("sanitizeIntent", Intent.class));
    }

    public static Intent sanitizeIntent(Intent i) {
        Intent ret = new Intent(i);
        String action = ret.getAction();
        if(action != null && action.startsWith("android.bluetooth.device.action")) {
            Log.d("sanitizeIntent", "sanitizing bluetooth intent");
            ret.removeExtra("android.bluetooth.device.extra.PACKAGE_NAME");
            ret.removeExtra("android.bluetooth.device.extra.CLASS_NAME");
        }
        return ret;
    }
}
