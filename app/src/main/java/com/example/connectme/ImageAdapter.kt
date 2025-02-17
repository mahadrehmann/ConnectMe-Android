package com.example.connectme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class ImageAdapter(private val context: Context, private val images: List<Int>) : BaseAdapter() {

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Any = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_image, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(images[position])
        return view
    }
}
