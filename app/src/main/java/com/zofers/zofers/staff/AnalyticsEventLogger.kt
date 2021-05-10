package com.zofers.zofers.staff

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.zofers.zofers.model.Offer

object AnalyticsEventLogger {

	private const val SEARCH_OFFER_EVENT_NAME = "search_offer"
	private const val OPEN_OFFER_EVENT_NAME = "open_offer"
	private const val INTERESTED_EVENT_NAME = "click_interested"
	private const val ACCEPT_REJECT_EVENT_NAME = "accept_reject_conversation"
	private const val CREATE_OFFER_BUTTON_CLICK_EVENT_NAME = "click_create_offer_button"
	private const val CREATE_OFFER_EVENT_NAME = "create_offer"
	private const val DELETE_OFFER_EVENT_NAME = "delete_offer"

	private var firebaseAnalytics: FirebaseAnalytics? = null
	private var userManager: UserManager? = null

	fun init(context: Context, userManager: UserManager) {
		firebaseAnalytics = FirebaseAnalytics.getInstance(context)
		this.userManager = userManager
	}

	fun logSearchEvent (text: String) {
		firebaseAnalytics?.logEvent(SEARCH_OFFER_EVENT_NAME, getUserInfoBundle().apply {
			putString("search_text", text)
		})
	}

	fun logOpenOfferEvent (offer: Offer, page: String) {
		firebaseAnalytics?.logEvent(OPEN_OFFER_EVENT_NAME, getUserInfoBundle().apply {
			putString("offer_name", offer.name)
			putString("from_page", page)
			putString("is_own", (userManager?.userProfile?.id == offer.userID).toString())
		})
	}

	fun logInterestedButtonClickEvent (offerName: String, isInterested: Boolean) {
		firebaseAnalytics?.logEvent(INTERESTED_EVENT_NAME, getUserInfoBundle().apply {
			putString("offer_name", offerName)
			putString("is_interested", isInterested.toString())
		})
	}

	fun logConversationAcceptDeleteEvent (isAccepted: Boolean) {
		firebaseAnalytics?.logEvent(ACCEPT_REJECT_EVENT_NAME, getUserInfoBundle().apply {
			putString("action", if (isAccepted) "accepted" else "rejected")
		})
	}

	fun logCreateOfferButtonClickEvent (page: String) {
		firebaseAnalytics?.logEvent(CREATE_OFFER_BUTTON_CLICK_EVENT_NAME, getUserInfoBundle().apply {
			putString("from_page", page)
		})
	}

	fun logCreateOfferEvent (offerName: String) {
		firebaseAnalytics?.logEvent(CREATE_OFFER_EVENT_NAME, getUserInfoBundle().apply {
			putString("offer_name", offerName)
		})
	}

	fun logDeleteOfferEvent (offerName: String) {
		firebaseAnalytics?.logEvent(DELETE_OFFER_EVENT_NAME, getUserInfoBundle().apply {
			putString("offer_name", offerName)
		})
	}

	private fun getUserInfoBundle(): Bundle {
		return Bundle().apply {
			putString("user_id", userManager?.userProfile?.id)
			putString("user_name", userManager?.userProfile?.name)
		}
	}
}