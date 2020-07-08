package com.zofers.zofers.model;

import android.content.Context;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Conversation {
	public static final String DOC_NAME = "conversation";
	private String id;
	private String name;
	private Date creationDate;
	private Date lastActionDate;

	private List<String> participantIDs;
	private List<Participant> participants;

	private Message lastMessage;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastActionDate() {
		return lastActionDate;
	}

	public void setLastActionDate(Date lastActionDate) {
		this.lastActionDate = lastActionDate;
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	public Message getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(Message lastMessage) {
		this.lastMessage = lastMessage;
	}

	public List<String> getParticipantIDs() {
		return participantIDs;
	}

	public void setParticipantIDs(List<String> participantIDs) {
		this.participantIDs = participantIDs;
	}

	//_____________________________________________________________________

	public Participant getUser(String id) {
		for (Participant u : participants) {
			if (u.getId().equals(id)) {
				return u;
			}
		}
		return null;
	}

	public List<Participant> getParticipantsExcept(String userId) {
		List<Participant> returnList = new ArrayList<>();
		for (Participant p : participants) {
			if (!p.getId().equals(userId)) {
				returnList.add(p);
			}
		}
		return returnList;
	}

	public Participant getParticipant(String id) {
		for (Participant p : participants) {
			if (p.getId().equals(id)) {
				return p;
			}
		}
		return null;
	}

	public void addParticipant(Participant participant) {
		if (participants == null) {
			participants = new ArrayList<>();
		}
		participants.add(participant);
	}

	public void removeParticipant(Participant user) {
		for (Participant p : participants) {
			if (p.getId().equals(user.getId())) {
				break;
			}
		}

	}

	public boolean isGroup() {
		return participants.size() > 2;
	}

	public String getDisplayName(String userId, Context context) {

		if (Strings.isNullOrEmpty(name)) {
			return name;
		}
		if (!isGroup()) {
			return getParticipantsExcept(userId).get(0).getName();
		}
		String displayName = "";
		int activeUsersCount = 0;
		Participant me = null;
		for (int i = 0; i < participants.size(); i++) {
			Participant user = participants.get(i);
			if (user.getId().equals(userId)) {
				me = user;
				continue;
			}
			if (activeUsersCount < 3) { // first 3 names
				if (Strings.isNullOrEmpty(displayName)) {
					displayName = user.getName();
				} else {
					displayName += ", " + user.getName();
				}
			}
			activeUsersCount++;
		}
		if (activeUsersCount == 0 && me != null) { // group chat, where only me
			displayName = me.getName();
		} else if (activeUsersCount > 3) {
			displayName += " ..."; //String.format(context.getString(R.string.messenger_group_name_others), (activeUsersCount - 3));
		} else if (activeUsersCount == 0) { // me == null
			displayName = "";
		}
		return displayName;
	}

	public boolean isUnread(String userId) {
		Participant participant = getParticipant(userId);
		if (participant == null) return false;
		if (lastMessage != null) {
			return !userId.equals(lastMessage.getUserId()) && !lastMessage.getId().equals(participant.getLastSeenMessageId());
		} else {
			return participant.getLastSeenMessageId() == null;
		}
	}

}