package com.naveentp.gesturebuilder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.gesture.Prediction
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_draw_gesture.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


class DrawGestureActivity : AppCompatActivity() {

    lateinit var gestureLibrary: GestureLibrary
    private val MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_gesture)
        setSupportActionBar(toolbar)

        loadGesturesWithRuntimePermission()
        tvNoGestureMsg.text = getString(R.string.draw_gesture_here)

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


    private fun loadGestures() {
        gestureLibrary = getGestureInstance()
        gestureLibrary.load()

        gestureOverlayView.addOnGesturePerformedListener { _, gesture ->
            val predictions: ArrayList<Prediction> = gestureLibrary.recognize(gesture)
            if (predictions.size > 0 && predictions[0].score > 1.0) {
                Snackbar.make(fab, "${predictions[0].score}", BaseTransientBottomBar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(fab, getString(R.string.no_gesture_found), BaseTransientBottomBar.LENGTH_SHORT).show()
            }
            tvNoGestureMsg.visibility = View.VISIBLE
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
    }

    /**
     * Runtime permissions
     * */
    private fun loadGesturesWithRuntimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE
                    )
                } else {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE
                    )
                }
            }
        } else {
            loadGestures()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadGestures()
                } else {
                    finish()
                }
            }
        }
    }
}
