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
	//todo can be removed if not used by IOS
	var status: Int? = 0 // requested - 0, approved - 1, rejected - 2

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
			userId != lastMessage!!.userID && lastMessage!!.id != participant.lastSeenMessageID
		} else {
			participant.lastSeenMessageID == null
		}
	}

	companion object {
		const val DOC_NAME = "conversation"

		const val STATUS_REQUESTED = 0
		const val STATUS_ACCEPTED = 1
		const val STATUS_REJECTED = 2
	}
}