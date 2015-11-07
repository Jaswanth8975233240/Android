package com.steppschuh.intelliq.ui;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;

import static android.animation.ValueAnimator.*;

public class AnimationHelper {

    public static final int DURATION_FAST = 250;
    public static final int DURATION_DEFAULT = 500;
    public static final int DURATION_SLOW = 1000;
    public static final int DURATION_CASUAL = 2500;

    public static void fadeToBackgroundColor(final View view, int currentColor, int targetColor, long duration) {
        ValueAnimator colorAnimation = ofObject(new ArgbEvaluator(), currentColor, targetColor);
        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });
        colorAnimation.setDuration(duration);
        colorAnimation.start();
    }

    public static void fadeToOpacity(final View view, float toValue, long duration) {
        fadeToOpacity(view, view.getAlpha(), toValue, duration);
    }

    public static void fadeToOpacity(final View view, float fromValue, float toValue, long duration) {
        ValueAnimator valueAnimator = ofObject(new FloatEvaluator(), fromValue, toValue);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setAlpha((float) animator.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }
}
