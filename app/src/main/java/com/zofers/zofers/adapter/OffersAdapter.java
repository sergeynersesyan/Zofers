package com.zofers.zofers.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zofers.zofers.R;
import com.zofers.zofers.model.Offer;

import java.util.ArrayList;

/**
 * Created by Mr Nersesyan on 26/08/2018.
 */

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder>{
    ArrayList<Offer> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView image;
        private TextView city;
        private TextView title;
        private TextView costs;
        private TextView people;


        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            city = itemView.findViewById(R.id.city_textView);
            title = itemView.findViewById(R.id.title_textView);
            costs = itemView.findViewById(R.id.costs_textView);
            people = itemView.findViewById(R.id.people_textView);
        }

        public void bind (Offer offer) {
            image.setImageURI(offer.getImageUrl());
            city.setText(offer.getCity());
            title.setText(offer.getTitle());
            costs.setText(offer.getCostText(costs.getContext()));
//            people.setText(offer.getPeopleCount());
        }
    }
}
