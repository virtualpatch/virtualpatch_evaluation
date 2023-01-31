package dev.virtualpatch.patch;

import android.app.ActivityManager;
import android.content.UriPermission;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

public class Patch extends PatchInstaller.BasePatch {
  private static final String T = "META_LEAK";

  @Override
  public void onDynamicProxyCreate() throws Throwable {
    try {
      PatchInstaller.init();
      Method checkURI = getClass().getDeclaredMethod("checkURI", Object.class, Method.class, Object[].class);
      PatchInstaller.addProviderProxy("com.android.providers.media.documents", checkURI);
      PatchInstaller.addProviderProxy("com.android.externalstorage.documents", checkURI);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static public Object checkURI(Object proxy, Method method, Object[] args) throws Throwable {
    if(method.getName().equals("call")) {
      String methodName = (String) args[1];
      if(methodName.equals("android:getDocumentMetadata")) {
        Log.d("virtualpatch_meta", "getDocumentMetadata called");
        // TODO: move reflection outside so we do this just once
        Method getService = ActivityManager.class.getDeclaredMethod("getService");
        Object ams = getService.invoke(null);
        Method getPersistedUriPermissions = ams.getClass().getDeclaredMethod("getPersistedUriPermissions", String.class, boolean.class);
        Log.d("virtualpatch_meta", "getPersistedUriPermissions: " + getPersistedUriPermissions.toString());
        // host package because the provider is external, i.e. any persisted permission is granted to the host package
        Object res = getPersistedUriPermissions.invoke(ams, PatchInstaller.getHostPkg(), true);
        Method getList = res.getClass().getDeclaredMethod("getList");
        List<UriPermission> permissions = (List<UriPermission>) getList.invoke(res);

        boolean canRead = false;
        for(UriPermission permission : permissions){
          if(permission.isReadPermission())
            canRead = true;
        }
        if(!canRead){
          Log.e(T, "No read permission.");
          throw new SecurityException("No read permissions.");
        }
      }
    }
    return null;
  }
}
