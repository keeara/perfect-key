/*
 * Copyright (C) 2014 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */

package com.perfectkey.keyboard.keyboard.internal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.perfectkey.keyboard.keyboard.Key;
import com.perfectkey.keyboard.latin.common.ColorType;
import com.perfectkey.keyboard.latin.common.Colors;
import com.perfectkey.keyboard.latin.common.CoordinateUtils;
import com.perfectkey.keyboard.latin.settings.Settings;
import com.perfectkey.keyboard.latin.utils.ViewLayoutUtils;

import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * This class controls pop up key previews. This class decides:
 * - what kind of key previews should be shown.
 * - where key previews should be placed.
 * - how key previews should be shown and dismissed.
 */
public final class KeyPreviewChoreographer {
    // Free {@link KeyPreviewView} pool that can be used for key preview.
    private final ArrayDeque<KeyPreviewView> mFreeKeyPreviewViews = new ArrayDeque<>();
    // Map from {@link Key} to {@link KeyPreviewView} that is currently being displayed as key
    // preview.
    private final HashMap<Key,KeyPreviewView> mShowingKeyPreviewViews = new HashMap<>();

    private final KeyPreviewDrawParams mParams;

    public KeyPreviewChoreographer(final KeyPreviewDrawParams params) {
        mParams = params;
    }

    public KeyPreviewView getKeyPreviewView(final Key key, final ViewGroup placerView) {
        KeyPreviewView keyPreviewView = mShowingKeyPreviewViews.remove(key);
        if (keyPreviewView == null) keyPreviewView = mFreeKeyPreviewViews.poll();
        if (keyPreviewView != null) {
            keyPreviewView.animate().cancel();
            keyPreviewView.setAlpha(1f);
            return keyPreviewView;
        }
        final Context context = placerView.getContext();
        keyPreviewView = new KeyPreviewView(context, null /* attrs */);
        keyPreviewView.setBackground(new KeyPreviewBalloonDrawable(context));
        placerView.addView(keyPreviewView, ViewLayoutUtils.newLayoutParam(placerView, 0, 0));
        return keyPreviewView;
    }

    public boolean isShowingKeyPreview(final Key key) {
        return mShowingKeyPreviewViews.containsKey(key);
    }

    public void dismissKeyPreview(final Key key) {
        if (key == null) {
            return;
        }
        final KeyPreviewView keyPreviewView = mShowingKeyPreviewViews.get(key);
        if (keyPreviewView == null) {
            return;
        }
        // Dismiss preview
        mShowingKeyPreviewViews.remove(key);
        keyPreviewView.setTag(null);
        // a very short fade, so quick typing does not make the balloons flicker on and off
        keyPreviewView.animate().cancel();
        if (com.perfectkey.keyboard.latin.utils.UiAnimations.enabled(keyPreviewView.getContext())) {
            keyPreviewView.animate().alpha(0f).setDuration(55).withEndAction(() -> {
                keyPreviewView.setVisibility(View.INVISIBLE);
                keyPreviewView.setAlpha(1f);
                mFreeKeyPreviewViews.add(keyPreviewView);
            }).start();
        } else {
            keyPreviewView.setVisibility(View.INVISIBLE);
            mFreeKeyPreviewViews.add(keyPreviewView);
        }
    }

    public void placeAndShowKeyPreview(final Key key, final KeyboardIconsSet iconsSet,
            final KeyDrawParams drawParams, final int fullKeyboardViewWidth, final int[] keyboardOrigin,
            final ViewGroup placerView) {
        final KeyPreviewView keyPreviewView = getKeyPreviewView(key, placerView);
        placeKeyPreview(key, keyPreviewView, iconsSet, drawParams, fullKeyboardViewWidth, keyboardOrigin);
        showKeyPreview(key, keyPreviewView);
    }

    private void placeKeyPreview(Key key, KeyPreviewView keyPreviewView, KeyboardIconsSet iconsSet,
            KeyDrawParams drawParams, int fullKeyboardViewWidth, int[] originCoords) {
        keyPreviewView.setPreviewVisual(key, iconsSet, drawParams);
        // a small rounded bubble with the character, right above the pressed key (no neck, does not cover the key)
        final int keyHeight = key.getHeight();
        final float density = keyPreviewView.getResources().getDisplayMetrics().density;
        final int previewWidth = Math.round(key.getDrawWidth() * 1.3f);
        final int gap = Math.round(3 * density);
        int boxHeight = Math.round(keyHeight * 0.9f);
        // the bubble must stay inside the keyboard window, so it gets lower for the top row
        final int available = key.getY() + CoordinateUtils.y(originCoords) - mParams.mPreviewOffset;
        if (boxHeight + gap > available)
            boxHeight = Math.max(Math.round(keyHeight * 0.62f), available - gap);
        final int neckHeight = 0;
        final int previewHeight = boxHeight;
        keyPreviewView.setPadding(0, 0, 0, 0); // text is centered in the bubble
        if (key.getIconName() == null) {
            // the character has to fit in the small bubble, also letters with a descender (g, y, p...)
            keyPreviewView.setIncludeFontPadding(false);
            keyPreviewView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                    keyHeight * 0.56f * Settings.getValues().mFontSizeMultiplier);
        }
        keyPreviewView.measure(View.MeasureSpec.makeMeasureSpec(previewWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(previewHeight, View.MeasureSpec.EXACTLY));
        mParams.setGeometry(keyPreviewView);
        int keyDrawWidth = key.getDrawWidth();
        int originX = CoordinateUtils.x(originCoords);
        // The key preview is horizontally aligned with the center of the visible part of the
        // parent key. If it doesn't fit in this {@link KeyboardView}, it is moved inward to fit and
        // the left/right background is used if such background is specified.
        int keyPreviewPosition;
        int previewX = key.getDrawX() - (previewWidth - keyDrawWidth) / 2 + originX;
        if (previewX < originX) {
            previewX = originX;
            keyPreviewPosition = KeyPreviewView.POSITION_LEFT;
        } else if (previewX > fullKeyboardViewWidth - previewWidth + originX) {
            previewX = fullKeyboardViewWidth - previewWidth + originX;
            keyPreviewPosition = KeyPreviewView.POSITION_RIGHT;
        } else {
            keyPreviewPosition = KeyPreviewView.POSITION_MIDDLE;
        }
        boolean hasPopupKeys = (key.getPopupKeys() != null);
        keyPreviewView.setPreviewBackground(hasPopupKeys, keyPreviewPosition);
        if (keyPreviewView.getBackground() instanceof KeyPreviewBalloonDrawable balloon) {
            balloon.setGeometry(0, 0, boxHeight, 0); // compact bubble: no stem
        }
        Colors colors = Settings.getValues().mColors;
        colors.setBackground(keyPreviewView, ColorType.KEY_PREVIEW_BACKGROUND);

        // The key preview is placed vertically above the top edge of the parent key with an
        // arbitrary offset.
        int previewY = key.getY() - previewHeight - gap - mParams.mPreviewOffset
                + CoordinateUtils.y(originCoords);

        ViewLayoutUtils.placeViewAt(keyPreviewView, previewX, previewY, previewWidth, previewHeight);
        keyPreviewView.setPivotX(previewWidth / 2.0f);
        keyPreviewView.setPivotY(previewHeight);
    }

    void showKeyPreview(final Key key, final KeyPreviewView keyPreviewView) {
        keyPreviewView.setVisibility(View.VISIBLE);
        mShowingKeyPreviewViews.put(key, keyPreviewView);
    }

}
