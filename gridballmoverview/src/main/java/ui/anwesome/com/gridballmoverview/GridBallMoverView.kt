package ui.anwesome.com.gridballmoverview

/**
 * Created by anweshmishra on 24/03/18.
 */
import android.app.Activity
import android.content.*
import android.graphics.*
import android.view.*

class GridBallMoverView(ctx : Context) : View(ctx) {
    val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }
    data class State(var prevScale : Float = 0f, var j : Int = 0, var dir : Int = 0) {
        val scales : Array<Float> = arrayOf(0f, 0f, 0f)
        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir
                if (j == scales.size || j == -1) {
                    j -= dir
                    dir = 0
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0) {
                dir = 1 - 2 * prevScale.toInt()
                startcb()
            }
        }
    }
    data class Animator (var view : View, var animated : Boolean = false) {
        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }
        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }
    data class GridBallMover(var i : Int, val state : State = State()) {
        fun draw(canvas : Canvas, paint : Paint) {
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()
            paint.color = Color.parseColor("#f1c407")
            val size = Math.min(w,h)/3
            val r = size/15
            canvas.save()
            canvas.translate(-r + (w/2+r) * (1 - state.scales[2]), h/2)
            canvas.save()
            canvas.translate(-size, -size)
            for (i in 0..8) {
                val x = size/3 + (2 * size/3) * (i%3)
                val y = size/3 + (2 * size/3) * (i/3)
                canvas.save()
                canvas.translate(x + (size - x) * state.scales[1], y + (size - y) * state.scales[1])
                canvas.drawCircle(0f, 0f, r * state.scales[0], paint)
                canvas.restore()
            }
            canvas.restore()
            canvas.restore()
        }
        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }
    data class Renderer(var view : GridBallMoverView, val gridBallMover : GridBallMover = GridBallMover(0), val animator : Animator = Animator(view)) {
        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            gridBallMover.draw(canvas, paint)
            animator.animate {
                gridBallMover.update {
                    animator.stop()
                }
            }
        }
        fun handleTap() {
            gridBallMover.startUpdating {
                animator.start()
            }
        }
    }
    companion object {
        fun create(activity : Activity) : GridBallMoverView {
            val view = GridBallMoverView(activity)
            activity.setContentView(view)
            return view
        }
    }
}