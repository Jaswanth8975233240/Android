package com.steppschuh.intelliq.ui;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.steppschuh.intelliq.R;

import static android.animation.ValueAnimator.AnimatorUpdateListener;
import static android.animation.ValueAnimator.ofObject;

public class AnimationHelper {

    public static final int DURATION_FAST = 250;
    public static final int DURATION_DEFAULT = 500;
    public static final int DURATION_SLOW = 1000;
    public static final int DURATION_CASUAL = 2500;

    /**
     * Any view
     */
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

    /**
     * FAB
     */
    public static void fadeFabToBackgroundColor(final FloatingActionButton fab, int targetColor, long duration) {
        int currentColor = fab.getBackgroundTintList().getDefaultColor();
        ValueAnimator colorAnimation = ofObject(new ArgbEvaluator(), currentColor, targetColor);
        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                fab.setBackgroundTintList(ColorStateList.valueOf((Integer) animator.getAnimatedValue()));
            }
        });
        colorAnimation.setDuration(duration);
        colorAnimation.start();
    }

    /**
     * Status bar
     */
    public static void fadeStatusBarToDefaultColor(final Activity activity) {
        fadeStatusBarToColor(activity, ContextCompat.getColor(activity, R.color.primaryDark), DURATION_FAST);
    }

    public static void fadeStatusBarToColor(final Activity activity, int targetColor, long duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int currentColor = activity.getWindow().getStatusBarColor();
            ValueAnimator colorAnimation = ofObject(new ArgbEvaluator(), currentColor, targetColor);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    activity.getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                }
            });
            colorAnimation.setDuration(duration);
            colorAnimation.start();
        }
    }
}
