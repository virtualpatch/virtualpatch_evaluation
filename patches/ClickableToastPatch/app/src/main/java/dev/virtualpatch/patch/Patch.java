package dev.virtualpatch.patch;

import android.util.Log;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Patch extends PatchInstaller.BasePatch {

    @Override
    public void onDynamicProxyCreate() throws Throwable {
        try {
            PatchInstaller.init();
            Method m = getClass().getDeclaredMethod("sanitizeToast", Object.class, Method.class, Object[].class);
            PatchInstaller.addMethodProxy("com.lody.virtual.client.hook.proxies.notification.NotificationManagerStub", "enqueueToast", m);

            Log.i("PATCHINSTALLER", "Installed toast");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sanitizeToast(Object who, Method method, Object... args) {
        Object mTN = args[2];
        try {
            Field field = mTN.getClass().getDeclaredField("mParams");
            field.setAccessible(true);
            Object mParams = field.get(mTN);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;

            // Toasts can't be clickable
            params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

            PatchInstaller.callNextProxy(who, method, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

}