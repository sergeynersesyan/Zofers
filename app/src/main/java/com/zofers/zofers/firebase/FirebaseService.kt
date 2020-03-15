package com.zofers.zofers.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.zofers.zofers.model.*
import java.util.*

class FirebaseService {
    fun updateDocument(collectionName: String, document: String, field: String, value: Any, onCompletionListener: ((Task<Void>) -> Unit)) {
        val db = FirebaseFirestore.getInstance()
        db.collection(collectionName)
                .document(document)
                .update(field, value)
                .addOnCompleteListener(onCompletionListener)
    }

    fun sendMessage(fromId: String, toId: String, text: String, isService: Boolean = false, onCompletionListener: ((Task<Void>) -> Unit)) {
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
        }

        val convRef = FirebaseFirestore.getInstance()
                .document("${Conversation.DOC_NAME}/${convId}")

        convRef.get()
                .addOnSuccessListener { docSnapshot ->
                    if (!docSnapshot.exists()) {
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


    fun sendInvitationMessage(fromId: String, toId: String, onCompletionListener: ((Task<Void>) -> Unit)) {
        sendMessage(fromId, toId, "Hi, I am interested in this offer", onCompletionListener = onCompletionListener)
    }

    private fun generateConversationName(fromId: String, toId: String): String {
        return if (fromId.compareTo(toId) < 0) fromId + toId else toId + fromId
    }

    fun getProfile(id: String, l: ((Profile?) -> Unit)) {
        FirebaseFirestore.getInstance()
                .collection("profile")
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

    fun deleteConversation(fromId: String, toId: String?) {
    }
}
