package com.zofers.zofers.model

class Profile {
	companion object {
		const val DOC_NAME = "profile"
	}
	var id: String = ""
	var name: String? = null
	var avatarUrl: String? = null
	var description: String? = null
	var publicImages: List<String>? = null
	var privateImages = mutableListOf<String>()
	var gender: Int? = null
	var orientation: Int? = null
	var height: Int? = null
	var weight: Int? = null
	var age: Int? = null
//	var offers: List<String>? = null
//	var interestedOffers = mutableListOf<String>()
	var favoriteOffers = mutableListOf<String>()
	var connections = mutableListOf<String>()

//	Private
//	Private Photos
//	contacts

}

