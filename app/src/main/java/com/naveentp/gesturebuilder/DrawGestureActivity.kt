package com.naveentp.gesturebuilder

import android.content.Intent
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.gesture.Prediction
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_draw_gesture.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


class DrawGestureActivity : AppCompatActivity() {

    lateinit var gestureLibrary: GestureLibrary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_gesture)
        setSupportActionBar(toolbar)

        gestureLibrary = getGestureInstance()
        gestureLibrary.load()
        tvNoGestureMsg.text = getString(R.string.draw_gesture_here)

        gestureOverlayView.addOnGesturePerformedListener { _, gesture ->
            val predictions: ArrayList<Prediction> = gestureLibrary.recognize(gesture)
            if (predictions.size > 0 && predictions[0].score > 1.0) {
                Snackbar.make(fab, "${predictions[0].score}", BaseTransientBottomBar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(fab, getString(R.string.no_gesture_found), BaseTransientBottomBar.LENGTH_SHORT).show()
            }
        }

        gestureOverlayView.addOnGestureListener(object : GestureOverlayView.OnGestureListener {
            override fun onGestureStarted(overlay: GestureOverlayView?, event: MotionEvent?) {
                tvNoGestureMsg.visibility = View.GONE
            }

            override fun onGestureCancelled(overlay: GestureOverlayView?, event: MotionEvent?) {
            }

            override fun onGesture(overlay: GestureOverlayView?, event: MotionEvent?) {
            }

            override fun onGestureEnded(overlay: GestureOverlayView?, event: MotionEvent?) {
            }
        })

        fab.setOnClickListener {
            startActivity(Intent(this, GestureListActivity::class.java))
        }
    }

    /**
     * Creates a path "Gesture/gesture.txt" inside internal storage
     * Reading gestures from "gestures.txt"
     * */
    private fun getGestureInstance(): GestureLibrary {
        val directory = File(Environment.getExternalStorageDirectory(), "/gesture")
        if (!directory.exists()) {
            directory.mkdir()
        }
        val gestureFile = File(directory.absoluteFile, "gestures.txt")
        if (!gestureFile.exists()) {
            gestureFile.createNewFile()
        }
        return GestureLibraries.fromFile(gestureFile)
    }
}
