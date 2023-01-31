package dev.virtualpatch.patch;

import android.annotation.SuppressLint;
import android.util.Log;

import java.lang.reflect.*;
import java.security.cert.X509Certificate;

public class Patch extends PatchInstaller.BasePatch {
    static final String T = "OkHttpPatch";

    @Override
    @SuppressLint("BlockedPrivateApi")
    public void onEnvCreate() throws Throwable {
        PatchInstaller.init();
        Class OkHostnameVerifier = Class.forName("com.android.okhttp.internal.tls.OkHostnameVerifier");
        Method target = OkHostnameVerifier.getDeclaredMethod("verifyHostName", String.class, X509Certificate.class);
        Method hook = getClass().getDeclaredMethod("hookCert", Object.class, String.class, X509Certificate.class);
        Method backup = getClass().getDeclaredMethod("backupCert", Object.class, String.class, X509Certificate.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);
        target = OkHostnameVerifier.getDeclaredMethod("verifyHostName", String.class, String.class);
        hook = getClass().getDeclaredMethod("hookString", Object.class, String.class, String.class);
        backup = getClass().getDeclaredMethod("backupString", Object.class, String.class, String.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);
    }

    public static boolean hookString(Object thiz, String hostName, String pattern) {
        Log.d(T, "hook called");
        if(!isPrintableAscii(pattern)) {
            return false;
        }
        return backupString(thiz, hostName, pattern);
    }

    public static boolean backupString(Object thiz, String hostName, String pattern) {
        Log.e(T, "backup should not be called...");
        return false;
    }

    public static boolean hookCert(Object thiz, String hostName, X509Certificate certificate) {
        Log.d(T, "hook called");
        if(!isPrintableAscii(hostName)) {
            return false;
        }
        return backupCert(thiz, hostName, certificate);
    }

    public static boolean backupCert(Object thiz, String hostName, X509Certificate certificate) {
        Log.e(T, "backup should not be called...");
        return false;
    }

    private static final char DEL = 127;
    static boolean isPrintableAscii(String input) {
        if (input == null) {
            return false;
        }
        for (char c : input.toCharArray()) {
            // Space is illegal in a DNS name. DEL and anything less than space is non-printing so
            // also illegal. Anything greater than DEL is not 7-bit.
            if (c <= ' ' || c >= DEL) {
                return false;
            }
        }
        return true;
    }
}
