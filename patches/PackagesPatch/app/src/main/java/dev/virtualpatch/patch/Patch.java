package dev.virtualpatch.patch;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Patch extends PatchInstaller.BasePatch {

    @Override
    public void onDynamicProxyCreate() throws Throwable {
        try {
            PatchInstaller.init();
            Method m = getClass().getDeclaredMethod("getAllPackages", Object.class, Method.class, Object[].class);
            PatchInstaller.addMethodProxy("com.lody.virtual.client.hook.proxies.pm.PackageManagerStub", "getAllPackages", m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllPackages(Object who, Method method, Object... args) {
        return new ArrayList<>();
    }

}
