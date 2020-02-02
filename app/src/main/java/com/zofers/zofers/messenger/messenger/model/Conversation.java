package com.zofers.zofers.messenger.messenger.model;

import android.content.Context;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by SoloLearn PC2 on 7/17/2017.
 */

public class Conversation {
	private String id;
	private String groupId;
	private String name;
	private Date creationDate;
	private Date lastActionDate;
	private boolean isBlocked;

	private List<Participant> participants;

	private Message lastMessage;

	private int conversationStatus = Participant.STATUS_APPROVED; // we need this for filtering local db


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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

	public void setParticipantStatus(int profileId, int participantStatus) {
		this.conversationStatus = participantStatus;
		getParticipant(profileId).setStatus(participantStatus);
	}

	public void setConversationStatus(int participantStatus) {
		this.conversationStatus = participantStatus;
	}

	public int getConversationStatus() {
		return conversationStatus;
	}

	public void setBlocked(boolean blocked) {
		isBlocked = blocked;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	//_____________________________________________________________________

	public Participant getUser(int id) {
		for (Participant u : participants) {
			if (u.getId() == id) {
				return u;
			}
		}
		return null;
	}

	public List<Participant> getActiveParticipants() {
		List<Participant> returnList = new ArrayList<>();
		for (Participant p : participants) {
			if (p.isActive()) {
				returnList.add(p);
			}
		}
		return returnList;
	}

	public List<Participant> getParticipantsExcept(int userId) {
		List<Participant> returnList = new ArrayList<>();
		if (participants.size() == 2) {
			//ignore isActive() in this case
			returnList.add(participants.get(0).getId() != userId ? participants.get(0) : participants.get(1));
		} else {
			for (Participant p : participants) {
				if (p.getId() != userId && p.isActive()) {
					returnList.add(p);
				}
			}
		}
		return returnList;
	}

	public Participant getParticipant(int id) {
		for (Participant p : participants) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

//    public void addMessage(Message message) {
//        if (messages == null) {
//            messages = new ArrayList<>();
//        }
//        messages.add(message);
//    }

	public void addParticipant(Participant participant) {
		if (participants == null) {
			participants = new ArrayList<>();
		}
		participant.setStatus(Participant.STATUS_APPROVED);
		participants.add(participant);
	}

	public void removeParticipant(Participant user) {
		for (Participant p : participants) {
			if (p.getId() == user.getId()) {
				p.setStatus(Participant.STATUS_REMOVED);
				break;
			}
		}

	}

	public boolean isGroup() {
		return participants.size() > 2;
	}

	public String getDisplayName(int userId, Context context) {

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
			if (!user.isActive()) continue;
			if (user.getId() == userId) {
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

	public boolean isUnread(int userId) {
		Participant participant = getParticipant(userId);
		if (participant == null) return false;
		if (lastMessage != null) {
			return participant.getStatus() != Participant.STATUS_REJECTED && userId != lastMessage.getUserId() && !lastMessage.getId().equals(participant.getLastSeenMessageId());
		} else {
			return participant.getStatus() != Participant.STATUS_REJECTED && participant.getLastSeenMessageId() == null;
		}
	}

	public boolean canRespond(int userId) {
		boolean soloLearnIsTalking = !isGroup() && getParticipantsExcept(userId).get(0).getId() == 1;
		return !isBlocked() && getParticipant(userId).getStatus() != Participant.STATUS_REMOVED && !soloLearnIsTalking;
	}

	public boolean isPending(int profileId) {
		for (Participant p : getParticipants()) {
			if (profileId == p.getId()) {
				if (p.getStatus() == Participant.STATUS_PENDING) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isSameSettings(Conversation conversation) {
		if (conversation == null) return false;
		if (!conversation.id.equals(id)) {
			return false;
		}
		if ((conversation.name == null && name != null) || (conversation.name != null && name == null) || (name != null && !name.equals(conversation.name))) {
			return false;
		}
		if (isBlocked ^ conversation.isBlocked) {
			return false;
		}
		if (participants.size() != conversation.participants.size()) {
			return false;
		}

		for (int i = 0; i < participants.size(); i++) {

			if (participants.get(i).getId() != conversation.participants.get(i).getId()) {
				return false;
			}
			if (participants.get(i).getStatus() != conversation.participants.get(i).getStatus()) {
				return false;
			}
			if (participants.get(i).isBlocked() ^ conversation.participants.get(i).isBlocked()) {
				return false;
			}
		}
		return true;
	}
}