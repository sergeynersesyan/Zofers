package com.zofers.zofers.ui.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Conversation
import com.zofers.zofers.staff.States

class NotificationsViewModel : AppViewModel() {

	val conversations = MutableLiveData<MutableList<Conversation>>()

	fun requestMessages() {
		state.value = States.LOADING
		val docRef = FirebaseFirestore.getInstance()
				.collection(Conversation.DOC_NAME)
				.orderBy("lastActionDate", Query.Direction.DESCENDING)
				.whereArrayContains("participantIDs", currentUser!!.id)

		docRef.addSnapshotListener { snapshot, exception ->
			if (exception != null) {
				state.value = States.ERROR
				return@addSnapshotListener // failed
			}

			if (snapshot != null && !snapshot.isEmpty) {
				val convList = mutableListOf<Conversation>()
				for (conv in snapshot) {
					convList.add(conv.toObject(Conversation::class.java))
				}
				conversations.value = convList
			}
			state.value = States.NONE
		}
	}
}