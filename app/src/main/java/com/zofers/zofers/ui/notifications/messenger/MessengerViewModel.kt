package com.zofers.zofers.ui.notifications.messenger

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.zofers.zofers.AppViewModel
import com.zofers.zofers.model.Conversation
import com.zofers.zofers.model.Message

class MessengerViewModel : AppViewModel() {

    val messages = MutableLiveData<MutableList<Message>>()
    lateinit var conversationID: String
    var conversation: MutableLiveData<Conversation?> = MutableLiveData()

    fun init (conversationID: String) {
        this.conversationID = conversationID
        getConversation()
        observeMessages()
    }

    private fun getConversation() {
        FirebaseFirestore.getInstance()
                .collection(Conversation.DOC_NAME)
                .document(conversationID)
                .get()
                .addOnSuccessListener {
                    if (!it.exists()) return@addOnSuccessListener
                    conversation.value = it.toObject(Conversation::class.java)
                }
    }

    private fun observeMessages() {
        val docRef = FirebaseFirestore.getInstance()
                .collection(Conversation.DOC_NAME)
                .document(conversationID)
                .collection(Message.DOC_NAME).orderBy("date", Query.Direction.DESCENDING)

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener // failed
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val messageList = mutableListOf<Message>()
                for (message in snapshot) {
                    messageList.add(message.toObject(Message::class.java))
                }
                messages.value = messageList // todo, pagination
            }
        }
    }
}