package com.testtube.gstreporter.views.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.testtube.gstreporter.R
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.views.vInterface.RecyclerViewInterface

class ImageRecyclerViewAdapter(
    var context: Context,
    var clickListener: RecyclerViewInterface? = null
) :
    RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder>() {
    var paths: MutableList<String?> = mutableListOf<String?>()

    init {
        init()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.image_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        try {
            val bitmap: Bitmap? = Common.grabBitMapfromFileAsync(context, paths[position], 96)
            if (bitmap != null) {
                holder.imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                holder.imageView.setImageBitmap(bitmap)
                holder.itemView.tag = true
            } else {
                holder.itemView.tag = false
                holder.imageView.setImageResource(R.drawable.ic_baseline_photo_camera_24)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return paths.size
    }

    fun removeAll() {
        paths.clear()
        paths.add(null)
        notifyDataSetChanged()
    }

    fun init() {
        paths.add(null)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imageView: ImageView
        override fun onClick(v: View) {
            val tag: Boolean = v.tag as Boolean
            if (!tag) {
                clickListener!!.onClick(adapterPosition)
            }
        }

        init {
            imageView = itemView.findViewById(R.id.input_bol_image)
            itemView.setOnClickListener(this)
        }
    }

}
