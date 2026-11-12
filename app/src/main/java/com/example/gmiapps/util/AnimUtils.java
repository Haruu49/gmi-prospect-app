package com.example.gmiapps.util;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.gmiapps.R;

/**
 * Small, reusable animations (property animations) used across the app.
 * Every method does nothing when the user has turned animations off in the system
 * settings, so the app stays accessible.
 */
public final class AnimUtils {

    private static final long REVEAL_DURATION_MS = 450;

    private AnimUtils() {
    }

    /** True unless the user has disabled animations (Developer options / Accessibility). */
    public static boolean enabled() {
        return ValueAnimator.areAnimatorsEnabled();
    }

    /** Fades in and slides up a view after {@code delayMs}. */
    public static void reveal(View view, long delayMs) {
        if (!enabled() || view == null || view.getVisibility() != View.VISIBLE) {
            return;
        }
        float distance = 24 * view.getResources().getDisplayMetrics().density;
        view.setAlpha(0f);
        view.setTranslationY(distance);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delayMs)
                .setDuration(REVEAL_DURATION_MS)
                .setInterpolator(new DecelerateInterpolator(2f))
                .start();
    }

    /**
     * Reveals the visible children of {@code group} one after another (a "stagger" effect).
     *
     * @param fromIndex first child to animate (earlier children are left alone)
     * @param stepMs    extra delay between one child and the next
     */
    public static void revealChildren(ViewGroup group, int fromIndex, long startDelayMs, long stepMs) {
        if (!enabled() || group == null) {
            return;
        }
        long delay = startDelayMs;
        for (int i = fromIndex; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                reveal(child, delay);
                delay += stepMs;
            }
        }
    }

    /** "Draws" a line from left to right by growing it horizontally. */
    public static void growFromStart(View view, long delayMs) {
        if (!enabled() || view == null) {
            return;
        }
        view.setPivotX(0f);
        view.setScaleX(0f);
        view.animate()
                .scaleX(1f)
                .setStartDelay(delayMs)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /** Pops a view in: grows from small to full size with a slight overshoot. */
    public static void pop(View view, long delayMs) {
        if (!enabled() || view == null) {
            return;
        }
        view.setAlpha(0f);
        view.setScaleX(0.4f);
        view.setScaleY(0.4f);
        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(delayMs)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator(2.5f))
                .start();
    }

    /** Gently pulses a view a few times to draw attention to it (e.g. Apply Online). */
    public static void pulse(View view, long delayMs) {
        if (!enabled() || view == null) {
            return;
        }
        ObjectAnimator pulse = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.05f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.05f));
        pulse.setDuration(450);
        pulse.setRepeatCount(3);
        pulse.setRepeatMode(ValueAnimator.REVERSE);
        pulse.setStartDelay(delayMs);
        pulse.start();
    }

    /** Shakes a view left and right (XML animation res/anim/shake.xml). */
    public static void shake(View view) {
        if (!enabled() || view == null) {
            return;
        }
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.shake));
    }
}
