package de.thkoeln.simpleprogressbar

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.simple_progressbar.view.*

private const val PROGRESS_DEFAULT = 50
private const val SECONDARY_DEFAULT = 70
private const val MAX_DEFAULT = 100

class SimpleProgressBar(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    var primaryProgress : Int = PROGRESS_DEFAULT
        set(value) {
            field = if(value > maxProgress) maxProgress else value
            updateProgress(field, progress_primary)
            updateText()
        }

    var secondaryProgress: Int = SECONDARY_DEFAULT
        set(value) {
            field = if(value > maxProgress) maxProgress else value
            updateProgress(field, progress_secondary)
        }

    var maxProgress : Int = MAX_DEFAULT
        set(value) { field = value; draw() }

    private var radius : Float = 0f
    private var viewWidth = 0
    private var padding = 0

    private val backgroundDrawable = GradientDrawable()
    private val progressDrawable = GradientDrawable()
    private val secondaryDrawable = GradientDrawable()

    var bgColor = -1
        set(value) { field = value; backgroundDrawable.setColor(value) }

    var primaryColor = -1
        set(value) { field = value; progressDrawable.setColor(value) }

    var secondaryColor = -1
        set(value) { field = value; secondaryDrawable.setColor(value) }

    private val constraints : ConstraintSet

    init{

        LayoutInflater.from(context).inflate(R.layout.simple_progressbar, this)

        //initialize constraint set
        constraints = ConstraintSet()
        constraints.clone(progressbar_layout)

        with(context!!.theme.obtainStyledAttributes(attrs, R.styleable.SimpleProgressBar, 0, 0)){
            try {
                radius = getDimension(R.styleable.SimpleProgressBar_corner_radius, 0f)

                val defaultBgColor = ContextCompat.getColor(context, R.color.backgroundDefault)
                bgColor = getInteger(R.styleable.SimpleProgressBar_background_color, defaultBgColor)

                val defaultProgressColor = ContextCompat.getColor(context, R.color.colorPrimary)
                primaryColor = getInteger(R.styleable.SimpleProgressBar_primary_progress_color, defaultProgressColor)

                val defaultSecondaryColor = ContextCompat.getColor(context, R.color.colorSecondary)
                secondaryColor = getInteger(R.styleable.SimpleProgressBar_secondary_progress_color, defaultSecondaryColor)

                padding = getDimensionPixelSize(R.styleable.SimpleProgressBar_padding, 0)

                maxProgress = getInteger(R.styleable.SimpleProgressBar_progress_max, maxProgress)
                primaryProgress = getInteger(R.styleable.SimpleProgressBar_progress_primary, primaryProgress)
                secondaryProgress = getInteger(R.styleable.SimpleProgressBar_progress_secondary,
                        secondaryProgress)

            } finally {
                recycle()
            }
        }
        setup()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        postDelayed({ draw(); }, 15)
    }

    private fun setup(){

        backgroundDrawable.setColor(bgColor)
        backgroundDrawable.cornerRadius = radius
        progress_background.background = backgroundDrawable

        progressDrawable.setColor(primaryColor)
        progressDrawable.cornerRadius = radius
        progress_primary.background = progressDrawable

        secondaryDrawable.setColor(secondaryColor)
        secondaryDrawable.cornerRadius = radius
        progress_secondary.background = secondaryDrawable

        val progressParams = progress_primary.layoutParams as MarginLayoutParams
        progressParams.setMargins(padding, padding, padding, padding)

        val secondaryParams = progress_secondary.layoutParams as MarginLayoutParams
        secondaryParams.setMargins(padding, padding, padding, padding)
    }

    private fun draw(){
        updateProgress(primaryProgress, progress_primary)
        updateProgress(secondaryProgress, progress_secondary)
        invalidate(); requestLayout()
        updateText()
    }

    private fun updateProgress(newProgress : Int, affectedView : View){

        val affectedGuideline = when(affectedView){
            progress_primary -> primary_progress_guide
            progress_secondary -> secondary_progress_guide
            else -> null
        }

        affectedGuideline?.let {
            var progressFloat = newProgress / maxProgress.toFloat()
            progressFloat = if(progressFloat > 1f) 1f else progressFloat

            val params = affectedGuideline.layoutParams as ConstraintLayout.LayoutParams
            params.guidePercent = progressFloat
            affectedGuideline.layoutParams = params
        }

    }

    private fun updateText(){ progress_text.text = "$primaryProgress/$maxProgress" }
}
