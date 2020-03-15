package com.zofers.zofers.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Conversation

class NotificationsViewModel : AppViewModel() {

	val conversations = MutableLiveData<MutableList<Conversation>>()

	fun init () {
		val docRef = FirebaseFirestore.getInstance()
				.collection(Conversation.DOC_NAME)
				.whereArrayContains("participantIDs", currentUser!!.id)
		docRef.addSnapshotListener { snapshot, e ->
			if (e != null) {
				return@addSnapshotListener // failed
			}

			if (snapshot != null && !snapshot.isEmpty) {
				val convList = mutableListOf<Conversation>()
				for (conv in snapshot) {
					convList.add(conv.toObject(Conversation::class.java))
				}
				conversations.value = convList
			}
		}
	}
}