package com.zofers.zofers.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.zofers.zofers.R

class ImageGalleryAdapter : RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>() {
	var items: List<String> = emptyList()
		set(value) {
			field = value
			notifyDataSetChanged()
		}
	var listener: Listener? = null
	var showAddButton = true
	private val offset: Int
	get() = if (showAddButton) 1 else 0

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
		return ViewHolder(view)
	}

	override fun getItemCount(): Int {
		return items.size + offset
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (position > 0) {
			holder.bind(items[position - offset])
		} else {
			holder.bind(null)
		}
	}

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

		private val image: ImageView = view.findViewById(R.id.image)

		fun bind(url: String?) {
			url?.let {
				image.scaleType = ImageView.ScaleType.CENTER_CROP
				image.load(url)
				image.setOnClickListener {
					listener?.onImageCLick(url)
				}
				return
			}

			//if url is null
			image.setOnClickListener {
				listener?.onAddClick()
			}
		}
	}

	interface Listener {
		fun onImageCLick(url: String?)
		fun onAddClick()
	}
}