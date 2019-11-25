package com.zofers.zofers.model

class Profile {
	var id: String = ""
	var userID: String = ""
	var description: String? = null
	var publicImages: List<String>? = null
	var privateImages = mutableListOf<String>()
	var gender: Int? = null
	var orientation: Int? = null
	var height: Int? = null
	var weight: Int? = null
	var age: Int? = null
	var offers: List<Offer>? = null
	var connections: List<String>? = null

//	Private
//	Private Photos
//	contacts

}


