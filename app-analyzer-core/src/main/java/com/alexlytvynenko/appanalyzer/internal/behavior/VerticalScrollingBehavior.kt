package com.alexlytvynenko.appanalyzer.internal.behavior

import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.view.View

internal abstract class VerticalScrollingBehavior<V : View>: CoordinatorLayout.Behavior<V>() {

    private var totalDyUnconsumed = 0
    private var totalDy = 0
    private var overScrollDirection = ScrollDirection.SCROLL_NONE
    private var scrollDirection = ScrollDirection.SCROLL_NONE

    internal enum class ScrollDirection {
        SCROLL_DIRECTION_UP,
        SCROLL_DIRECTION_DOWN,
        SCROLL_NONE
    }

    /**
     * @param coordinatorLayout
     * @param child
     * @param direction         Direction of the overscroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     * @param currentOverScroll Unconsumed value, negative or positive based on the direction;
     * @param totalOverScroll   Cumulative value for current direction
     */
    abstract fun onNestedVerticalOverScroll(coordinatorLayout: CoordinatorLayout, child: V, direction: ScrollDirection, currentOverScroll: Int, totalOverScroll: Int)

    /**
     * @param scrollDirection Direction of the overscroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     */
    abstract fun onDirectionNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, scrollDirection: ScrollDirection)

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        if (dyUnconsumed > 0 && totalDyUnconsumed < 0) {
            totalDyUnconsumed = 0
            overScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP
        } else if (dyUnconsumed < 0 && totalDyUnconsumed > 0) {
            totalDyUnconsumed = 0
            overScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN
        }
        totalDyUnconsumed += dyUnconsumed
        onNestedVerticalOverScroll(coordinatorLayout, child, overScrollDirection, dyConsumed, totalDyUnconsumed)

    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        if (dy > 0 && totalDy < 0) {
            totalDy = 0
            scrollDirection = ScrollDirection.SCROLL_DIRECTION_UP
        } else if (dy < 0 && totalDy > 0) {
            totalDy = 0
            scrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN
        }
        totalDy += dy
        onDirectionNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, scrollDirection)
    }

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
        scrollDirection = if (velocityY > 0) ScrollDirection.SCROLL_DIRECTION_UP else ScrollDirection.SCROLL_DIRECTION_DOWN
        return onNestedDirectionFling(coordinatorLayout, child, target, velocityX, velocityY, scrollDirection)
    }

    protected abstract fun onNestedDirectionFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float, scrollDirection: ScrollDirection): Boolean

}