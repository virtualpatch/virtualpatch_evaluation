package dev.virtualpatch.patch;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * PatchInstaller contains all the utilities that can be used to add different kinds of patches to
 * the virtual environment. This does not include native patches, since native patches are just
 * dynamically loaded native libraries and don't need any java class.
 */
public class PatchInstaller {

    public static abstract class BasePatch {

        /**
         * this is called on the guest application process when the virtual environment is being
         * created.
         * @throws Throwable
         */
        public void onEnvCreate() throws Throwable {

        }

        /**
         * This is called on the guest application process before the dynamic proxies are created and
         * injected
         * @throws Throwable
         */
        public void onDynamicProxyCreate() throws Throwable {

        }

        /**
         * This is called on the server process before all the VirtualApp services are created
         * @throws Throwable
         */
        public void onServerCreate() throws Throwable {

        }
    }

    private static final String T = "PATCH_INSTALLER";
    private static Method backupAndHook;
    private static Method addIntentSanitizer;
    private static Method chainMethodProxy;
    private static Method callMethodProxy;

    public static void init() throws Exception {
        backupAndHook = Class
                .forName("lab.galaxy.yahfa.HookMain")
                .getDeclaredMethod("backupAndHook", Object.class, Method.class, Method.class);
        chainMethodProxy = Class
                .forName("com.lody.virtual.client.hook.base.MethodInvocationProxy")
                .getDeclaredMethod("addAdditionalProxy", Class.class, String.class, Method.class);
        addIntentSanitizer = Class
                .forName("dev.virtualpatch.patch.IntentSanitizer")
                .getDeclaredMethod("addSanitizer", Method.class);
        callMethodProxy = Class
                .forName("com.lody.virtual.client.hook.base.MethodProxy")
                .getDeclaredMethod("call", Object.class, Method.class, Object[].class);
    }

    /**
     * Replace a Java method in the virtual environment
     * @param target the method to replace
     * @param hook the method that will replace target
     * @param backup a method that when called from `hook` will call `target`, i.e. gives a way to
     *               call the original method from the hook
     */
    public static void hookJavaMethod(Method target, Method hook, Method backup) {
        try {
            backupAndHook.invoke(null, target, hook, backup);
        } catch (Exception e) {
            Log.e(T, "error hooking java method " + target.getName());
            e.printStackTrace();
        }
    }

    /**
     * Add an intent sanitizer to the virtual environment
     * @param sanitize method that will be called with the intent to sanitize, and should return
     *                 the sanitized intent. If the returned intent is null, the intent will be
     *                 blocked.
     */
    public static void sanitizeIntent(Method sanitize) {
        try {
            addIntentSanitizer.invoke(null, sanitize);
        } catch (Exception e) {
            Log.e(T, "error adding intent sanitizer " + sanitize.getName());
            e.printStackTrace();
        }
    }

    /**
     *
     * @param className fully qualified name of the proxy class. Check VirtualApp source code to get
     *                  the name.
     * @param methodName name of the method that should be proxied. Multiple proxies to the same
     *                   method are chained.
     * @param proxy proxy method, called instead of the original method. Should have the same
     *              signature as {callNextProxy}.
     *              The `args` array will have
     *              the next method proxy as the first element, followed by the arguments used to
     *              call the method. To call the next proxy in the chain, you can use {callNextProxy}.
     *              You can also return the result, in which case the next proxies in the chain will
     *              not be called.
     */
    public static void addMethodProxy(String className, String methodName, Method proxy) {
        try {
            Class<?> cl = Class.forName(className);
            chainMethodProxy.invoke(null, cl, methodName, proxy);
        } catch (Exception e) {
            Log.e(T, "error adding method proxy" + proxy.getName());
            e.printStackTrace();
        }
    }

    /**
     * To be used inside the proxy methods to call the next method in the proxy chain. Uses reflection
     * to access VirtualApp internal classes.
     * @param who invocation object
     * @param method original method
     * @param args args[0] is the next method proxy in the chain, args[1..] are the actial arguments.
     *             you can just pass the array you got in the proxy.
     * @return the result of the call
     * @throws Throwable any throwable that was thrown due to reflection error or invocation error
     */
    public static Object callNextProxy(Object who, Method method, Object... args) throws Throwable {
        Object next = args[0];
        Object[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        if(next != null) {
            return callMethodProxy.invoke(next, who, method, newArgs);
        }
        // in case there is no next, and the chain was not interrupted, call the original method.
        return method.invoke(who, newArgs);
    }

}
