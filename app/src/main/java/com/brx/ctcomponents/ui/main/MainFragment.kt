package com.brx.ctcomponents.ui.main

import android.animation.ValueAnimator
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
