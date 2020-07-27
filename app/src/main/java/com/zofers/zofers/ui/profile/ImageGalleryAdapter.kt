package com.zofers.zofers.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.zofers.zofers.R

class ImageGalleryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	var items: List<String> = emptyList()
		set(value) {
			field = value
			notifyDataSetChanged()
		}
	var listener: Listener? = null
	var showAddButton = true

	private val offset: Int
		get() = if (showAddButton) 1 else 0

	companion object {
		const val ITEM_TYPE_IMAGE = 0
		const val ITEM_TYPE_ADD = 1
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			ITEM_TYPE_ADD -> {
				val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_add, parent, false)
				AddViewHolder(view)
			}
			else -> {
				val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
				ImageViewHolder(view)
			}
		}
	}

	override fun getItemCount(): Int {
		return items.size + offset
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (getItemViewType(position)) {
			ITEM_TYPE_ADD -> {
				(holder as AddViewHolder).bind()
			}
			else -> {
				(holder as ImageViewHolder).bind(items[position - offset])
			}
		}
	}

	inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

		private val image: ImageView = view.findViewById(R.id.image)

		fun bind(url: String?) {
			url?.let {
				image.scaleType = ImageView.ScaleType.CENTER_CROP
				image.load(url)
				image.setOnClickListener {
					listener?.onImageCLick(url)
				}
				image.setOnLongClickListener {
					listener?.onImageLongClick(url)
					true
				}
			}
		}
	}

	inner class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		private val textView: TextView = view.findViewById(R.id.text)

		fun bind() {
			textView.setOnClickListener {
				listener?.onAddClick()
			}
		}
	}

	override fun getItemViewType(position: Int): Int {
		return if (showAddButton && position == 0) {
			ITEM_TYPE_ADD
		} else ITEM_TYPE_IMAGE
	}

	interface Listener {
		fun onImageCLick(url: String?)
		fun onImageLongClick(url: String?)
		fun onAddClick()
	}
}