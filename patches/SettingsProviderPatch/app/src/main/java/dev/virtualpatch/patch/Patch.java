package dev.virtualpatch.patch;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;

public class Patch extends PatchInstaller.BasePatch {
  private static final String T = "LANGUAGEDOS";

  @Override
  public void onDynamicProxyCreate() throws Throwable {
    try {
      PatchInstaller.init();
      Method sanitizeLocale = getClass().getDeclaredMethod("sanitizeLocale", Object.class, Method.class, Object[].class);
      PatchInstaller.addProviderProxy("settings", sanitizeLocale);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static public Object sanitizeLocale(Object proxy, Method method, Object[] args) throws Throwable {
    if(method.getName().equals("call")) {
      int start = 1;
      if (Build.VERSION.SDK_INT > 29) {
          start = 3;
      } else if (Build.VERSION.SDK_INT > 28) {
          start = 2;
      }

      String methodName = (String) args[start];
      if(methodName.equals("PUT_system")) {
        String arg = (String) args[start + 1];
        Bundle extras = (Bundle) args[start + 2];
        sanitizeLocaleSettings(extras);
      }
    }
    return null;
  }

  private static void sanitizeLocaleSettings(Bundle extras) {
    String values = extras.getString("value");
    Log.i(T, "values: " + values);

    if (values != null && !values.equals("")) {
      String[] tags = values.split(",");
      Locale[] locales = new Locale[tags.length];

      for (int i = 0; i < locales.length; i++) {
        locales[i] = Locale.forLanguageTag(tags[i]);
      }

      final HashSet<Locale> seenLocales = new HashSet<Locale>();

      final StringBuilder sb = new StringBuilder();

      for (int i = 0; i < locales.length; i++) {
        final Locale l = locales[i];
        if(!seenLocales.contains(l)) {
          final Locale localeClone = (Locale) l.clone();

          sb.append(localeClone.toLanguageTag());
          sb.append(',');

          seenLocales.add(localeClone);
        }
      }

      if (sb.length() > 0 && sb.charAt(sb.length()-1) == ',') {
        sb.deleteCharAt(sb.length() - 1);
      }

      String result = sb.toString();
      extras.putString("value", result);
      Log.i(T, "result: " + result);
    }
  }
}
