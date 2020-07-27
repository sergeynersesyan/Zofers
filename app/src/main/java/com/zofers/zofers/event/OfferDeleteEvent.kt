package com.zofers.zofers.event

import com.zofers.zofers.model.Offer

data class OfferDeleteEvent(
		val offer: Offer?
)