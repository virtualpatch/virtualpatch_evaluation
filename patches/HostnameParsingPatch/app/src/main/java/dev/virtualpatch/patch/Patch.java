package dev.virtualpatch.patch;

import android.annotation.SuppressLint;
import android.util.Log;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLStreamHandler;

public class Patch extends PatchInstaller.BasePatch {
    public static final String TAG = "PATCH";

    @Override
    public void onEnvCreate() throws Throwable {
        super.onEnvCreate();
        init();
    }

    public void init() throws Throwable {
        PatchInstaller.init();

        Method target = URLStreamHandler.class.getDeclaredMethod("parseURL", URL.class, String.class, int.class, int.class);
        Method hook = getClass().getDeclaredMethod("hook_parseURL", URLStreamHandler.class, URL.class, String.class, int.class, int.class);
        Method backup = getClass().getDeclaredMethod("backup_parseURL", URLStreamHandler.class, URL.class, String.class, int.class, int.class);

        PatchInstaller.hookJavaMethod(target, hook, backup);
    }

    public static void hook_parseURL(URLStreamHandler thiz, URL u, String spec, int start, int limit) throws Throwable {
        Log.wtf(TAG, "HOOKED!!");
        // These fields may receive context content if this was relative URL
        String protocol = u.getProtocol();
        String authority = u.getAuthority();
        String userInfo = u.getUserInfo();
        String host = u.getHost();
        int port = u.getPort();
        String path = u.getPath();
        String query = u.getQuery();
        // This field has already been parsed
        String ref = u.getRef();
        boolean isRelPath = false;
        boolean queryOnly = false;
        // BEGIN Android-changed: App compat
        boolean querySet = false;
        // END Android-changed: App compat
// FIX: should not assume query if opaque
        // Strip off the query part
        if (start < limit) {
            int queryStart = spec.indexOf('?');
            queryOnly = queryStart == start;
            if ((queryStart != -1) && (queryStart < limit)) {
                query = spec.substring(queryStart + 1, limit);
                if (limit > queryStart)
                    limit = queryStart;
                spec = spec.substring(0, queryStart);
                // BEGIN Android-changed: App compat
                querySet = true;
                // END Android-changed: App compat
            }
        }
        int i = 0;
        // Parse the authority part if any
        // BEGIN Android-changed: App compat
        // boolean isUNCName = (start <= limit - 4) &&
        //                 (spec.charAt(start) == '/') &&
        //                 (spec.charAt(start + 1) == '/') &&
        //                 (spec.charAt(start + 2) == '/') &&
        //                 (spec.charAt(start + 3) == '/');
        boolean isUNCName = false;
        // END Android-changed: App compat
        if (!isUNCName && (start <= limit - 2) && (spec.charAt(start) == '/') &&
                (spec.charAt(start + 1) == '/')) {
            start += 2;
            /* PATCH */
            /*
            i = spec.indexOf('/', start);
            if (i < 0 || i > limit) {
                i = spec.indexOf('?', start);
                if (i < 0 || i > limit)
                    i = limit;
            }
             */

            LOOP:
            for (i = start; i < limit; i++) {
                switch (spec.charAt(i)) {
                    case '/':  // Start of path
                    case '\\': // Start of path - see https://url.spec.whatwg.org/#host-state
                    case '?':  // Start of query
                    case '#':  // Start of fragment
                        break LOOP;
                }
            }
            /* PATCH */


            host = authority = spec.substring(start, i);
            int ind = authority.indexOf('@');
            if (ind != -1) {
                if (ind != authority.lastIndexOf('@')) {
                    // more than one '@' in authority. This is not server based
                    userInfo = null;
                    host = null;
                } else {
                    userInfo = authority.substring(0, ind);
                    host = authority.substring(ind + 1);
                }
            } else {
                userInfo = null;
            }
            if (host != null) {
                // If the host is surrounded by [ and ] then its an IPv6
                // literal address as specified in RFC2732
                if (host.length() > 0 && (host.charAt(0) == '[')) {
                    if ((ind = host.indexOf(']')) > 2) {
                        String nhost = host;
                        host = nhost.substring(0, ind + 1);

                        Class<?> IPAddressUtil = Class.forName("sun.net.util.IPAddressUtil");
                        @SuppressLint("SoonBlockedPrivateApi") Method isIPv6LiteralAddress = IPAddressUtil.getDeclaredMethod("isIPv6LiteralAddress", String.class);
                        isIPv6LiteralAddress.setAccessible(true);
                        if (!(boolean) isIPv6LiteralAddress.invoke(null, host.substring(1, ind))) {
                            throw new IllegalArgumentException(
                                    "Invalid host: " + host);
                        }
                        port = -1;
                        if (nhost.length() > ind + 1) {
                            if (nhost.charAt(ind + 1) == ':') {
                                ++ind;
                                // port can be null according to RFC2396
                                if (nhost.length() > (ind + 1)) {
                                    port = Integer.parseInt(nhost.substring(ind + 1));
                                }
                            } else {
                                throw new IllegalArgumentException(
                                        "Invalid authority field: " + authority);
                            }
                        }
                    } else {
                        throw new IllegalArgumentException(
                                "Invalid authority field: " + authority);
                    }
                } else {
                    ind = host.indexOf(':');
                    port = -1;
                    if (ind >= 0) {
                        // port can be null according to RFC2396
                        if (host.length() > (ind + 1)) {
                            // BEGIN Android-changed: App compat
                            // port = Integer.parseInt(host.substring(ind + 1));
                            char firstPortChar = host.charAt(ind + 1);
                            if (firstPortChar >= '0' && firstPortChar <= '9') {
                                port = Integer.parseInt(host.substring(ind + 1));
                            } else {
                                throw new IllegalArgumentException("invalid port: " +
                                        host.substring(ind + 1));
                            }
                            // END Android-changed: App compat
                        }
                        host = host.substring(0, ind);
                    }
                }
            } else {
                host = "";
            }
            if (port < -1)
                throw new IllegalArgumentException("Invalid port number :" +
                        port);
            start = i;
            // If the authority is defined then the path is defined by the
            // spec only; See RFC 2396 Section 5.2.4.
            // BEGIN Android-changed: App compat
            // if (authority != null && authority.length() > 0)
            //   path = "";
            path = null;
            if (!querySet) {
                query = null;
            }
            // END Android-changed: App compat
        }
        if (host == null) {
            host = "";
        }
        // Parse the file path if any
        if (start < limit) {

            /* PATCH */
            //if (spec.charAt(start) == '/') {
            if (spec.charAt(start) == '/' || spec.charAt(start) == '\\') {
                /* PATCH */
                path = spec.substring(start, limit);
            } else if (path != null && path.length() > 0) {
                isRelPath = true;
                int ind = path.lastIndexOf('/');
                String seperator = "";
                if (ind == -1 && authority != null)
                    seperator = "/";
                path = path.substring(0, ind + 1) + seperator +
                        spec.substring(start, limit);
            } else {
                String seperator = (authority != null) ? "/" : "";
                path = seperator + spec.substring(start, limit);
            }
        }
        // BEGIN Android-changed: App compat
        //else if (queryOnly && path != null) {
        //    int ind = path.lastIndexOf('/');
        //    if (ind < 0)
        //        ind = 0;
        //    path = path.substring(0, ind) + "/";
        //}
        // END Android-changed: App compat
        if (path == null)
            path = "";
        // BEGIN Android-changed
        //if (isRelPath) {
        if (true) {
            // END Android-changed
            // Remove embedded /./
            while ((i = path.indexOf("/./")) >= 0) {
                path = path.substring(0, i) + path.substring(i + 2);
            }
            // Remove embedded /../ if possible
            i = 0;
            while ((i = path.indexOf("/../", i)) >= 0) {
                // BEGIN Android-changed: App compat
                /*
                 * Trailing /../
                 */
                if (i == 0) {
                    path = path.substring(i + 3);
                    i = 0;
                    // END Android-changed: App compat
                    /*
                     * A "/../" will cancel the previous segment and itself,
                     * unless that segment is a "/../" itself
                     * i.e. "/a/b/../c" becomes "/a/c"
                     * but "/../../a" should stay unchanged
                     */
                    // Android-changed: App compat
                    // if (i > 0 && (limit = path.lastIndexOf('/', i - 1)) >= 0 &&
                } else if (i > 0 && (limit = path.lastIndexOf('/', i - 1)) >= 0 &&
                        (path.indexOf("/../", limit) != 0)) {
                    path = path.substring(0, limit) + path.substring(i + 3);
                    i = 0;
                } else {
                    i = i + 3;
                }
            }
            // Remove trailing .. if possible
            while (path.endsWith("/..")) {
                i = path.indexOf("/..");
                if ((limit = path.lastIndexOf('/', i - 1)) >= 0) {
                    path = path.substring(0, limit + 1);
                } else {
                    break;
                }
            }
            // Remove starting .
            if (path.startsWith("./") && path.length() > 2)
                path = path.substring(2);
            // Remove trailing .
            if (path.endsWith("/."))
                path = path.substring(0, path.length() - 1);
            // Android-changed: App compat: Remove trailing ?
            if (path.endsWith("?"))
                path = path.substring(0, path.length() - 1);
        }
        Method setURL = URLStreamHandler.class.getDeclaredMethod("setURL", URL.class, String.class, String.class, int.class, String.class, String.class, String.class, String.class, String.class);
        setURL.setAccessible(true);
        setURL.invoke(thiz, u, protocol, host, port, authority, userInfo, path, query, ref);
    }

    public static void backup_parseURL(URLStreamHandler thiz, URL u, String spec, int start, int limit) throws Throwable {
        Log.wtf(TAG, "Shouldn't be called.");
        return;
    }
}
