package com.naveentp.gesturebuilder.ui.list

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.naveentp.gesturebuilder.R
import kotlinx.android.synthetic.main.activity_gesture_list.*
import kotlinx.android.synthetic.main.gesture_list_item.view.*

/**
 * @author Naveen T P
 * @since 14/03/19
 */
class GestureListAdapter(private val gestureList: List<GestureHolder>) : RecyclerView.Adapter<GestureListAdapter.ViewHolder>() {

    lateinit var gestureActions: GestureActions
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.gesture_list_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = gestureList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(gestureList[position])
        holder.itemView.itemGestureDelete.setOnClickListener {
            gestureActions = context as GestureActions
            gestureActions.onDeleteClicked(gestureList[position], position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(gestureItem: GestureHolder) {
            itemView.itemImgGesture.setImageBitmap(gestureItem.gesture.toBitmap(60, 60, 3, Color.BLUE))
            itemView.itemGestureName.text = gestureItem.name
        }
    }
}