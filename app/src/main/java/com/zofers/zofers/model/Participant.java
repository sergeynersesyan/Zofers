package com.zofers.zofers.model;


public class Participant {

	private String id;

	private String name;
	private String avatarUrl;
	private String lastSeenMessageId;
	private String seenMessageDate;

	private boolean isBlocked;

	public String getLastSeenMessageId() {
		return lastSeenMessageId;
	}

	public void setLastSeenMessageId(String lastSeenMessageId) {
		this.lastSeenMessageId = lastSeenMessageId;
	}

	public String getSeenMessageDate() {
		return seenMessageDate;
	}

	public void setSeenMessageDate(String seenMessageDate) {
		this.seenMessageDate = seenMessageDate;
	}

	public void setBlocked(boolean blocked) {
		isBlocked = blocked;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

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

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

}