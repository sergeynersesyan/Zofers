package com.zofers.zofers.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zofers.zofers.R;
import com.zofers.zofers.model.Offer;

import java.util.List;

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

public class OffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int ITEM_TYPE_FILTERS = 1;

	private List<Offer> items;
	private Listener listener;

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == ITEM_TYPE_FILTERS) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filters, parent, false);
			return new FiltersViewHolder(view);
		}
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (getItemViewType(position) == ITEM_TYPE_FILTERS) {
			((FiltersViewHolder) holder).bind();
			return;
		}

		((ViewHolder) holder).bind(items.get(position - 1));
	}

	@Override
	public int getItemCount() {
		return items == null ? 0 : 1 + items.size();
	}

	public void setItems(List<Offer> items) {
		this.items = items;
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return ITEM_TYPE_FILTERS;
		}
		return super.getItemViewType(position);
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private SimpleDraweeView image;
		private TextView city;
		private TextView title;
		private TextView costs;
		private TextView people;
		private ImageView peopleImage;


		public ViewHolder(View itemView) {
			super(itemView);
			image = itemView.findViewById(R.id.image);
			city = itemView.findViewById(R.id.city_textView);
			title = itemView.findViewById(R.id.title_textView);
			costs = itemView.findViewById(R.id.costs_textView);
			people = itemView.findViewById(R.id.people_textView);
			peopleImage = itemView.findViewById(R.id.people_imageView);
		}

		public void bind(Offer offer) {
			image.setImageURI(offer.getImageUrl());
			city.setText(offer.getCity());
			title.setText(offer.getName());
			costs.setText(offer.getCostTextRes(costs.getContext()));

			int peopleCount = offer.getPeopleCount();
			if (peopleCount > 0) {
				String peopleText = people.getContext().getResources().getQuantityString(offer.getPeopleTextResource(), peopleCount, peopleCount);
				people.setText(peopleText);
				people.setVisibility(View.VISIBLE);
				peopleImage.setVisibility(View.VISIBLE);
			} else {
				people.setVisibility(View.GONE);
				peopleImage.setVisibility(View.GONE);
			}
			itemView.setOnClickListener((view) -> {
				if (listener != null) {
					listener.onItemClick(offer);
				}
			});
		}
	}

	public class FiltersViewHolder extends RecyclerView.ViewHolder {

		private TextView filters;

		public FiltersViewHolder(@NonNull View itemView) {
			super(itemView);
			filters = itemView.findViewById(R.id.filters_text);
		}

		public void bind() {
//            filters.requestFocus();
		}
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public interface Listener {
		void onItemClick(Offer offer);
	}
}
