package com.zofers.zofers.messenger.messenger.model;


public class Participant {

	public static final int STATUS_PENDING = 0;
	public static final int STATUS_APPROVED = 1;
	public static final int STATUS_REJECTED = 2;
	public static final int STATUS_REMOVED = 3;
	public static final int STATUS_LEFT = 4;


	private int id;
	private int actionUserId;

	private String name;
	private String avatarUrl;
	private String lastSeenMessageId;
	private String deletedMessageId;
	private String seenMessageDate;
	private String joinDate;

	private int status;
	private int rights;

	private String badge;
	private boolean isBlocked;

	public int getActionUserId() {
		return actionUserId;
	}

	public void setActionUserId(int actionUserId) {
		this.actionUserId = actionUserId;
	}

	public String getLastSeenMessageId() {
		return lastSeenMessageId;
	}

	public void setLastSeenMessageId(String lastSeenMessageId) {
		this.lastSeenMessageId = lastSeenMessageId;
	}

	public String getDeletedMessageId() {
		return deletedMessageId;
	}

	public void setDeletedMessageId(String deletedMessageId) {
		this.deletedMessageId = deletedMessageId;
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

	public String getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRights() {
		return rights;
	}

	public void setRights(int rights) {
		this.rights = rights;
	}

	public boolean isActive() {
		return status == Participant.STATUS_APPROVED || status == Participant.STATUS_PENDING;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}
}