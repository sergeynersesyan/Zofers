package com.zofers.zofers.model

class Profile {
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
	var interestedOffers: List<String>? = null
	var favoriteOffers: List<String>? = null
	var followers: List<String>? = null

//	Private
//	Private Photos
//	contacts

}


