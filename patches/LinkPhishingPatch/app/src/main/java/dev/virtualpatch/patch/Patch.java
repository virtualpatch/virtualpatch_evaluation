package dev.virtualpatch.patch;

import static android.text.util.Linkify.addLinks;

import static dev.virtualpatch.patch.Utils.containsUnsupportedCharacters;

import android.text.Spannable;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class Patch extends PatchInstaller.BasePatch {
    public static final String TAG = "PATCH";

    @Override
    public void onEnvCreate() throws Throwable {
        super.onEnvCreate();
        init();
    }

    public void init() throws Throwable {
        PatchInstaller.init();

        Method target = Linkify.class.getDeclaredMethod("addLinks", Spannable.class, int.class);
        Method hook = getClass().getDeclaredMethod("hook_addLinks", Spannable.class, int.class);
        Method backup = getClass().getDeclaredMethod("backup_addLinks", Spannable.class, int.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);

        target = Linkify.class.getDeclaredMethod("addLinks", TextView.class, int.class);
        hook = getClass().getDeclaredMethod("hook_addLinks", TextView.class, int.class);
        backup = getClass().getDeclaredMethod("backup_addLinks", TextView.class, int.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);

        target = Linkify.class.getDeclaredMethod("addLinks", Spannable.class, Pattern.class, String.class, Linkify.MatchFilter.class, Linkify.TransformFilter.class);
        hook = getClass().getDeclaredMethod("hook_addLinks", Spannable.class, Pattern.class, String.class, Linkify.MatchFilter.class, Linkify.TransformFilter.class);
        backup = getClass().getDeclaredMethod("backup_addLinks", Spannable.class, Pattern.class, String.class, Linkify.MatchFilter.class, Linkify.TransformFilter.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);
    }

    public static boolean hook_addLinks(Spannable text, int mask) throws Throwable {
        Log.wtf(TAG, "HOOKED!!");
        /* PATCH */
        if (text != null && containsUnsupportedCharacters(text.toString())) {
            android.util.EventLog.writeEvent(0x534e4554, "116321860", -1, "");
            return false;
        }
        /* PATCH */

        return backup_addLinks(text, mask);
    }

    public static boolean backup_addLinks(Spannable text, int mask) {
        Log.wtf(TAG, "Shouldn't be called.");
        return false;
    }

    public static boolean hook_addLinks(TextView text, int mask) throws Throwable {
        Log.wtf(TAG, "HOOKED!!");
        /* PATCH */
        if (text != null && containsUnsupportedCharacters((String) text.getText())) {
            android.util.EventLog.writeEvent(0x534e4554, "116321860", -1, "");
            return false;
        }
        /* PATCH */

        return backup_addLinks(text, mask);
    }

    public static boolean backup_addLinks(TextView text, int mask) {
        Log.wtf(TAG, "Shouldn't be called.");
        return false;
    }

    public static boolean hook_addLinks(Spannable s, Pattern p, String scheme, Linkify.MatchFilter matchFilter, Linkify.TransformFilter transformFilter) throws Throwable {
        Log.wtf(TAG, "HOOKED!!");
        /* PATCH */
        if (s != null && containsUnsupportedCharacters(s.toString())) {
            android.util.EventLog.writeEvent(0x534e4554, "116321860", -1, "");
            return false;
        }
        /* PATCH */

        return backup_addLinks(s, p, scheme, matchFilter, transformFilter);
    }

    public static boolean backup_addLinks(Spannable s, Pattern p, String scheme, Linkify.MatchFilter matchFilter, Linkify.TransformFilter transformFilter) {
        Log.wtf(TAG, "Shouldn't be called.");
        return false;
    }
}
