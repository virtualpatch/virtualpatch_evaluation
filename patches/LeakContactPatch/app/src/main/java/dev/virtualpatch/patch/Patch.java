package dev.virtualpatch.patch;

import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

public class Patch extends PatchInstaller.BasePatch {
  private static final String T = "QC_RESULT";

  @Override
  public void onEnvCreate() throws Throwable {
    try {
      PatchInstaller.init();
      Method sanitize = getClass().getDeclaredMethod("sanitizeQCResult", List.class, Intent.class);
      PatchInstaller.sanitizeResult(sanitize);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static public Intent sanitizeQCResult(List<Intent> possibleLaunchIntents, Intent result) {
    for(Intent i: possibleLaunchIntents) {
      if(ContactsContract.QuickContact.ACTION_QUICK_CONTACT.equals(i.getAction())) {
        Log.d(T, "sanitizing result from QuickContacts");
        return null;
      }
    }
    return result;
  }
}
