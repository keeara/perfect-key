/*
 * Copyright (C) 2012 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */

package com.perfectkey.keyboard.latin;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.View;

import com.perfectkey.keyboard.event.HapticEvent;
import com.perfectkey.keyboard.keyboard.internal.keyboard_parser.floris.KeyCode;
import com.perfectkey.keyboard.latin.common.Constants;
import com.perfectkey.keyboard.latin.settings.SettingsValues;

/**
 * This class gathers audio feedback and haptic feedback functions.
 * <p>
 * It offers a consistent and simple interface that allows LatinIME to forget about the
 * complexity of settings and the like.
 */
public final class AudioAndHapticFeedbackManager {
    private AudioManager mAudioManager;
    private Vibrator mVibrator;
    // Light, crisp haptics from vibration primitives (API 30+, needs a haptic actuator that supports them,
    // e.g. Pixel phones). Built once, so a key press does not allocate anything.
    private VibrationEffect mKeyPressEffect;
    private VibrationEffect mKeyRepeatEffect;
    private VibrationEffect mLongPressEffect;
    private VibrationEffect mGestureMoveEffect;
    private static final float KEY_PRESS_STRENGTH = 1.0f;
    // soft CLICK layered on the TICK gives the key press more body without making it harsher
    private static final float KEY_PRESS_BODY_STRENGTH = 0.28f;
    private static final float KEY_REPEAT_STRENGTH = 0.7f;
    private static final float LONG_PRESS_STRENGTH = 1.0f;
    private static final float GESTURE_MOVE_STRENGTH = 0.6f;

    private SettingsValues mSettingsValues;
    private boolean mSoundOn;
    private boolean mDoNotDisturb;

    private static final AudioAndHapticFeedbackManager sInstance =
            new AudioAndHapticFeedbackManager();

    public static AudioAndHapticFeedbackManager getInstance() {
        return sInstance;
    }

    private AudioAndHapticFeedbackManager() {
        // Intentional empty constructor for singleton.
    }

    public static void init(final Context context) {
        sInstance.initInternal(context);
    }

    private void initInternal(final Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        initPrimitiveEffects();
    }

    private void initPrimitiveEffects() {
        mKeyPressEffect = mKeyRepeatEffect = mLongPressEffect = mGestureMoveEffect = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || mVibrator == null) return;
        if (!mVibrator.areAllPrimitivesSupported(VibrationEffect.Composition.PRIMITIVE_TICK,
                VibrationEffect.Composition.PRIMITIVE_LOW_TICK, VibrationEffect.Composition.PRIMITIVE_CLICK)) return;
        // a crisp tick followed right away by a low tick gives the tap some body without getting buzzy
        mKeyPressEffect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, KEY_PRESS_STRENGTH)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, KEY_PRESS_BODY_STRENGTH, 0).compose();
        mKeyRepeatEffect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, KEY_REPEAT_STRENGTH).compose();
        mLongPressEffect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, LONG_PRESS_STRENGTH).compose();
        mGestureMoveEffect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_LOW_TICK, GESTURE_MOVE_STRENGTH).compose();
    }

    private VibrationEffect primitiveEffectFor(final HapticEvent hapticEvent) {
        return switch (hapticEvent) {
            case KEY_PRESS -> mKeyPressEffect;
            case KEY_REPEAT -> mKeyRepeatEffect;
            case KEY_LONG_PRESS -> mLongPressEffect;
            case GESTURE_MOVE -> mGestureMoveEffect;
            default -> null;
        };
    }

    public void performHapticAndAudioFeedback(
        final int code,
        final View viewToPerformHapticFeedbackOn,
        final HapticEvent hapticEvent
    ) {
        performHapticFeedback(viewToPerformHapticFeedbackOn, hapticEvent);
        performAudioFeedback(code, hapticEvent);
    }

    public boolean hasVibrator() {
        return mVibrator != null && mVibrator.hasVibrator();
    }

    public void vibrate(final long milliseconds) {
        if (mVibrator == null || milliseconds <= 0) {
            return;
        }
        mVibrator.vibrate(milliseconds);
    }

    private boolean reevaluateIfSoundIsOn() {
        if (mSettingsValues == null || !mSettingsValues.mSoundOn || mAudioManager == null || mDoNotDisturb) {
            return false;
        }
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    public void performAudioFeedback(final int code, final HapticEvent hapticEvent) {
        // if mAudioManager is null, we can't play a sound anyway, so return
        if (mAudioManager == null) {
            return;
        }
        if (!mSoundOn) {
            return;
        }
        if (hapticEvent != HapticEvent.KEY_PRESS) {
            return;
        }
        final int sound = switch (code) {
            case KeyCode.DELETE -> AudioManager.FX_KEYPRESS_DELETE;
            case Constants.CODE_ENTER -> AudioManager.FX_KEYPRESS_RETURN;
            case Constants.CODE_SPACE -> AudioManager.FX_KEYPRESS_SPACEBAR;
            default -> AudioManager.FX_KEYPRESS_STANDARD;
        };
        mAudioManager.playSoundEffect(sound, mSettingsValues.mKeypressSoundVolume);
    }

    public void performHapticFeedback(final View viewToPerformHapticFeedbackOn, final HapticEvent hapticEvent) {
        if (!mSettingsValues.mVibrateOn || (mDoNotDisturb && !mSettingsValues.mVibrateInDndMode)) {
            return;
        }
        if (hapticEvent == HapticEvent.NO_HAPTICS) {
            // Avoid surprises with the handling of HapticFeedbackConstants.NO_HAPTICS
            return;
        }
        if (hapticEvent.allowCustomDuration && mSettingsValues.mKeypressVibrationDuration >= 0) {
            vibrate(mSettingsValues.mKeypressVibrationDuration);
            return;
        }
        final VibrationEffect primitiveEffect = mVibrator == null ? null : primitiveEffectFor(hapticEvent);
        if (primitiveEffect != null) {
            mVibrator.vibrate(primitiveEffect);
            return;
        }
        // Go ahead with the system default
        if (viewToPerformHapticFeedbackOn != null) {
            viewToPerformHapticFeedbackOn.performHapticFeedback(
                    hapticEvent.feedbackConstant,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        }
    }

    public void onSettingsChanged(final SettingsValues settingsValues) {
        mSettingsValues = settingsValues;
        mSoundOn = reevaluateIfSoundIsOn();
    }

    public void onRingerModeChanged(boolean doNotDisturb) {
        mDoNotDisturb = doNotDisturb;
        mSoundOn = reevaluateIfSoundIsOn();
    }
}
