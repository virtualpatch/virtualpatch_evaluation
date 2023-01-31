package dev.virtualpatch.patch;

import android.util.Log;

import java.lang.reflect.Method;

public class Patch extends PatchInstaller.BasePatch {
    static final String T = "BTALIAS_PATCH";
    static final String stub = "com.lody.virtual.client.hook.proxies.bluetooth.BluetoothStub";

    @Override
    public void onDynamicProxyCreate() throws Throwable {
        try {
            PatchInstaller.init();
            Method getAliasHook = getClass().getDeclaredMethod("getAlias", Object.class, Method.class, Object[].class);
            Method setAliasHook = getClass().getDeclaredMethod("setAlias", Object.class, Method.class, Object[].class);
            PatchInstaller.addMethodProxy(stub, "getRemoteAlias", getAliasHook);
            PatchInstaller.addMethodProxy(stub, "setRemoteAlias", setAliasHook);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAlias(Object who, Method method, Object... args) throws Throwable {
        String alias = (String) PatchInstaller.callNextProxy(who, method, args);
        Log.d(T, "getAlias proxy (" + alias + ")");
        return alias
            .replace('\t', ' ')
            .replace('\n', ' ')
            .replace('\r', ' ')
            .replaceAll("\\s+", " ");
    }

    public static Object setAlias(Object who, Method method, Object... args) throws Throwable {
        String alias = (String) args[2];
        Log.d(T, "setAlias proxy (" + alias + ")");
        alias = alias
            .replace('\t', ' ')
            .replace('\n', ' ')
            .replace('\r', ' ')
            .replaceAll("\\s+", " ");
        args[2] = alias;
        return PatchInstaller.callNextProxy(who, method, args);
    }
}
