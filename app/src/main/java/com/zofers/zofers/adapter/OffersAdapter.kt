package com.zofers.zofers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.facebook.drawee.view.SimpleDraweeView
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
		private val ITEM_TYPE_OFFER = 0
		private val ITEM_TYPE_FILTERS = 1
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
	}

	override fun getItemCount(): Int {
		if (items == null) return 0

		return items!!.size + if (showFilters) 1 else 0
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
		private val image: SimpleDraweeView
		private val city: TextView
		private val title: TextView
		private val costs: TextView
		private val people: TextView
		private val peopleImage: ImageView


		init {
			image = itemView.findViewById(R.id.image)
			city = itemView.findViewById(R.id.city_textView)
			title = itemView.findViewById(R.id.title_textView)
			costs = itemView.findViewById(R.id.costs_textView)
			people = itemView.findViewById(R.id.people_textView)
			peopleImage = itemView.findViewById(R.id.people_imageView)
		}

		fun bind(offer: Offer) {
			image.setImageURI(offer.imageUrl)
			city.text = offer.city
			title.text = offer.name
			costs.setText(offer.getCostTextRes(costs.context))

			val peopleCount = offer.peopleCount
			if (peopleCount > 0) {
				val peopleText = people.context.resources.getQuantityString(offer.peopleTextResource, peopleCount, peopleCount)
				people.text = peopleText
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
	}

}
