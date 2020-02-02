package com.zofers.zofers.messenger.messenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by SoloLearn PC2 on 7/17/2017.
 */

public class Message {
	public static final int TYPE_DEFAULT = 0;
	public static final int TYPE_SERVICE = 1;

	private String localId;

	@SerializedName("id")
	private String realId;

	private String conversationId;
	private int userId;
	private Date date;
	private String text;
	private int type; //default 0 or service 1;
	private int status;

	private boolean isInternal;

	private MessageDetails details;

	public Message() {

	}

	public String getId() {
		return realId == null ? localId : realId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public void setRealId(String realId) {
		this.realId = realId;
	}

	public String getLocalId() {
		return localId;
	}

	public String getRealId() {
		return realId;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isInternal() {
		return isInternal;
	}

	public boolean isUnsent() {
		return isInternal && System.currentTimeMillis() - getDate().getTime() > 1000 * 5;
	}

	public void setInternal(boolean internal) {
		isInternal = internal;
	}

	public MessageDetails getDetails() {
		return details;
	}

	public boolean equalsVisually(Message m) {
		return conversationId.equals(m.conversationId) &&
				text.equals(m.text) &&
				userId == m.userId;
	}
}
