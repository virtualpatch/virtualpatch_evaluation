package dev.virtualpatch.patch;

import static dev.virtualpatch.patch.Utils.validate_uri;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
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

        target = ContentResolver.class.getDeclaredMethod("insert", Uri.class, ContentValues.class);
        hook = getClass().getDeclaredMethod("hook_insert", ContentResolver.class, Uri.class, ContentValues.class);
        backup = getClass().getDeclaredMethod("backup_insert", ContentResolver.class, Uri.class, ContentValues.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);

        target = ContentResolver.class.getDeclaredMethod("update", Uri.class, ContentValues.class, String.class, String[].class);
        hook = getClass().getDeclaredMethod("hook_update", ContentResolver.class, Uri.class, ContentValues.class, String.class, String[].class);
        backup = getClass().getDeclaredMethod("backup_update", ContentResolver.class, Uri.class, ContentValues.class, String.class, String[].class);
        PatchInstaller.hookJavaMethod(target, hook, backup);

        target = ContentResolver.class.getDeclaredMethod("delete", Uri.class, String.class, String[].class);
        hook = getClass().getDeclaredMethod("hook_delete", ContentResolver.class, Uri.class, String.class, String[].class);
        backup = getClass().getDeclaredMethod("backup_delete", ContentResolver.class, Uri.class, String.class, String[].class);
        PatchInstaller.hookJavaMethod(target, hook, backup);
    }

    public static Cursor hook_query(ContentResolver cr, Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) throws Throwable {
        Log.wtf(TAG, "HOOKED!!!!");
        // Uri validation patch
        uri = validate_uri(uri);
        // end patch

        return backup_query(cr, uri, projection, queryArgs, cancellationSignal);
    }

    public static Cursor backup_query(ContentResolver cr, Uri uri, String[] projection, Bundle queryArgs, CancellationSignal cancellationSignal) throws Throwable {
        Log.wtf(TAG, "Shouldn't be called");
        return null;
    }

    public static Uri hook_insert(ContentResolver cr, Uri uri, ContentValues values) throws Throwable {
        Log.wtf(TAG, "HOOKED!!!!");
        // Uri verification patch
        uri = validate_uri(uri);
        // end patch

        return backup_insert(cr, uri, values);
    }

    public static Uri backup_insert(ContentResolver cr, Uri uri, ContentValues values) throws Throwable {
        Log.wtf(TAG, "Shouldn't be called");
        return null;
    }

    public static int hook_update(ContentResolver cr, Uri uri, ContentValues values, String where, String[] selectionArgs) throws Throwable {
        Log.wtf(TAG, "HOOKED!!!!");
        // Uri verification patch
        uri = validate_uri(uri);
        // end patch

        return backup_update(cr, uri, values, where, selectionArgs);
    }

    public static int backup_update(ContentResolver cr, Uri uri, ContentValues values, String where, String[] selectionArgs) throws Throwable {
        Log.wtf(TAG, "Shouldn't be called");
        return -1;
    }

    public static int hook_delete(ContentResolver cr, Uri uri, String where, String[] selectionArgs) throws Throwable {
        Log.wtf(TAG, "HOOKED!!!!");
        // Uri verification patch
        uri = validate_uri(uri);
        // end patch

        return backup_delete(cr, uri, where, selectionArgs);
    }

    public static int backup_delete(ContentResolver cr, Uri uri, String where, String[] selectionArgs) throws Throwable {
        Log.wtf(TAG, "Shouldn't be called");
        return -1;
    }
}
