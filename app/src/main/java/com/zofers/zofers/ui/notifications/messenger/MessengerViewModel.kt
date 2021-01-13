package com.zofers.zofers.ui.notifications.messenger

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Conversation
import com.zofers.zofers.model.Message
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Profile
import com.zofers.zofers.staff.States

class MessengerViewModel : AppViewModel() {

	private var lastVisibleItem: DocumentSnapshot? = null
	private var loading = false

	var reachedToEnd: MutableLiveData<Boolean> = MutableLiveData(false)
	val messages = MutableLiveData<MutableList<Message>>()
	lateinit var conversationID: String
	var conversation: MutableLiveData<Conversation?> = MutableLiveData()
	var updateViewEvent: MutableLiveData<Boolean?> = MutableLiveData()
	val opponent
		get() = conversation.value?.getParticipantsExcept(currentUser?.id.orEmpty())?.get(0)
	val isMyConnection
		get() = currentUser?.connections?.contains(opponent?.id) == true


	companion object {
		private const val LIMIT = 20
	}

	fun init(conversationID: String) {
		this.conversationID = conversationID
		getConversation()
		loadMessages()
		observeMessages()
	}

	fun rejectConversation() {
//		firebaseService.updateDocument(Conversation.DOC_NAME, conversationID, "status", Conversation.STATUS_REJECTED) {task ->
//			if (task.isSuccessful) {
//				conversation.value?.status = Conversation.STATUS_REJECTED
//			} else {
//				state.value = States.ERROR
//			}
//		}
		firebaseService.updateDocument(Conversation.DOC_NAME, conversationID, "participantIDs", listOf(opponent?.id)) {
			if (it.isSuccessful) {
				state.value = States.FINISH
			} else {
				state.value = States.ERROR
			}
		}
	}

	fun getOffer(id: String, listener: ((Offer?) -> Unit)) {
		firebaseService.getDocument<Offer>(
				collection = Offer.DOC_NAME,
				id = id
		) {
			listener.invoke(it)
		}
	}

	fun loadMore() {
		if (loading || reachedToEnd.value == true) return
		loadMessages()
	}

	fun updateLastSeenMessage() {
		currentUser?.id?.let { userID ->
			val conver = conversation.value
			val mess = messages.value
			if (conver != null && !mess.isNullOrEmpty() && conver.getParticipant(userID)?.lastSeenMessageID != mess.first().id) {
				conver.getParticipant(userID)?.lastSeenMessageID = mess.first().id
				firebaseService.updateDocument(
						collectionName = "conversation",
						document = conversationID,
						field = "participants",
						value = conver.participants.orEmpty()
				) {}
			}

		}
	}

	private fun getConversation() {
		FirebaseFirestore.getInstance()
				.collection(Conversation.DOC_NAME)
				.document(conversationID)
				.addSnapshotListener { snapshot, e ->
					if (snapshot?.exists() == true) {
						val firstOpen = conversation.value == null
						conversation.value = snapshot.toObject(Conversation::class.java)
						if (firstOpen) updateLastSeenMessage()
					}
				}
	}

	private fun loadMessages() {
		loading = true
		var docRef = FirebaseFirestore.getInstance()
				.collection(Conversation.DOC_NAME)
				.document(conversationID)
				.collection(Message.DOC_NAME).orderBy("date", Query.Direction.DESCENDING)
				.limit(LIMIT + 1L)

		lastVisibleItem?.let {
			docRef = docRef.startAt(it)
		}

		docRef.get().addOnCompleteListener { task ->
			loading = false
			val result = task.result
			if (task.isSuccessful && result != null) {
				val messageList = messages.value ?: mutableListOf()
				for (message in result) {
					messageList.add(message.toObject(Message::class.java))
				}
				if (result.documents.size > LIMIT) {
					messageList.removeAt(messageList.size - 1)
				} else {
					reachedToEnd.value = true
				}
				lastVisibleItem = result.documents.lastOrNull()
				messages.value = messageList
			} else {
				state.value = States.ERROR
			}
		}
	}

	private fun observeMessages() {
		val docRef = FirebaseFirestore.getInstance()
				.collection(Conversation.DOC_NAME)
				.document(conversationID)
				.collection(Message.DOC_NAME).orderBy("date", Query.Direction.DESCENDING)
				.limit(LIMIT.toLong())

		docRef.addSnapshotListener { snapshot, e ->
			if (e != null) {
				return@addSnapshotListener // failed
			}

			if (messages.value.isNullOrEmpty()) return@addSnapshotListener

			if (snapshot != null && !snapshot.isEmpty) {
				val newMessageList = snapshot.documents.map { it.toObject(Message::class.java) }
				val existingNewMessage = messages.value?.get(0)
				val resultList: MutableList<Message> = mutableListOf<Message>()
				for (i in newMessageList.indices) {
					if (newMessageList[i]?.id == existingNewMessage?.id) {
						resultList.addAll(messages.value.orEmpty())
						messages.value = resultList
						break
					}
					newMessageList[i]?.let {
						resultList.add(it)
					}
				}
			}
		}
	}

	fun accept() {
		currentUser?.let { profile ->
			opponent?.id?.let { participantID ->
				profile.connections.add(participantID)

				firebaseService.updateDocument(Profile.DOC_NAME, profile.id, "connections", profile.connections) { task ->
					if (task.isSuccessful) {
						currentUser = profile
						firebaseService.updateDocument(Conversation.DOC_NAME, conversationID, "status", Conversation.STATUS_ACCEPTED) {task ->
							if (task.isSuccessful) {
								conversation.value?.status = Conversation.STATUS_ACCEPTED
								updateViewEvent.postValue(true)
							} else {
								state.value = States.ERROR
							}
						}
					} else {
						profile.connections.remove(participantID)
						state.value = States.ERROR
					}
				}
			}
		}
	}

	fun sendMessage(message: String) {
		firebaseService.sendMessage(
				fromId = currentUser!!.id,
				toId = opponent!!.id!!,
				text = message
		) {}
	}
}