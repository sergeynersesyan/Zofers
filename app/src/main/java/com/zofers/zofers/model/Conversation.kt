package com.zofers.zofers.model

import java.util.*

class Conversation {
	var id: String? = null
	var name: String? = null
	var creationDate: Date? = null
	var lastActionDate: Date? = null
	var participantIDs: List<String>? = null
	var participants: List<Participant>? = null
	var lastMessage: Message? = null

	//_____________________________________________________________________
	fun getUser(id: String): Participant? {
		for (u in participants!!) {
			if (u.id == id) {
				return u
			}
		}
		return null
	}

	fun getParticipantsExcept(userId: String): List<Participant> {
		val returnList: MutableList<Participant> = ArrayList()
		for (p in participants!!) {
			if (p.id != userId) {
				returnList.add(p)
			}
		}
		return returnList
	}

	fun getParticipant(id: String): Participant? {
		for (p in participants!!) {
			if (p.id == id) {
				return p
			}
		}
		return null
	}

	fun removeParticipant(user: Participant) {
		for (p in participants!!) {
			if (p.id == user.id) {
				break
			}
		}
	}

	val isGroup: Boolean
		get() = participants!!.size > 2

	fun isUnread(userId: String): Boolean {
		val participant = getParticipant(userId) ?: return false
		return if (lastMessage != null) {
			userId != lastMessage!!.userId && lastMessage!!.id != participant.lastSeenMessageId
		} else {
			participant.lastSeenMessageId == null
		}
	}

	companion object {
		const val DOC_NAME = "conversation"
	}
}