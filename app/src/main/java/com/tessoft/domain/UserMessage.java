package com.tessoft.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMessage {

	private String messageID;
	private User fromUser;
	private User toUser;
	private User user;
	private String message;
	private boolean isRead;
	private boolean isMe;
	
	private String createdDate;
	public User getFromUser() {
		return fromUser;
	}
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}
	public User getToUser() {
		return toUser;
	}
	public void setToUser(User toUser) {
		this.toUser = toUser;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getMessageID() {
		return messageID;
	}
	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public boolean isMe() {
		return isMe;
	}
	public void setMe(boolean isMe) {
		this.isMe = isMe;
	}
	
}
