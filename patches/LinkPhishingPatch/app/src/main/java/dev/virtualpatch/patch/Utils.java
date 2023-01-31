package dev.virtualpatch.patch;

import android.util.Log;

public class Utils {
    public static boolean containsUnsupportedCharacters(String text) {
        Log.wtf(Patch.TAG, text);
        if (text.contains("\u202C")) {
            Log.e(Patch.TAG, "Unsupported character for applying links: u202C");
            return true;
        }
        if (text.contains("\u202D")) {
            Log.e(Patch.TAG, "Unsupported character for applying links: u202D");
            return true;
        }
        if (text.contains("\u202E")) {
            Log.e(Patch.TAG, "Unsupported character for applying links: u202E");
            return true;
        }
        return false;
    }
}
