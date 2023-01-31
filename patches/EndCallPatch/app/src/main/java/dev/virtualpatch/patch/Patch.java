package dev.virtualpatch.patch;

import java.lang.reflect.Method;

public class Patch extends PatchInstaller.BasePatch {

    @Override
    public void onDynamicProxyCreate() throws Throwable {
        try {
            PatchInstaller.init();
            Method m = getClass().getDeclaredMethod("prevent", Object.class, Method.class, Object[].class);
            PatchInstaller.addMethodProxy("com.lody.virtual.client.hook.proxies.telecom.TelecomManagerStub", "endCall", m);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean prevent(Object w, Method m, Object[] args) {
        return false;
    }

}
