package dev.virtualpatch.patch;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Patch extends PatchInstaller.BasePatch {
    public final static String TAG = "PATCH";
    private static Class<?> TextLine;

    @Override
    public void onEnvCreate() throws Throwable {
        super.onEnvCreate();
        init();
    }

    @SuppressLint({"SoonBlockedPrivateApi", "PrivateApi", "DiscouragedPrivateApi"})
    public void init() throws Throwable {
        PatchInstaller.init();

        TextLine = Class.forName("android.text.TextLine");

        Method target = TextLine.getDeclaredMethod("draw", Canvas.class, float.class, int.class, int.class, int.class);
        Method hook = getClass().getDeclaredMethod("hook_draw", Object.class, Canvas.class, float.class, int.class, int.class, int.class);
        Method backup = getClass().getDeclaredMethod("backup_draw", Object.class, Canvas.class, float.class, int.class, int.class, int.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);

        target = TextLine.getDeclaredMethod("measure", int.class, boolean.class, Paint.FontMetricsInt.class);
        hook = getClass().getDeclaredMethod("hook_measure", Object.class, int.class, boolean.class, Paint.FontMetricsInt.class);
        backup = getClass().getDeclaredMethod("backup_measure", Object.class, int.class, boolean.class, Paint.FontMetricsInt.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);

        target = TextLine.getDeclaredMethod("measureAllOffsets", boolean[].class, Paint.FontMetricsInt.class);
        hook = getClass().getDeclaredMethod("hook_measureAllOffsets", Object.class, boolean[].class, Paint.FontMetricsInt.class);
        backup = getClass().getDeclaredMethod("backup_measureAllOffsets", Object.class, boolean[].class, Paint.FontMetricsInt.class);
        PatchInstaller.hookJavaMethod(target, hook, backup);
    }

    public static void hook_draw(Object thiz, Canvas c, float x, int top, int y, int bottom) throws Throwable {
        //Log.wtf(TAG, "HOOKED! draw");
        /* VARIABLES */
        Field fmHasTabs = TextLine.getDeclaredField("mHasTabs");
        fmHasTabs.setAccessible(true);
        boolean mHasTabs = (boolean) fmHasTabs.get(thiz);

        Field fmDirections = TextLine.getDeclaredField("mDirections");
        fmDirections.setAccessible(true);
        Layout.Directions mDirections = (Layout.Directions) fmDirections.get(thiz);

        @SuppressLint("DiscouragedPrivateApi") Field fDIRS_ALL_LEFT_TO_RIGHT = Layout.class.getDeclaredField("DIRS_ALL_LEFT_TO_RIGHT");
        fDIRS_ALL_LEFT_TO_RIGHT.setAccessible(true);
        Layout.Directions DIRS_ALL_LEFT_TO_RIGHT = (Layout.Directions) fDIRS_ALL_LEFT_TO_RIGHT.get(null);

        @SuppressLint("DiscouragedPrivateApi") Field fDIRS_ALL_RIGHT_TO_LEFT = Layout.class.getDeclaredField("DIRS_ALL_RIGHT_TO_LEFT");
        fDIRS_ALL_RIGHT_TO_LEFT.setAccessible(true);
        Layout.Directions DIRS_ALL_RIGHT_TO_LEFT = (Layout.Directions) fDIRS_ALL_RIGHT_TO_LEFT.get(null);

        Field fmLen = TextLine.getDeclaredField("mLen");
        fmLen.setAccessible(true);
        int mLen = (int) fmLen.get(thiz);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_LENGTH_MASK = Layout.class.getDeclaredField("RUN_LENGTH_MASK");
        fRUN_LENGTH_MASK.setAccessible(true);
        int RUN_LENGTH_MASK = (int) fRUN_LENGTH_MASK.get(null);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_RTL_FLAG = Layout.class.getDeclaredField("RUN_RTL_FLAG");
        fRUN_RTL_FLAG.setAccessible(true);
        int RUN_RTL_FLAG = (int) fRUN_RTL_FLAG.get(null);

        @SuppressLint("SoonBlockedPrivateApi") Field fmDirections2 = Layout.Directions.class.getDeclaredField("mDirections");
        fmDirections2.setAccessible(true);
        int[] mDirections2 = (int[]) fmDirections2.get(mDirections);

        Field fmChars = TextLine.getDeclaredField("mChars");
        fmChars.setAccessible(true);
        char[] mChars = (char[]) fmChars.get(thiz);

        Field fmDir = TextLine.getDeclaredField("mDir");
        fmDir.setAccessible(true);
        int mDir = (int) fmDir.get(thiz);
        /* VARIABLES */

        /* METHODS */
        Method drawRun = TextLine.getDeclaredMethod("drawRun", Canvas.class, int.class, int.class, boolean.class, float.class, int.class, int.class, int.class, boolean.class);
        drawRun.setAccessible(true);

        Method nextTab = TextLine.getDeclaredMethod("nextTab", float.class);
        nextTab.setAccessible(true);
        /* METHODS */

        if (!mHasTabs) {
            if (mDirections == DIRS_ALL_LEFT_TO_RIGHT) {
                drawRun.invoke(thiz, c, 0, mLen, false, x, top, y, bottom, false);
                return;
            }
            if (mDirections == DIRS_ALL_RIGHT_TO_LEFT) {
                drawRun.invoke(thiz, c, 0, mLen, true, x, top, y, bottom, false);
                return;
            }
        }
        float h = 0;
        int[] runs = mDirections2;
        int lastRunIndex = runs.length - 2;
        for (int i = 0; i < runs.length; i += 2) {
            int runStart = runs[i];
            int runLimit = runStart + (runs[i + 1] & RUN_LENGTH_MASK);
            /* PATCH */
            if (runLimit > mLen) {
                Log.wtf(TAG, "break");
                break;
            }
            /* PATCH */
            boolean runIsRtl = (runs[i + 1] & RUN_RTL_FLAG) != 0;
            int segstart = runStart;
            for (int j = mHasTabs ? runStart : runLimit; j <= runLimit; j++) {
                int codept = 0;
                if (mHasTabs && j < runLimit) {
                    codept = mChars[j];
                    if (codept >= 0xD800 && codept < 0xDC00 && j + 1 < runLimit) {
                        codept = Character.codePointAt(mChars, j);
                        if (codept > 0xFFFF) {
                            ++j;
                            continue;
                        }
                    }
                }
                if (j == runLimit || codept == '\t') {
                    h += (float) drawRun.invoke(thiz, c, segstart, j, runIsRtl, x + h, top, y, bottom, i != lastRunIndex || j != mLen);
                    if (codept == '\t') {
                        h = mDir * (float) nextTab.invoke(thiz, h * mDir);
                    }
                    segstart = j + 1;
                }
            }
        }
    }

    public static void backup_draw(Object textLine, Canvas c, float x, int top, int y, int bottom) throws Throwable {
        Log.wtf(TAG, "Shouldn't be called.");
        return;
    }

    public static float hook_measure(Object thiz, int offset, boolean trailing, Paint.FontMetricsInt fmi) throws Throwable {
        //Log.wtf(TAG, "HOOKED! measure");
        /* VARIABLES */
        Field fmHasTabs = TextLine.getDeclaredField("mHasTabs");
        fmHasTabs.setAccessible(true);
        boolean mHasTabs = (boolean) fmHasTabs.get(thiz);

        Field fmDirections = TextLine.getDeclaredField("mDirections");
        fmDirections.setAccessible(true);
        Layout.Directions mDirections = (Layout.Directions) fmDirections.get(thiz);

        @SuppressLint("DiscouragedPrivateApi") Field fDIRS_ALL_LEFT_TO_RIGHT = Layout.class.getDeclaredField("DIRS_ALL_LEFT_TO_RIGHT");
        fDIRS_ALL_LEFT_TO_RIGHT.setAccessible(true);
        Layout.Directions DIRS_ALL_LEFT_TO_RIGHT = (Layout.Directions) fDIRS_ALL_LEFT_TO_RIGHT.get(null);

        @SuppressLint("DiscouragedPrivateApi") Field fDIRS_ALL_RIGHT_TO_LEFT = Layout.class.getDeclaredField("DIRS_ALL_RIGHT_TO_LEFT");
        fDIRS_ALL_RIGHT_TO_LEFT.setAccessible(true);
        Layout.Directions DIRS_ALL_RIGHT_TO_LEFT = (Layout.Directions) fDIRS_ALL_RIGHT_TO_LEFT.get(null);

        Field fmLen = TextLine.getDeclaredField("mLen");
        fmLen.setAccessible(true);
        int mLen = (int) fmLen.get(thiz);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_LENGTH_MASK = Layout.class.getDeclaredField("RUN_LENGTH_MASK");
        fRUN_LENGTH_MASK.setAccessible(true);
        int RUN_LENGTH_MASK = (int) fRUN_LENGTH_MASK.get(null);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_RTL_FLAG = Layout.class.getDeclaredField("RUN_RTL_FLAG");
        fRUN_RTL_FLAG.setAccessible(true);
        int RUN_RTL_FLAG = (int) fRUN_RTL_FLAG.get(null);

        @SuppressLint("SoonBlockedPrivateApi") Field fmDirections2 = Layout.Directions.class.getDeclaredField("mDirections");
        fmDirections2.setAccessible(true);
        int[] mDirections2 = (int[]) fmDirections2.get(mDirections);

        Field fmChars = TextLine.getDeclaredField("mChars");
        fmChars.setAccessible(true);
        char[] mChars = (char[]) fmChars.get(thiz);

        Field fmDir = TextLine.getDeclaredField("mDir");
        fmDir.setAccessible(true);
        int mDir = (int) fmDir.get(thiz);
        /* VARIABLES */

        /* METHODS */
        Method nextTab = TextLine.getDeclaredMethod("nextTab", float.class);
        nextTab.setAccessible(true);

        Method measureRun = TextLine.getDeclaredMethod("measureRun", int.class, int.class, int.class, boolean.class, Paint.FontMetricsInt.class);
        measureRun.setAccessible(true);
        /* METHODS */

        int target = trailing ? offset - 1 : offset;
        if (target < 0) {
            return 0;
        }
        float h = 0;
        if (!mHasTabs) {
            if (mDirections == DIRS_ALL_LEFT_TO_RIGHT) {
                return (float) measureRun.invoke(thiz, 0, offset, mLen, false, fmi);
            }
            if (mDirections == DIRS_ALL_RIGHT_TO_LEFT) {
                return (float) measureRun.invoke(thiz, 0, offset, mLen, true, fmi);
            }
        }
        char[] chars = mChars;
        int[] runs = mDirections2;
        for (int i = 0; i < runs.length; i += 2) {
            int runStart = runs[i];
            int runLimit = runStart + (runs[i + 1] & RUN_LENGTH_MASK);
            if (runLimit > mLen) {
                Log.wtf(TAG, "break");
                break;
            }
            boolean runIsRtl = (runs[i + 1] & RUN_RTL_FLAG) != 0;
            int segstart = runStart;
            for (int j = mHasTabs ? runStart : runLimit; j <= runLimit; j++) {
                int codept = 0;
                if (mHasTabs && j < runLimit) {
                    codept = chars[j];
                    if (codept >= 0xD800 && codept < 0xDC00 && j + 1 < runLimit) {
                        codept = Character.codePointAt(chars, j);
                        if (codept > 0xFFFF) {
                            ++j;
                            continue;
                        }
                    }
                }
                if (j == runLimit || codept == '\t') {
                    boolean inSegment = target >= segstart && target < j;
                    boolean advance = (mDir == Layout.DIR_RIGHT_TO_LEFT) == runIsRtl;
                    if (inSegment && advance) {
                        return h += (float) measureRun.invoke(thiz, segstart, offset, j, runIsRtl, fmi);
                    }
                    float w = (float) measureRun.invoke(thiz, segstart, j, j, runIsRtl, fmi);
                    h += advance ? w : -w;
                    if (inSegment) {
                        return h += (float) measureRun.invoke(thiz, segstart, offset, j, runIsRtl, null);
                    }
                    if (codept == '\t') {
                        if (offset == j) {
                            return h;
                        }
                        h = mDir * (float) nextTab.invoke(thiz, h * mDir);
                        if (target == j) {
                            return h;
                        }
                    }
                    segstart = j + 1;
                }
            }
        }
        return h;
    }

    public static float backup_measure(Object thiz, int offset, boolean trailing, Paint.FontMetricsInt fmi) throws Throwable {
        Log.wtf(TAG, "Shouldn't be called.");
        return -1;
    }

    public static float[] hook_measureAllOffsets(Object thiz, boolean[] trailing, Paint.FontMetricsInt fmi) throws Throwable {
        //Log.wtf(TAG, "HOOKED! measureAllOffsets");
        /* VARIABLES */
        Field fmHasTabs = TextLine.getDeclaredField("mHasTabs");
        fmHasTabs.setAccessible(true);
        boolean mHasTabs = (boolean) fmHasTabs.get(thiz);

        Field fmDirections = TextLine.getDeclaredField("mDirections");
        fmDirections.setAccessible(true);
        Layout.Directions mDirections = (Layout.Directions) fmDirections.get(thiz);

        @SuppressLint("DiscouragedPrivateApi") Field fDIRS_ALL_LEFT_TO_RIGHT = Layout.class.getDeclaredField("DIRS_ALL_LEFT_TO_RIGHT");
        fDIRS_ALL_LEFT_TO_RIGHT.setAccessible(true);
        Layout.Directions DIRS_ALL_LEFT_TO_RIGHT = (Layout.Directions) fDIRS_ALL_LEFT_TO_RIGHT.get(null);

        @SuppressLint("DiscouragedPrivateApi") Field fDIRS_ALL_RIGHT_TO_LEFT = Layout.class.getDeclaredField("DIRS_ALL_RIGHT_TO_LEFT");
        fDIRS_ALL_RIGHT_TO_LEFT.setAccessible(true);
        Layout.Directions DIRS_ALL_RIGHT_TO_LEFT = (Layout.Directions) fDIRS_ALL_RIGHT_TO_LEFT.get(null);

        Field fmLen = TextLine.getDeclaredField("mLen");
        fmLen.setAccessible(true);
        int mLen = (int) fmLen.get(thiz);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_LENGTH_MASK = Layout.class.getDeclaredField("RUN_LENGTH_MASK");
        fRUN_LENGTH_MASK.setAccessible(true);
        int RUN_LENGTH_MASK = (int) fRUN_LENGTH_MASK.get(null);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_RTL_FLAG = Layout.class.getDeclaredField("RUN_RTL_FLAG");
        fRUN_RTL_FLAG.setAccessible(true);
        int RUN_RTL_FLAG = (int) fRUN_RTL_FLAG.get(null);

        @SuppressLint("SoonBlockedPrivateApi") Field fmDirections2 = Layout.Directions.class.getDeclaredField("mDirections");
        fmDirections2.setAccessible(true);
        int[] mDirections2 = (int[]) fmDirections2.get(mDirections);

        Field fmChars = TextLine.getDeclaredField("mChars");
        fmChars.setAccessible(true);
        char[] mChars = (char[]) fmChars.get(thiz);

        Field fmDir = TextLine.getDeclaredField("mDir");
        fmDir.setAccessible(true);
        int mDir = (int) fmDir.get(thiz);
        /* VARIABLES */

        /* METHODS */
        Method nextTab = TextLine.getDeclaredMethod("nextTab", float.class);
        nextTab.setAccessible(true);

        Method measureRun = TextLine.getDeclaredMethod("measureRun", int.class, int.class, int.class, boolean.class, Paint.FontMetricsInt.class);
        measureRun.setAccessible(true);
        /* METHODS */

        float[] measurement = new float[mLen + 1];
        int[] target = new int[mLen + 1];
        for (int offset = 0; offset < target.length; ++offset) {
            target[offset] = trailing[offset] ? offset - 1 : offset;
        }
        if (target[0] < 0) {
            measurement[0] = 0;
        }
        float h = 0;
        if (!mHasTabs) {
            if (mDirections == DIRS_ALL_LEFT_TO_RIGHT) {
                for (int offset = 0; offset <= mLen; ++offset) {
                    measurement[offset] = (float) measureRun.invoke(thiz, 0, offset, mLen, false, fmi);
                }
                return measurement;
            }
            if (mDirections == DIRS_ALL_RIGHT_TO_LEFT) {
                for (int offset = 0; offset <= mLen; ++offset) {
                    measurement[offset] = (float) measureRun.invoke(thiz, 0, offset, mLen, true, fmi);
                }
                return measurement;
            }
        }
        char[] chars = mChars;
        int[] runs = mDirections2;
        for (int i = 0; i < runs.length; i += 2) {
            int runStart = runs[i];
            int runLimit = runStart + (runs[i + 1] & RUN_LENGTH_MASK);
            if (runLimit > mLen) {
                Log.wtf(TAG, "break");
                break;
            }
            boolean runIsRtl = (runs[i + 1] & RUN_RTL_FLAG) != 0;
            int segstart = runStart;
            for (int j = mHasTabs ? runStart : runLimit; j <= runLimit; ++j) {
                int codept = 0;
                if (mHasTabs && j < runLimit) {
                    codept = chars[j];
                    if (codept >= 0xD800 && codept < 0xDC00 && j + 1 < runLimit) {
                        codept = Character.codePointAt(chars, j);
                        if (codept > 0xFFFF) {
                            ++j;
                            continue;
                        }
                    }
                }
                if (j == runLimit || codept == '\t') {
                    float oldh = h;
                    boolean advance = (mDir == Layout.DIR_RIGHT_TO_LEFT) == runIsRtl;
                    float w = (float) measureRun.invoke(thiz, segstart, j, j, runIsRtl, fmi);
                    h += advance ? w : -w;
                    float baseh = advance ? oldh : h;
                    Paint.FontMetricsInt crtfmi = advance ? fmi : null;
                    for (int offset = segstart; offset <= j && offset <= mLen; ++offset) {
                        if (target[offset] >= segstart && target[offset] < j) {
                            measurement[offset] = baseh + (float) measureRun.invoke(thiz, segstart, offset, j, runIsRtl, crtfmi);
                        }
                    }
                    if (codept == '\t') {
                        if (target[j] == j) {
                            measurement[j] = h;
                        }
                        h = mDir * (float) nextTab.invoke(thiz, h * mDir);
                        if (target[j + 1] == j) {
                            measurement[j + 1] = h;
                        }
                    }
                    segstart = j + 1;
                }
            }
        }
        if (target[mLen] == mLen) {
            measurement[mLen] = h;
        }
        return measurement;
    }

    public static float[] backup_measureAllOffsets(Object thiz, boolean[] trailing, Paint.FontMetricsInt fmi) throws Throwable {
        Log.wtf(TAG, "Shouldn't be called.");
        return null;
    }
}