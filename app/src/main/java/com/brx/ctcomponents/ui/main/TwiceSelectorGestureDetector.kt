package com.brx.ctcomponents.ui.main

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

class TwiceSelectorGestureDetector(private var leftGesture: LeftGestureListener? = null,
                                   private var rightGesture: RightGestureListener? = null) :
    GestureDetector.SimpleOnGestureListener() {
    private val swipeMinDistance = 100
    private val swipeMax = 250
    private val velocity = 200

    override fun onFling(
        startEvent: MotionEvent,
        finalEvent: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        try {
            if (Math.abs(startEvent.y - finalEvent.y) > swipeMax) return false

            if (startEvent.x - finalEvent.x > swipeMinDistance && Math.abs(velocityX) > velocity)
                leftGesture?.onGesture()
            else if (finalEvent.x - startEvent.x > swipeMinDistance && Math.abs(velocityX) > velocity)
                rightGesture?.onGesture()

        } catch (e: Exception) {
            Log.d(this.javaClass.name, e.message)
        }

        return true
    }

    interface LeftGestureListener {
        fun onGesture()
    }

    interface RightGestureListener {
        fun onGesture()
    }
}