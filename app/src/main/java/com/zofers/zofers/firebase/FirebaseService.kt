package com.zofers.zofers.firebase

import com.google.android.gms.tasks.Task
import com.google.api.AuthProvider
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.model.*
import java.util.*

class FirebaseService {
	fun updateDocument(collectionName: String, document: String, field: String, value: Any, onCompletionListener: ((Task<Void>) -> Unit)) {
		updateDocument(collectionName, document, mapOf(Pair(field, value)), onCompletionListener)
	}

	fun updateDocument(collectionName: String, document: String, fields: Map<String, Any>, onCompletionListener: ((Task<Void>) -> Unit)) {
		val db = FirebaseFirestore.getInstance()
		db.collection(collectionName)
				.document(document)
				.update(fields)
				.addOnCompleteListener(onCompletionListener)
	}

	fun deleteDocument(collectionName: String, document: String, onCompletionListener: ((Task<Void>) -> Unit)) {
		val db = FirebaseFirestore.getInstance()
		db.collection(collectionName)
				.document(document)
				.delete()
				.addOnCompleteListener(onCompletionListener)
	}

	fun sendMessage(fromId: String, toId: String, text: String, isService: Boolean = false, offerID: String? = null, onCompletionListener: ((Task<Void>) -> Unit)) {
		val messageId = ObjectId.get().toString()
		val convId = generateConversationName(fromId, toId)
		val now = Date()
		val message = Message().apply {
			conversationId = convId
			userId = fromId
			date = now
			this.text = text
			type = if (isService) 1 else 0
			id = messageId
			this.offerID = offerID
		}

		val convRef = FirebaseFirestore.getInstance()
				.document("${Conversation.DOC_NAME}/${convId}")

		convRef.get()
				.addOnSuccessListener { docSnapshot ->
					if (docSnapshot.exists()) {
						convRef.update("lastActionDate", now, "lastMessage", message)
					} else {
						var userFrom: Profile?
						var userTo: Profile?
						getProfile(fromId) {
							userFrom = it // todo nested profile request
							getProfile(toId) {
								userTo = it

								val conversation = Conversation().apply {
									id = convId
									creationDate = now
									lastActionDate = creationDate
									lastMessage = message
									participantIDs = listOf(fromId, toId)
									participants = listOf(
											Participant().apply {
												id = fromId
												avatarUrl = userFrom?.avatarUrl
												name = userFrom?.name
												lastSeenMessageId = messageId
											},
											Participant().apply {
												id = toId
												avatarUrl = userTo?.avatarUrl
												name = userTo?.name
											}
									)
								}
								convRef.set(conversation)
							}
						}

					}
				}

		FirebaseFirestore.getInstance()
				.document("${Conversation.DOC_NAME}/${convId}/${Message.DOC_NAME}/$messageId")
				.set(message)
				.addOnCompleteListener(onCompletionListener)
	}


	fun sendInvitationMessage(fromId: String, toId: String, offerID: String? = null, onCompletionListener: ((Task<Void>) -> Unit)) {
		sendMessage(
				fromId = fromId,
				toId = toId,
				text = "Hi, I am interested in this offer",
				offerID = offerID,
				onCompletionListener = onCompletionListener
		)
	}

	private fun generateConversationName(fromId: String, toId: String): String {
		return if (fromId.compareTo(toId) < 0) fromId + toId else toId + fromId
	}

	fun getProfile(id: String, l: ((Profile?) -> Unit)) {
		FirebaseFirestore.getInstance()
				.collection(Profile.DOC_NAME)
				.document(id)
				.get()
				.addOnSuccessListener {
					l.invoke(
							if (it.exists()) {
								it.toObject(Profile::class.java)
							} else {
								null
							}
					)
				}
	}

	inline fun <reified T> getDocument(collection: String, id: String, crossinline l: ((T?) -> Unit)) {
		FirebaseFirestore.getInstance()
				.collection(collection)
				.document(id)
				.get()
				.addOnSuccessListener {
					l.invoke(
							if (it.exists()) {
								it.toObject(T::class.java)
							} else {
								null
							}
					)
				}
	}

	fun updatePassword(newPassword: String, onCompletionListener: ((Task<Void>) -> Unit)) {
		val user = FirebaseAuth.getInstance().currentUser
//		val credential = GoogleAuthCredential.CREATOR.;
//
//		user?.reauthenticate(credential)?.addOnCompleteListener { task ->
//			if (task.isSuccessful) {
		user?.updatePassword(newPassword)
				?.addOnCompleteListener(onCompletionListener)
//			} else {
//				onCompletionListener.invoke(task)
//			}
//		}
	}


	fun updateEmail(newEmail: String, onCompletionListener: ((Task<Void>) -> Unit)) {
		val user = FirebaseAuth.getInstance().currentUser

		user?.updateEmail(newEmail)
				?.addOnCompleteListener(onCompletionListener)
	}

	fun deleteConversation(fromId: String, toId: String) {
		val convID = generateConversationName(fromId, toId)
		deleteConversation(convID)
	}

	fun deleteConversation(convID: String) {
		val convRef = FirebaseFirestore
				.getInstance()
				.document("${Conversation.DOC_NAME}/${convID}")
		convRef.delete()
	}

	fun updateUserName(name: String, onCompletionListener: ((Task<Void>) -> Unit)) {
		val user = FirebaseAuth.getInstance().currentUser

		val profileUpdates = UserProfileChangeRequest.Builder()
				.setDisplayName(name)
				.build()

		user?.updateProfile(profileUpdates)
				?.addOnCompleteListener(onCompletionListener)
	}

	fun saveDeviceToken(token: String?) {
		val user = FirebaseAuth.getInstance().currentUser
		user?.let {
			updateDocument("profile", FirebaseAuth.getInstance().currentUser!!.uid, "deviceToken", token.orEmpty()) {}
		}
	}

}
