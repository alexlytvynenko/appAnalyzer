package com.alexlytvynenko.appanalyzer.internal.behavior

import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.View
import android.view.ViewGroup

/**
 *
 */
internal class BottomNavigationBehavior<V : View> : VerticalScrollingBehavior<V>() {

    private val INTERPOLATOR = LinearOutSlowInInterpolator()
    private val ANIM_DURATION = 300

    /**
     * Is hidden
     * @return
     */
    var isHidden = false
        private set
    private var translationAnimator: ViewPropertyAnimatorCompat? = null
    private var snackbarLayout: Snackbar.SnackbarLayout? = null
    private var mSnackbarHeight = -1
    private var behaviorTranslationEnabled = true

    override fun layoutDependsOn(parent: CoordinatorLayout?, child: V?, dependency: View?): Boolean {
        if (dependency != null && dependency is Snackbar.SnackbarLayout) {
            updateSnackbar(child, dependency)
            return true
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onNestedVerticalOverScroll(coordinatorLayout: CoordinatorLayout, child: V, direction: ScrollDirection, currentOverScroll: Int, totalOverScroll: Int) {}

    override fun onDirectionNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, scrollDirection: ScrollDirection) {}

    protected override fun onNestedDirectionFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float, scrollDirection: ScrollDirection): Boolean {
        return false
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (dyConsumed < 0) {
            handleDirection(child, ScrollDirection.SCROLL_DIRECTION_DOWN)
        } else if (dyConsumed > 0) {
            handleDirection(child, ScrollDirection.SCROLL_DIRECTION_UP)
        }
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes)
    }

    /**
     * Handle scroll direction
     * @param child
     * @param scrollDirection
     */
    private fun handleDirection(child: V, scrollDirection: ScrollDirection) {
        if (!behaviorTranslationEnabled) {
            return
        }
        if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_DOWN && isHidden) {
            isHidden = false
            animateOffset(child, 0, false, true)
        } else if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_UP && !isHidden) {
            isHidden = true
            animateOffset(child, child.height, false, true)
        }
    }

    /**
     * Animate offset
     *
     * @param child
     * @param offset
     */
    private fun animateOffset(child: V, offset: Int, forceAnimation: Boolean, withAnimation: Boolean) {
        if (!behaviorTranslationEnabled && !forceAnimation) {
            return
        }
        ensureOrCancelAnimator(child, withAnimation)
        translationAnimator!!.translationY(offset.toFloat()).start()
    }

    /**
     * Manage animation for Android >= KITKAT
     *
     * @param child
     */
    private fun ensureOrCancelAnimator(child: V, withAnimation: Boolean) {
        if (translationAnimator == null) {
            translationAnimator = ViewCompat.animate(child)
            translationAnimator!!.duration = (if (withAnimation) ANIM_DURATION else 0).toLong()
            translationAnimator!!.interpolator = INTERPOLATOR
        } else {
            translationAnimator!!.duration = (if (withAnimation) ANIM_DURATION else 0).toLong()
            translationAnimator!!.cancel()
        }
    }

    /**
     * Update Snackbar bottom margin
     */
    private fun updateSnackbar(child: View?, dependency: View?) {

        if (dependency != null && dependency is Snackbar.SnackbarLayout) {

            snackbarLayout = dependency

            if (mSnackbarHeight == -1) {
                mSnackbarHeight = dependency.height
            }

            val targetMargin = (child!!.measuredHeight - child.translationY).toInt()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                child.bringToFront()
            }

            if (dependency.layoutParams is ViewGroup.MarginLayoutParams) {
                val p = dependency.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, targetMargin)
                dependency.requestLayout()
            }
        }
    }

    internal fun reset(child: V) {
        animateOffset(child, 0, false, false)
    }
}