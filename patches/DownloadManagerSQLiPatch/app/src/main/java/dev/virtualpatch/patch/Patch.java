package dev.virtualpatch.patch;

import static dev.virtualpatch.patch.Utils.sqli_detection;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

import java.lang.reflect.Method;

public class Patch extends PatchInstaller.BasePatch {
    public static final String TAG = "PATCH";

    @Override
    public void onEnvCreate() throws Throwable {
        super.onEnvCreate();
        init();
    }

    @SuppressLint("SoonBlockedPrivateApi")
    public void init() throws Throwable {
        PatchInstaller.init();

        Method target = ContentResolver.class.getDeclaredMethod("query", Uri.class, String[].class, Bundle.class, CancellationSignal.class);
        Method hook = getClass().getDeclaredMethod("hook_query", ContentResolver.class, Uri.class, String[].class, Bundle.class, CancellationSignal.class);
        Method backup = getClass().getDeclaredMethod("backup_query", ContentResolver.class, Uri.class, String[].class, Bundle.class, CancellationSignal.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);
    }

    public static Cursor hook_query(ContentResolver cr, Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) throws Throwable {
        Log.wtf(TAG, "HOOKED!!!!");
        // SQLi detection patch
        try {
            String selection = queryArgs.getString("android:query-arg-sql-selection");
            if (sqli_detection(selection))
                return null;
        } catch (NullPointerException e) {
            Log.wtf(TAG, "No selection");
        }

        return backup_query(cr, uri, projection, queryArgs, cancellationSignal);
    }

    public static Cursor backup_query(ContentResolver cr, Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) throws Throwable {
        Log.wtf(TAG, "Shouldn't be called");
        return null;
    }
}