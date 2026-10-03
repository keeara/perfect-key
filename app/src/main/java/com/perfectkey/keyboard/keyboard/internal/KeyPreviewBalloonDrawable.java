/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.perfectkey.keyboard.keyboard.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import com.perfectkey.keyboard.latin.common.ColorType;
import com.perfectkey.keyboard.latin.settings.Settings;

/**
 * key preview: a wide rounded box above the key, joined by a smooth neck to a stem
 * that covers exactly the pressed key.
 * <pre>
 *  +--------+
 *  |   x    |   box
 *  +-.    .-+
 *     |  |      neck (S-curve)
 *     +--+      stem = key footprint
 * </pre>
 */
public final class KeyPreviewBalloonDrawable extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private final RectF mArc = new RectF();
    private final float mBoxRadius;
    private final float mStemRadius;
    private final int mIntrinsicWidth;
    private final float mCompactRadius;
    private final float mCompactStroke;
    private float mFootLeft;
    private float mFootWidth;
    private float mBoxHeight;
    private float mNeckHeight;

    public KeyPreviewBalloonDrawable(final Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        mBoxRadius = 9 * density;
        mStemRadius = 6 * density;
        mIntrinsicWidth = Math.round(56 * density);
        mCompactRadius = 13 * density;
        mCompactStroke = Math.max(1f, density);
    }

    /** all values in px; [footLeft] is relative to the left edge of this drawable */
    public void setGeometry(final float footLeft, final float footWidth, final float boxHeight, final float neckHeight) {
        mFootLeft = footLeft;
        mFootWidth = footWidth;
        mBoxHeight = boxHeight;
        mNeckHeight = neckHeight;
        invalidateSelf();
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        final float w = getBounds().width();
        final float h = getBounds().height();
        // compact bubble (single key tap): a plain rounded rectangle
        if (mFootWidth <= 0) {
            mPath.rewind();
            final float cr = Math.min(mCompactRadius, Math.min(w, h) / 2);
            mArc.set(0, 0, w, h);
            mPath.addRoundRect(mArc, cr, cr, Path.Direction.CW);
            final int base = Settings.getValues().mColors.get(ColorType.KEY_BACKGROUND) | 0xFF000000;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(ColorUtils.blendARGB(base, Color.WHITE, 0.14f));
            canvas.drawPath(mPath, mPaint);
            // thin lighter edge, like the keys
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mCompactStroke);
            mPaint.setColor(0x33FFFFFF);
            mArc.inset(mCompactStroke / 2, mCompactStroke / 2);
            mPath.rewind();
            mPath.addRoundRect(mArc, cr - mCompactStroke / 2, cr - mCompactStroke / 2, Path.Direction.CW);
            canvas.drawPath(mPath, mPaint);
            mPaint.setStyle(Paint.Style.FILL);
            return;
        }
        final float fl = mFootLeft;
        final float fr = mFootLeft + mFootWidth;
        final float yb = mBoxHeight;
        final float ys = mBoxHeight + mNeckHeight;
        final float r = mBoxRadius;
        final float rs = mStemRadius;

        mPath.rewind();
        mPath.moveTo(r, 0);
        mPath.lineTo(w - r, 0);
        mArc.set(w - 2 * r, 0, w, 2 * r);
        mPath.arcTo(mArc, 270, 90);
        mPath.lineTo(w, yb);
        mPath.cubicTo(w, yb + mNeckHeight * 0.6f, fr, yb + mNeckHeight * 0.4f, fr, ys);
        mPath.lineTo(fr, h - rs);
        mArc.set(fr - 2 * rs, h - 2 * rs, fr, h);
        mPath.arcTo(mArc, 0, 90);
        mPath.lineTo(fl + rs, h);
        mArc.set(fl, h - 2 * rs, fl + 2 * rs, h);
        mPath.arcTo(mArc, 90, 90);
        mPath.lineTo(fl, ys);
        mPath.cubicTo(fl, yb + mNeckHeight * 0.4f, 0, yb + mNeckHeight * 0.6f, 0, yb);
        mPath.lineTo(0, r);
        mArc.set(0, 0, 2 * r, 2 * r);
        mPath.arcTo(mArc, 180, 90);
        mPath.close();

        // lighter than the keys, so the pressed key stands out
        final int keyColor = Settings.getValues().mColors.get(ColorType.KEY_BACKGROUND);
        mPaint.setColor(ColorUtils.blendARGB(keyColor | 0xFF000000, Color.WHITE, 0.14f));
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public void setAlpha(final int alpha) { }

    // the color is chosen in draw()
    @Override
    public void setColorFilter(final ColorFilter colorFilter) { }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
