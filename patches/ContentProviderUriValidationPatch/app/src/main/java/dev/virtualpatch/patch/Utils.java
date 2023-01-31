package dev.virtualpatch.patch;

import android.net.Uri;
import android.util.Log;

public class Utils {
    public static Uri validate_uri(Uri uri) {
        final String encodedPath = uri.getEncodedPath();
        if (encodedPath != null && encodedPath.indexOf("//") != -1) {
            final Uri normalized = uri.buildUpon()
                    .encodedPath(encodedPath.replaceAll("//+", "/")).build();
            Log.w(Patch.TAG, "Normalized " + uri + " to " + normalized
                    + " to avoid possible security issues");
            uri = normalized;
        }
        return uri;
    }
}
