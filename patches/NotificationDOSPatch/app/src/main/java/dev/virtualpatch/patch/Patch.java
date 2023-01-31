package dev.virtualpatch.patch;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Person;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification.MessagingStyle.Message;

public class Patch extends PatchInstaller.BasePatch {

    private static final String TAG = "LONGNOTIFICATIONPATCH";

    @Override
    public void onEnvCreate() throws Throwable {
        installPatch();
    }

    @SuppressLint("SoonBlockedPrivateApi")
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void installPatch() throws Throwable {
        PatchInstaller.init();


        @SuppressLint("SoonBlockedPrivateApi") Method target =
                Notification.class.getDeclaredMethod("safeCharSequence", CharSequence.class);
        Method hook = getClass().getDeclaredMethod("safeCharSequence", CharSequence.class);
        Method backup = getClass().getDeclaredMethod("safeBackup", CharSequence.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);

        Constructor<?> target2 = Message.class.getDeclaredConstructor(CharSequence.class, long.class, Person.class, boolean.class);
        hook = getClass().getDeclaredMethod("safeMessageConstructor", Message.class, CharSequence.class,
                long.class, Person.class, boolean.class);
        backup = getClass().getDeclaredMethod("safeMessageConstructorBackup", Message.class, CharSequence.class,
                long.class, Person.class, boolean.class);
        PatchInstaller.hookJavaMethod(target2, hook, backup);

        target = Message.class.getDeclaredMethod("toBundle");
        hook = getClass().getDeclaredMethod("safeToBundle", Message.class);
        backup = getClass().getDeclaredMethod("safeToBundleBackup", Message.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);

        Log.i(TAG, "Installed");
    }

    private static final int MAX_CHARSEQUENCE_LENGTH = 1024;
    public static CharSequence safeCharSequence(CharSequence cs) {
        if (cs == null) return cs;
        if (cs.length() > MAX_CHARSEQUENCE_LENGTH) {
            cs = cs.subSequence(0, MAX_CHARSEQUENCE_LENGTH);
        }
        if (cs instanceof Parcelable) {
            Log.e(TAG, "warning: " + cs.getClass().getCanonicalName()
                    + " instance is a custom Parcelable and not allowed in Notification");
            return cs.toString();
        }
        return removeTextSizeSpans(cs);
    }
    private static CharSequence removeTextSizeSpans(CharSequence charSequence) {
        if (charSequence instanceof Spanned) {
            Spanned ss = (Spanned) charSequence;
            Object[] spans = ss.getSpans(0, ss.length(), Object.class);
            SpannableStringBuilder builder = new SpannableStringBuilder(ss.toString());
            for (Object span : spans) {
                Object resultSpan = span;
                if (resultSpan instanceof CharacterStyle) {
                    resultSpan = ((CharacterStyle) span).getUnderlying();
                }
                if (resultSpan instanceof TextAppearanceSpan) {
                    TextAppearanceSpan originalSpan = (TextAppearanceSpan) resultSpan;
                    resultSpan = new TextAppearanceSpan(
                            originalSpan.getFamily(),
                            originalSpan.getTextStyle(),
                            -1,
                            originalSpan.getTextColor(),
                            originalSpan.getLinkTextColor());
                } else if (resultSpan instanceof RelativeSizeSpan
                        || resultSpan instanceof AbsoluteSizeSpan) {
                    continue;
                } else {
                    resultSpan = span;
                }
                builder.setSpan(resultSpan, ss.getSpanStart(span), ss.getSpanEnd(span),
                        ss.getSpanFlags(span));
            }
            return builder;
        }
        return charSequence;
    }
    public static CharSequence safeBackup(CharSequence cs) {
        Log.i(TAG, "Should not be called");
        return null;
    }
    public static void safeMessageConstructor(Message thiz, CharSequence text, long timestamp, Person sender, boolean remoteInputHistory) {
        text = safeCharSequence(text);
        safeMessageConstructorBackup(thiz, text, timestamp, sender, remoteInputHistory);
    }
    public static void safeMessageConstructorBackup(Message thiz, CharSequence text, long timestamp, Person sender, boolean remoteInputHistory) {
        Log.i(TAG, "Should not be called");
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static Bundle safeToBundle(Message thiz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Bundle bundle = new Bundle();
        if (thiz.getText() != null) {
            bundle.putCharSequence("text", thiz.getText());
        }
        bundle.putLong("time", thiz.getTimestamp());
        if (thiz.getSenderPerson() != null) {
            // Legacy listeners need this
            bundle.putCharSequence("sender", safeCharSequence(thiz.getSenderPerson().getName()));
            bundle.putParcelable("sender_person", thiz.getSenderPerson());
        }
        if (thiz.getDataMimeType() != null) {
            bundle.putString("type", thiz.getDataMimeType());
        }
        if (thiz.getDataUri() != null) {
            bundle.putParcelable("uri", thiz.getDataUri());
        }
        if (thiz.getExtras() != null) {
            bundle.putBundle("extras", thiz.getExtras());
        }
        @SuppressLint("SoonBlockedPrivateApi")
        Method isRemoteInputHistory = thiz.getClass().getDeclaredMethod("isRemoteInputHistory");
        boolean mRemoteInputHistory = (boolean) isRemoteInputHistory.invoke(thiz);

        if (mRemoteInputHistory) {
            bundle.putBoolean("remote_input_history", mRemoteInputHistory);
        }
        return bundle;
    }


    public static Bundle safeToBundleBackup(Message thiz) {
        Log.i(TAG, "Should not be called");
        return null;
    }

}