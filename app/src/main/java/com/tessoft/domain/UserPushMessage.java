package com.tessoft.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPushMessage {

	private String pushNo;
	private String toUserID;
	private String message;
	private boolean isRead;
	private String type;
	private String param1;
	private String createdDate;
	public String getToUserID() {
		return toUserID;
	}
	public void setToUserID(String toUserID) {
		this.toUserID = toUserID;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getPushNo() {
		return pushNo;
	}
	public void setPushNo(String pushNo) {
		this.pushNo = pushNo;
	}
}
