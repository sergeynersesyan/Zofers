package com.zofers.zofers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.zofers.zofers.R
import com.zofers.zofers.model.Offer

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

class OffersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private var items: List<Offer>? = null
	private var listener: Listener? = null
	var showFilters = false
	var itemResId = R.layout.item_offer


	companion object {
		private const val ITEM_TYPE_OFFER = 0
		private const val ITEM_TYPE_FILTERS = 1
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		if (viewType == ITEM_TYPE_FILTERS) {
			val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filters, parent, false)
			return FiltersViewHolder(view)
		}
		val view = LayoutInflater.from(parent.context).inflate(itemResId, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (getItemViewType(position) == ITEM_TYPE_FILTERS) {
			(holder as FiltersViewHolder).bind()
			return
		}

		(holder as ViewHolder).bind(items!![position - if (showFilters) 1 else 0])
		if (position == itemCount - 1) {
			listener?.loadMore()
		}
	}

	override fun getItemCount(): Int {
		return items?.let {
			return it.size + if (showFilters) 1 else 0
		} ?: 0
	}

	fun setItems(items: List<Offer>) {
		this.items = items
		notifyDataSetChanged()
	}

	override fun getItemViewType(position: Int): Int {
		return if (position == 0 && showFilters) {
			ITEM_TYPE_FILTERS
		} else ITEM_TYPE_OFFER
	}

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private val image: ImageView = itemView.findViewById(R.id.image)
		private val city: TextView = itemView.findViewById(R.id.city_textView)
		private val title: TextView = itemView.findViewById(R.id.title_textView)
		private val costs: TextView = itemView.findViewById(R.id.costs_textView)
		private val people: TextView = itemView.findViewById(R.id.people_textView)
		private val peopleImage: ImageView = itemView.findViewById(R.id.people_imageView)
		private val ownerAvatar: ImageView? = itemView.findViewById(R.id.avatar)
		private val ownerName: TextView? = itemView.findViewById(R.id.owner_name)

		fun bind(offer: Offer) {
			image.load(offer.imageURL)
			city.text = offer.city
			title.text = offer.name
			costs.text = offer.getCostText(costs.context)
			if (offer.owner != null) {
				ownerAvatar?.isVisible = true
				ownerName?.isVisible = true
				ownerName?.text = offer.owner!!.name
				ownerAvatar?.load(offer.owner!!.avatarURL) {
					placeholder(R.drawable.ic_avatar)
					fallback(R.drawable.ic_avatar)
					transformations(CircleCropTransformation())
				}
			} else {
				ownerAvatar?.isVisible = false
				ownerName?.isVisible = false
			}

			val peopleCount = offer.peopleCount
			if (peopleCount > 0) {
				val peopleText = people.context.resources.getQuantityString(offer.peopleTextResource, peopleCount, peopleCount)
				people.text = String.format(people.context.getString(R.string.for_someone), peopleText)
				people.visibility = View.VISIBLE
				peopleImage.visibility = View.VISIBLE
			} else {
				people.visibility = View.GONE
				peopleImage.visibility = View.GONE
			}
			itemView.setOnClickListener { view ->
				if (listener != null) {
					listener!!.onItemClick(offer)
				}
			}
		}
	}

	inner class FiltersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		private val filters: TextView

		init {
			filters = itemView.findViewById(R.id.filters_text)
		}

		fun bind() {
			//            filters.requestFocus();
		}
	}

	fun setListener(listener: Listener) {
		this.listener = listener
	}

	interface Listener {
		fun onItemClick(offer: Offer)
		fun loadMore()
	}

}
