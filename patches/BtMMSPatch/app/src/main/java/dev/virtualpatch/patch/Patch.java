package dev.virtualpatch.patch;

import android.content.Intent;
import android.util.Log;

public class Patch extends PatchInstaller.BasePatch {
    @Override
    public void onServerCreate() throws Throwable {
        PatchInstaller.init();
        Log.v("patch-bt", "adding bt intent filter");
        PatchInstaller.sanitizeIntent(getClass().getDeclaredMethod("sanitizeIntent", Intent.class));
    }

    public static Intent sanitizeIntent(Intent i) {
        if(BluetoothOppUtils.checkIntent(i)) {
            return i;
        }
        return null;
    }
}
