package com.tretton37.twitter37.utils.ui.customsearchview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;
import android.view.ViewAnimationUtils;

import static com.tretton37.twitter37.utils.AppConstants.ONE;
import static com.tretton37.twitter37.utils.AppConstants.ZERO;

/**
 *
 * Created by suyashg
 *
 * Utility class used to easily animate Views. Most used for revealing or hiding Views.
 */
public class AnimationUtils {

    public static final int ANIMATION_DURATION_SHORTEST = 150;
    public static final int ANIMATION_DURATION_SHORT = 250;

    @TargetApi(21)
    public static void circleRevealView(View view, int duration) {
        // get the center for the clipping circle
        int cx = view.getWidth();
        int cy = view.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, ZERO, finalRadius);

        if (duration > ZERO) {
            anim.setDuration(duration);
        }
        else {
            anim.setDuration(ANIMATION_DURATION_SHORT);
        }

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    @TargetApi(21)
    public static void circleRevealView(View view) {
        circleRevealView(view,ANIMATION_DURATION_SHORT);
    }

    @TargetApi(21)
    public static void circleHideView(final View view, AnimatorListenerAdapter listenerAdapter) {
        circleHideView(view,ANIMATION_DURATION_SHORT,listenerAdapter);
    }

    @TargetApi(21)
    public static void circleHideView(final View view, int duration, AnimatorListenerAdapter listenerAdapter) {
        // get the center for the clipping circle
        int cx = view.getWidth();
        int cy = view.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, ZERO);

        // make the view invisible when the animation is done
        anim.addListener(listenerAdapter);

        if (duration > ZERO) {
            anim.setDuration(duration);
        }
        else {
            anim.setDuration(ANIMATION_DURATION_SHORT);
        }

//        anim.setStartDelay(200);

        // start the animation
        anim.start();
    }

    public static void fadeInView(View view) {
        fadeInView(view, ANIMATION_DURATION_SHORTEST);
    }

    /**
     * Reveal the provided View with a fade-in animation.
     *
     * @param view The View that's being animated.
     * @param duration How long should the animation take, in millis.
     */
    public static void fadeInView(View view, int duration) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(ZERO);

        // Setting the listener to null, so it won't keep getting called.
        ViewCompat.animate(view).alpha(ONE).setDuration(duration).setListener(null);
    }

    /**
     *  Hide the provided View with a fade-out animation. Fast.
     *
     * @param view The View that's being animated.
     */
    public static void fadeOutView(View view) {
        fadeOutView(view, ANIMATION_DURATION_SHORTEST);
    }

    /**
     * Hide the provided View with a fade-out animation.
     *
     * @param view The View that's being animated.
     * @param duration How long should the animation take, in millis.
     */
    public static void fadeOutView(final View view, int duration) {
        ViewCompat.animate(view).alpha(ZERO).setDuration(duration).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                view.setDrawingCacheEnabled(true);
            }

            @Override
            public void onAnimationEnd(View view) {
                view.setVisibility(View.GONE);
                view.setAlpha(ONE);
                view.setDrawingCacheEnabled(false);
            }

            @Override
            public void onAnimationCancel(View view) { }
        });
    }
}
