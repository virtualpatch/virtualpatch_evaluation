package dev.virtualpatch.patch;

import android.accounts.Account;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.*;

public class Patch extends PatchInstaller.BasePatch {

    @Override
    public void onServerCreate() throws Throwable {
        installPatch();
    }

    @Override
    public void onEnvCreate() throws Throwable {
        installPatch();
    }

    public void installPatch() throws Throwable {
        PatchInstaller.init();
        Constructor<?> target = Account.class.getDeclaredConstructor(Parcel.class);
        Method hook = getClass().getDeclaredMethod("hook", Account.class, Parcel.class);
        Method backup = getClass().getDeclaredMethod("backup", Account.class, Parcel.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);
    }

    public static void hook(Account thiz, Parcel in) {
        Log.v("virtualpatch", "hook on account constructor...");
        String name = in.readString();
        String type = in.readString();
        if (TextUtils.isEmpty(name)) {
            throw new android.os.BadParcelableException("the name must not be empty: " + name);
        }
        if (TextUtils.isEmpty(type)) {
            throw new android.os.BadParcelableException("the type must not be empty: " + type);
        }
        in.setDataPosition(0);
        backup(thiz, in);
    }

    public static void backup(Account thiz, Parcel in) {
        Log.e("virtualpatch", "backup should not be called...");
        return;
    }
}
