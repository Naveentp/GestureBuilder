package com.naveentp.gesturebuilder.ui.list

import android.app.AlertDialog
import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.naveentp.gesturebuilder.R
import com.naveentp.gesturebuilder.util.Utils
import kotlinx.android.synthetic.main.activity_gesture_list.*
import kotlinx.android.synthetic.main.popup_add_gesture.view.*
import java.util.*

class GestureListActivity : AppCompatActivity(), GestureActions {

    lateinit var gestureLibrary: GestureLibrary
    lateinit var gestureListAdapter: GestureListAdapter
    private var gesture: Gesture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture_list)
        title = getString(R.string.gestures_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        gestureListAdapter = GestureListAdapter(getGestureEntries())
        rvGestureList.adapter = gestureListAdapter
        rvGestureList.layoutManager = LinearLayoutManager(this)
    }

    private fun getGestureEntries(): List<GestureHolder> {
        val gestureList = ArrayList<GestureHolder>()
        try {
            gestureLibrary = GestureLibraries.fromFile(Utils.getGestureFile())
            gestureLibrary.load()
            val gestureSet = gestureLibrary.gestureEntries
            for (name in gestureSet) {
                val list = gestureLibrary.getGestures(name)
                for (g in list) {
                    gestureList.add(GestureHolder(g, name))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return gestureList
    }

    override fun onDeleteClicked(gestureDetails: GestureHolder, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.delete_gesture))
        builder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            gestureLibrary.removeEntry(gestureDetails.name)
            gestureLibrary.save()
        }
        builder.setNegativeButton(getString(R.string.cancel)
        ) { dialogInterface, _ -> dialogInterface.cancel() }
        builder.show()
    }

    private fun triggerAddGestureDialog(){
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.create_new_gesture))
        alertDialog.setCancelable(false)

        val gestureView = layoutInflater.inflate(R.layout.popup_add_gesture, null)
        val gestureOverlayView = gestureView.popupGestureAdd
        gestureOverlayView.addOnGestureListener(mGestureListener)
        val gestureNameView = gestureView.popupGestureName

        alertDialog
            .setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.save)) { dialog, which ->
                val gestureName = gestureNameView.getText().toString()
                val mGesture = gesture
                gesture = null
                if (mGesture == null) {
                    gestureNameView.error = getString(R.string.add_some_gesture)
                } else if (gestureName.isEmpty()) {
                    gestureNameView.error = getString(R.string.gesture_name_required)
                } else {
                    gestureLibrary.addGesture(gestureName, mGesture)
                    if (!gestureLibrary.save()) {
                        Toast.makeText(this, getString(R.string.not_saved), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show()
                        rvGestureList.adapter?.notifyDataSetChanged()
                    }
                }
            }

        alertDialog
            .setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)
            ) { dialog, which -> dialog.dismiss() }
        alertDialog.setView(gestureView)
        alertDialog.show()
    }

    private val mGestureListener = object : GestureOverlayView.OnGestureListener {
        override fun onGestureStarted(gestureOverlayView: GestureOverlayView, motionEvent: MotionEvent) {

        }

        override fun onGesture(gestureOverlayView: GestureOverlayView, motionEvent: MotionEvent) {
            gesture = gestureOverlayView.gesture
        }

        override fun onGestureEnded(gestureOverlayView: GestureOverlayView, motionEvent: MotionEvent) {

        }

        override fun onGestureCancelled(gestureOverlayView: GestureOverlayView, motionEvent: MotionEvent) {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menuAddGesture -> {
                triggerAddGestureDialog()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
