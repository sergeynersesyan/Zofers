package com.zofers.zofers.vvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zofers.zofers.service.RetrofitProvider
import java.util.*

open class AppViewModel : ViewModel() {
	var state: MutableLiveData<Int>
		protected set
	protected var retrofitProvider: RetrofitProvider
	val auth = FirebaseAuth.getInstance()
	val currentUser
		get() = auth.currentUser

	init {
		state = MutableLiveData()
		state.value = States.NONE
		retrofitProvider = RetrofitProvider.getInstance()
	}

	//todo move to helper class
	protected fun convertToHashMap(offer: Any): HashMap<String, Any>? {
		val gson = Gson()
		val json = gson.toJson(offer)
		val type = object : TypeToken<Map<String, Any>>() {

		}.type
		return gson.fromJson<HashMap<String, Any>>(json, type)
	}
}
