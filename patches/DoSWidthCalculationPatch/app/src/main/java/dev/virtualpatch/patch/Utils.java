package dev.virtualpatch.patch;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.TabStopSpan;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Utils {
    public static Layout layout;

    private static float[] getLineHorizontals(int line, boolean clamped, boolean primary) throws Throwable {
        /* Layout variables */
        @SuppressLint("SoonBlockedPrivateApi") Field fmText = Layout.class.getDeclaredField("mText");
        fmText.setAccessible(true);
        CharSequence mText = (CharSequence) fmText.get(layout);

        @SuppressLint("DiscouragedPrivateApi") Field fmPaint = Layout.class.getDeclaredField("mPaint");
        fmPaint.setAccessible(true);
        TextPaint mPaint = (TextPaint) fmPaint.get(layout);

        @SuppressLint("SoonBlockedPrivateApi") Field fmWidth = Layout.class.getDeclaredField("mWidth");
        fmWidth.setAccessible(true);
        int mWidth = (int) fmWidth.get(layout);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fTAB_INCREMENT = Layout.class.getDeclaredField("TAB_INCREMENT");
        fTAB_INCREMENT.setAccessible(true);
        int TAB_INCREMENT = (int) fTAB_INCREMENT.get(layout);
        /* Layout variables */

        Method getLineStart = Layout.class.getDeclaredMethod("getLineStart", int.class);
        getLineStart.setAccessible(true);
        int start = (int) getLineStart.invoke(layout, line);

        Method getLineEnd = Layout.class.getDeclaredMethod("getLineEnd", int.class);
        getLineEnd.setAccessible(true);
        int end = (int) getLineEnd.invoke(layout, line);

        Method getParagraphDirection = Layout.class.getDeclaredMethod("getParagraphDirection", int.class);
        getParagraphDirection.setAccessible(true);
        int dir = (int) getParagraphDirection.invoke(layout, line);

        Method getLineContainsTab = Layout.class.getDeclaredMethod("getLineContainsTab", int.class);
        getLineContainsTab.setAccessible(true);
        boolean hasTab = (boolean) getLineContainsTab.invoke(layout, line);

        Method getLineDirections = Layout.class.getDeclaredMethod("getLineDirections", int.class);
        getLineDirections.setAccessible(true);
        Layout.Directions directions = (Layout.Directions) getLineDirections.invoke(layout, line);

        @SuppressLint("PrivateApi") Class<?> TabStops = Class.forName("android.text.Layout$TabStops");

        Object tabStops = null;
        if (hasTab && mText instanceof Spanned) {
            // Just checking this line should be good enough, tabs should be
            // consistent across all lines in a paragraph.
            @SuppressLint("SoonBlockedPrivateApi") Method getParagraphSpans = Layout.class.getDeclaredMethod("getParagraphSpans", Spanned.class, int.class, int.class, Class.class);
            getParagraphSpans.setAccessible(true);
            TabStopSpan[] tabs = (TabStopSpan[]) getParagraphSpans.invoke(layout, (Spanned) mText, start, end, TabStopSpan.class);
            if (tabs.length > 0) {
                Constructor<?> tabStops_con = TabStops.getDeclaredConstructor(int.class, TabStopSpan[].class);
                tabStops_con.setAccessible(true);
                tabStops = tabStops_con.newInstance(TAB_INCREMENT, tabs);
            }
        }

        @SuppressLint("PrivateApi") Class<?> TextLine = Class.forName("android.text.TextLine");
        @SuppressLint("DiscouragedPrivateApi") Method obtain = TextLine.getDeclaredMethod("obtain");
        obtain.setAccessible(true);
        Object tl = obtain.invoke(null);

        Method set = TextLine.getDeclaredMethod("set", TextPaint.class, CharSequence.class, int.class, int.class, int.class, Layout.Directions.class, boolean. class, TabStops);
        set.setAccessible(true);
        set.invoke(tl, mPaint, mText, start, end, dir, directions, hasTab, tabStops);

        boolean[] trailings = Utils.primaryIsTrailingPreviousAllLineOffsets(line);
        if (!primary) {
            for (int offset = 0; offset < trailings.length; ++offset) {
                trailings[offset] = !trailings[offset];
            }
        }
        Method measureAllOffsets = TextLine.getDeclaredMethod("measureAllOffsets", boolean[].class, Paint.FontMetricsInt.class);
        measureAllOffsets.setAccessible(true);
        float[] wid = (float[]) measureAllOffsets.invoke(tl,trailings, null);

        Method recycle = TextLine.getDeclaredMethod("recycle", TextLine);
        recycle.setAccessible(true);
        recycle.invoke(null, tl);

        if (clamped) {
            for (int offset = 0; offset <= wid.length; ++offset) {
                if (wid[offset] > mWidth) {
                    wid[offset] = mWidth;
                }
            }
        }

        Method getParagraphLeft = Layout.class.getDeclaredMethod("getParagraphLeft", int.class);
        getParagraphLeft.setAccessible(true);
        int left = (int) getParagraphLeft.invoke(layout, line);

        Method getParagraphRight = Layout.class.getDeclaredMethod("getParagraphRight", int.class);
        getParagraphRight.setAccessible(true);
        int right = (int) getParagraphRight.invoke(layout, line);

        @SuppressLint("SoonBlockedPrivateApi") Method getLineStartPos = Layout.class.getDeclaredMethod("getLineStartPos", int.class, int.class, int.class);
        getLineStartPos.setAccessible(true);
        int lineStartPos = (int) getLineStartPos.invoke(layout, line, left, right);

        float[] horizontal = new float[end - start + 1];
        for (int offset = 0; offset < horizontal.length; ++offset) {
            horizontal[offset] = lineStartPos + wid[offset];
        }
        return horizontal;
    }

    private static boolean[] primaryIsTrailingPreviousAllLineOffsets(int line) throws Throwable{
        /* Layout variables */
        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_LENGTH_MASK = Layout.class.getDeclaredField("RUN_LENGTH_MASK");
        fRUN_LENGTH_MASK.setAccessible(true);
        int RUN_LENGTH_MASK = (int) fRUN_LENGTH_MASK.get(layout);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_LEVEL_SHIFT = Layout.class.getDeclaredField("RUN_LEVEL_SHIFT");
        fRUN_LEVEL_SHIFT.setAccessible(true);
        int RUN_LEVEL_SHIFT = (int) fRUN_LEVEL_SHIFT.get(layout);

        @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fRUN_LEVEL_MASK = Layout.class.getDeclaredField("RUN_LEVEL_MASK");
        fRUN_LEVEL_MASK.setAccessible(true);
        int RUN_LEVEL_MASK = (int) fRUN_LEVEL_MASK.get(layout);
        /* Layout variables */

        Method getLineStart = Layout.class.getDeclaredMethod("getLineStart", int.class);
        getLineStart.setAccessible(true);
        int lineStart = (int) getLineStart.invoke(layout, line);

        Method getLineEnd = Layout.class.getDeclaredMethod("getLineEnd", int.class);
        getLineEnd.setAccessible(true);
        int lineEnd = (int) getLineEnd.invoke(layout, line);

        Method getLineDirections = Layout.class.getDeclaredMethod("getLineDirections", int.class);
        getLineDirections.setAccessible(true);
        Layout.Directions dirs = (Layout.Directions) getLineDirections.invoke(layout, line);
        @SuppressLint("SoonBlockedPrivateApi") Field fmDirections = Layout.Directions.class.getDeclaredField("mDirections");
        fmDirections.setAccessible(true);
        int[] runs = (int[]) fmDirections.get(dirs);

        boolean[] trailing = new boolean[lineEnd - lineStart + 1];

        byte[] level = new byte[lineEnd - lineStart + 1];
        for (int i = 0; i < runs.length; i += 2) {
            int start = lineStart + runs[i];
            int limit = start+(runs[i+1] &RUN_LENGTH_MASK);
            if (limit > lineEnd) {
                limit = lineEnd;
            }
            level[limit - lineStart - 1] =
                    (byte) ((runs[i + 1] >>>RUN_LEVEL_SHIFT) &RUN_LEVEL_MASK);
        }

        for (int i = 0; i < runs.length; i += 2) {
            int start = lineStart + runs[i];
            byte currentLevel = (byte) ((runs[i + 1] >>>RUN_LEVEL_SHIFT) &RUN_LEVEL_MASK);

            Method mgetParagraphDirection = Layout.class.getDeclaredMethod("getParagraphDirection", int.class);
            mgetParagraphDirection.setAccessible(true);
            int getParagraphDirection = (int) mgetParagraphDirection.invoke(layout, line);
            trailing[start - lineStart] = currentLevel > (start == lineStart
                    ? (getParagraphDirection == 1 ? 0 : 1)
                    : level[start - lineStart - 1]);
        }

        return trailing;
    }

    public static class HorizontalMeasurementProvider {
        private final int mLine;
        private final boolean mPrimary;
        private float[] mHorizontals;
        private int mLineStartOffset;

        HorizontalMeasurementProvider(final int line, final boolean primary) throws Throwable {
            mLine = line;
            mPrimary = primary;
            init();
        }

        private void init() throws Throwable {
            /* Layout variables */
            @SuppressLint({"SoonBlockedPrivateApi", "BlockedPrivateApi"}) Field fDIRS_ALL_LEFT_TO_RIGHT = Layout.class.getDeclaredField("DIRS_ALL_LEFT_TO_RIGHT");
            fDIRS_ALL_LEFT_TO_RIGHT.setAccessible(true);
            Layout.Directions DIRS_ALL_LEFT_TO_RIGHT = (Layout.Directions) fDIRS_ALL_LEFT_TO_RIGHT.get(layout);
            /* Layout variables */

            Method getLineDirections = Layout.class.getDeclaredMethod("getLineDirections", int.class);
            getLineDirections.setAccessible(true);
            Layout.Directions dirs = (Layout.Directions) getLineDirections.invoke(layout, mLine);

            if (dirs == DIRS_ALL_LEFT_TO_RIGHT) {
                return;
            }
            mHorizontals = getLineHorizontals(mLine, false, mPrimary);
            Method getLineStart = Layout.class.getDeclaredMethod("getLineStart", int.class);
            getLineStart.setAccessible(true);
            mLineStartOffset = (int) getLineStart.invoke(layout, mLine);
        }

        float get(final int offset) throws Throwable {
            if (mHorizontals == null || offset < mLineStartOffset || offset >= mLineStartOffset + mHorizontals.length) {
                @SuppressLint("SoonBlockedPrivateApi") Method getHorizontal = Layout.class.getDeclaredMethod("getHorizontal", int.class, boolean.class);
                getHorizontal.setAccessible(true);
                return (float) getHorizontal.invoke(layout, offset, mPrimary);
            } else {
                return mHorizontals[offset - mLineStartOffset];
            }
        }
    }
}
