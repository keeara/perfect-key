/*
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.perfectkey.keyboard.latin.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * Soft light on the top of the keyboard panel, like the light on the top edge of the keys: a thin bright line along
 * the rounded top edge that fades out downwards, and a very faint glow below it. Used as foreground of the panel,
 * which is clipped to the same squircle outline.
 */
public final class PanelTopLightDrawable extends Drawable {
    private final float mRadius;
    private final float mDensity;
    private final Paint mRimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mGlowPaint = new Paint();
    private final Path mPath = new Path();

    public PanelTopLightDrawable(final float radiusPx, final float density) {
        mRadius = radiusPx;
        mDensity = density;
        mRimPaint.setStyle(Paint.Style.STROKE);
        // half of the stroke is clipped by the outline, so this is a 1dp line on screen
        mRimPaint.setStrokeWidth(2 * density);
    }

    @Override
    protected void onBoundsChange(@NonNull final Rect bounds) {
        mPath.set(SquircleUtils.topRoundedPath(bounds.width(), bounds.height() + mRadius, mRadius, 0.7f));
        mRimPaint.setShader(new LinearGradient(0, 0, 0, 56 * mDensity,
                new int[] {0x4DFFFFFF, 0x1AFFFFFF, 0x00FFFFFF}, new float[] {0f, 0.35f, 1f}, Shader.TileMode.CLAMP));
        mGlowPaint.setShader(new LinearGradient(0, 0, 0, 90 * mDensity,
                new int[] {0x0FFFFFFF, 0x00FFFFFF}, null, Shader.TileMode.CLAMP));
    }

    @Override
    public void draw(@NonNull final Canvas canvas) {
        final Rect b = getBounds();
        canvas.drawRect(b.left, b.top, b.right, b.top + 90 * mDensity, mGlowPaint);
        canvas.drawPath(mPath, mRimPaint);
    }

    @Override
    public void setAlpha(final int alpha) { }

    @Override
    public void setColorFilter(final ColorFilter colorFilter) { }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
