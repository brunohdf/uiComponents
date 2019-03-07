package com.brx.ctcomponents.ui.main

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.RadioButton
import android.widget.Toast
import com.brx.ctcomponents.R
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private var set = false
    private val constraint1 = ConstraintSet()
    private val constraint2 = ConstraintSet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        constraint1.clone(root)
        constraint2.clone(activity, R.layout.twice_selector_right)

        toggle.setOnCheckedChangeListener { radioGroup, checkedItem ->
            val item: RadioButton = radioGroup.findViewById(checkedItem)
            Toast.makeText(activity, "${item.text} selected", Toast.LENGTH_SHORT).show()
            highlightSelected(checkedItem, item)
            motionTransition()
            alphaTransition()
        }
        addGestureEvents()

        // animacao para selecao por clique
        toggle2.setOnCheckedChangeListener { radioGroup, checkedItem ->
            val item: RadioButton = radioGroup.findViewById(checkedItem)
            Toast.makeText(activity, "${item.text} selected", Toast.LENGTH_SHORT).show()
            highlightSelected2(checkedItem, item)

            if (item.id == R.id.first2) {
                motionLayout.transitionToStart()
            } else {
                motionLayout.transitionToEnd()
            }
        }

        // propaga gestures para o MotionLayout sobreposto
        first2.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE)
                motionLayout.onTouchEvent(motionEvent)
            else if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_UP)
                motionLayout.onTouchEvent(motionEvent)
            else view.onTouchEvent(motionEvent)

            false
        }

        // propaga gestures para o MotionLayout sobreposto
        second2.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE || motionEvent.action == MotionEvent.ACTION_UP)
                motionLayout.onTouchEvent(motionEvent)
            else view.onTouchEvent(motionEvent)

            false
        }


        // View Animation - bounce?
        var leftRight = true
        imageView.setOnClickListener {

            val display = activity?.windowManager?.defaultDisplay
            val size = Point()
            display?.getSize(size)

            val containerRightEdge = main.width - main.paddingRight
            val imgWidth = imageView.width
            val imgUltimateX = if (leftRight) (containerRightEdge - imgWidth) else 0

            val posAnim = imageView.animate()
            posAnim.x(imgUltimateX.toFloat()).setDuration(1000).start()
            //
            val startColor =
                ContextCompat.getColor(context!!, if (leftRight) R.color.lightBlue else R.color.lightOrange)
            val endColor = ContextCompat.getColor(context!!, if (leftRight) R.color.lightOrange else R.color.lightBlue)
            val bgAnim = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
            bgAnim.duration = 1000
            bgAnim.addUpdateListener { animation ->
                imageView.setColorFilter(animation.animatedValue as Int)
            }
            bgAnim.start()
            leftRight = !leftRight
        }


        // Property Animation - Transição de cor / movimento até o limite
        imageView2.setOnClickListener {
            val set = AnimatorInflater.loadAnimator(context, R.animator.anim) as AnimatorSet
            set.setTarget(imageView2)
            set.start()
        }
    }

    // Destaca o elemento selecionado (bold)
    private fun highlightSelected(checkedItem: Int, item: RadioButton) {
        if (checkedItem == R.id.first) {
            item.setTypeface(null, Typeface.BOLD)
            second.setTypeface(null, Typeface.NORMAL)
        } else {
            first.setTypeface(null, Typeface.NORMAL)
            item.setTypeface(null, Typeface.BOLD)
        }
    }

    private fun highlightSelected2(checkedItem: Int, item: RadioButton) {
        if (checkedItem == R.id.first2) {
            item.setTypeface(null, Typeface.BOLD)
            second2.setTypeface(null, Typeface.NORMAL)
        } else {
            first2.setTypeface(null, Typeface.NORMAL)
            item.setTypeface(null, Typeface.BOLD)
        }
    }

    private fun addGestureEvents() {
        val leftGesture = GestureDetector(
            activity,
            TwiceSelectorGestureDetector(object : TwiceSelectorGestureDetector.LeftGestureListener {
                override fun onGesture() {
                    first.performClick()
                }
            })
        )

        val rightGesture = GestureDetector(
            activity,
            TwiceSelectorGestureDetector(rightGesture = object : TwiceSelectorGestureDetector.RightGestureListener {
                override fun onGesture() {
                    second.performClick()
                }
            })
        )

        first.setOnTouchListener { _, event -> rightGesture.onTouchEvent(event) }
        second.setOnTouchListener { _, event -> leftGesture.onTouchEvent(event) }
    }

    // Realia a animação lateral enter os estados
    private fun motionTransition() {
        // transition between two layouts(just layout attribs)
        TransitionManager.beginDelayedTransition(root)
        val constraint = if (set) constraint1 else constraint2
        constraint.applyTo(root)
        set = !set
    }

    // Realiza a transição entre os dois estados de selecao.
    // O primeiro estado perde visibilidade gradualmente enquando o segundo tem sua visibilidade incrementada
    private fun alphaTransition() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { animation ->
            selection_indicator.alpha = when {
                set -> 1 - animation.animatedValue as Float
                else -> animation.animatedValue as Float
            }

            selection_indicator2.alpha = when {
                set -> animation.animatedValue as Float
                else -> 1 - animation.animatedValue as Float
            }
        }

        animator.duration = 300
        animator.start()
    }
}
