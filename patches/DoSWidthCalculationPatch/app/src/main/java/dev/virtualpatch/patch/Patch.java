package dev.virtualpatch.patch;

import android.annotation.SuppressLint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Patch extends PatchInstaller.BasePatch {
    private static final String TAG = "PATCH";

    @SuppressLint("PrivateApi")
    @Override
    public void onEnvCreate() throws Throwable {
        init();
    }

    @SuppressLint("PrivateApi")
    public void init() throws Throwable {
        PatchInstaller.init();

        Method target = Layout.class.getDeclaredMethod("getOffsetForHorizontal", int.class, float.class);
        Method hook = getClass().getDeclaredMethod("hookgetOffsetForHorizontal", Layout.class, int.class, float.class);
        Method backup = getClass().getDeclaredMethod("backupgetOffsetForHorizontal", Layout.class, int.class, float.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);
    }

    public static int hookgetOffsetForHorizontal(Layout layout, int line, float horiz) throws Throwable {
        Log.wtf(TAG, "NEW HOOKED!");
        /* Layout variables */
        Utils.layout = layout;
        boolean primary = true;

        @SuppressLint("DiscouragedPrivateApi") Field fmPaint = Layout.class.getDeclaredField("mPaint");
        fmPaint.setAccessible(true);
        TextPaint mPaint = (TextPaint) fmPaint.get(layout);

        @SuppressLint("SoonBlockedPrivateApi") Field fmText = Layout.class.getDeclaredField("mText");
        fmText.setAccessible(true);
        CharSequence mText = (CharSequence) fmText.get(layout);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_LENGTH_MASK = Layout.class.getDeclaredField("RUN_LENGTH_MASK");
        fRUN_LENGTH_MASK.setAccessible(true);
        int RUN_LENGTH_MASK = (int) fRUN_LENGTH_MASK.get(layout);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_RTL_FLAG = Layout.class.getDeclaredField("RUN_RTL_FLAG");
        fRUN_RTL_FLAG.setAccessible(true);
        int RUN_RTL_FLAG = (int) fRUN_RTL_FLAG.get(layout);
        /* Layout variables */

        Method getLineEnd = Layout.class.getDeclaredMethod("getLineEnd", int.class);
        getLineEnd.setAccessible(true);
        int lineEndOffset = (int) getLineEnd.invoke(layout, line);

        Method getLineStart = Layout.class.getDeclaredMethod("getLineStart", int.class);
        getLineStart.setAccessible(true);
        int lineStartOffset = (int) getLineStart.invoke(layout, line);

        Method getLineDirections = Layout.class.getDeclaredMethod("getLineDirections", int.class);
        getLineDirections.setAccessible(true);
        Layout.Directions dirs = (Layout.Directions) getLineDirections.invoke(layout, line);
        @SuppressLint("SoonBlockedPrivateApi") Field fmDirections = Layout.Directions.class.getDeclaredField("mDirections");
        fmDirections.setAccessible(true);
        int[] mDirections = (int[]) fmDirections.get(dirs);

        @SuppressLint("PrivateApi") Class<?> TextLine = Class.forName("android.text.TextLine");
        @SuppressLint("DiscouragedPrivateApi") Method obtain = TextLine.getDeclaredMethod("obtain");
        obtain.setAccessible(true);
        Object tl = obtain.invoke(null);

        @SuppressLint("PrivateApi") Class<?> TabStops = Class.forName("android.text.Layout$TabStops");

        Method getParagraphDirection = Layout.class.getDeclaredMethod("getParagraphDirection", int.class);
        getParagraphDirection.setAccessible(true);
        int res_getParagraphDirection = (int) getParagraphDirection.invoke(layout, line);

        Method set = TextLine.getDeclaredMethod("set", TextPaint.class, CharSequence.class, int.class, int.class, int.class, Layout.Directions.class, boolean.class, TabStops);
        set.setAccessible(true);
        set.invoke(tl, mPaint, mText, lineStartOffset, lineEndOffset, res_getParagraphDirection, dirs, false, null);

        Utils.HorizontalMeasurementProvider horizontal = new Utils.HorizontalMeasurementProvider(line, primary);
        int max;
        Method getLineCount = Layout.class.getDeclaredMethod("getLineCount");
        int line_count = (int) getLineCount.invoke(layout, null);

        if (line == line_count - 1) {
            max = lineEndOffset;
        } else {
            Method misRtlCharAt = Layout.class.getDeclaredMethod("isRtlCharAt", int.class);
            misRtlCharAt.setAccessible(true);
            boolean isRtlCharAt = (boolean) misRtlCharAt.invoke(layout, lineEndOffset - 1);

            @SuppressLint("SoonBlockedPrivateApi") Method getOffsetToLeftRightOf = TextLine.getDeclaredMethod("getOffsetToLeftRightOf", int.class, boolean.class);
            getOffsetToLeftRightOf.setAccessible(true);
            max = ((int) getOffsetToLeftRightOf.invoke(tl, lineEndOffset - lineStartOffset, !isRtlCharAt) + lineStartOffset);
        }
        int best = lineStartOffset;
        float bestdist = Math.abs(horizontal.get(lineStartOffset) - horiz);

        for (int i = 0; i < mDirections.length; i += 2) {
            int here = lineStartOffset + mDirections[i];
            int there = here + (mDirections[i + 1] & RUN_LENGTH_MASK);
            boolean isRtl = (mDirections[i + 1] & RUN_RTL_FLAG) != 0;
            int swap = isRtl ? -1 : 1;

            if (there > max)
                there = max;
            int high = there - 1 + 1, low = here + 1 - 1, guess;

            while (high - low > 1) {
                guess = (high + low) / 2;

                @SuppressLint("SoonBlockedPrivateApi") Method getOffsetAtStartOf = Layout.class.getDeclaredMethod("getOffsetAtStartOf", int.class);
                getOffsetAtStartOf.setAccessible(true);
                int adguess = (int) getOffsetAtStartOf.invoke(layout, guess);

                if (horizontal.get(adguess) * swap >= horiz * swap) {
                    high = guess;
                } else {
                    low = guess;
                }
            }
            if (low < here + 1)
                low = here + 1;

            if (low < there) {
                @SuppressLint("SoonBlockedPrivateApi") Method getOffsetToLeftRightOf = TextLine.getDeclaredMethod("getOffsetToLeftRightOf", int.class, boolean.class);
                getOffsetToLeftRightOf.setAccessible(true);
                int aft = ((int) getOffsetToLeftRightOf.invoke(tl, low - lineStartOffset, isRtl) + lineStartOffset);

                low = ((int) getOffsetToLeftRightOf.invoke(tl, aft - lineStartOffset, !isRtl) + lineStartOffset);
                if (low >= here && low < there) {
                    float dist = Math.abs(horizontal.get(low) - horiz);
                    if (aft < there) {
                        float other = Math.abs(horizontal.get(aft) - horiz);
                        if (other < dist) {
                            dist = other;
                            low = aft;
                        }
                    }
                    if (dist < bestdist) {
                        bestdist = dist;
                        best = low;
                    }
                }
            }
            float dist = Math.abs(horizontal.get(here) - horiz);
            if (dist < bestdist) {
                bestdist = dist;
                best = here;
            }
        }
        float dist = Math.abs(horizontal.get(max) - horiz);
        if (dist <= bestdist) {
            best = max;
        }

        Method recycle = TextLine.getDeclaredMethod("recycle", TextLine);
        recycle.setAccessible(true);
        recycle.invoke(null, tl);
        return best;
    }

    public static int backupgetOffsetForHorizontal(Layout layout, int line, float horiz) throws Throwable {
        Log.wtf(TAG, "Shouldn't be executed.");
        return -1;
    }
}
