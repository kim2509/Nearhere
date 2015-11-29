package com.tessoft.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSetting {
	private String userID;
	private String messagePushReceiveYN;
	private String replyPushReceiveYN;
	private String recommendPushReceiveYN;
	private String inquiryUserPushReceiveYN;
	private String newUserPushReceiveYN;
	
	private String createdDate;
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getMessagePushReceiveYN() {
		return messagePushReceiveYN;
	}
	public void setMessagePushReceiveYN(String messagePushReceiveYN) {
		this.messagePushReceiveYN = messagePushReceiveYN;
	}
	public String getReplyPushReceiveYN() {
		return replyPushReceiveYN;
	}
	public void setReplyPushReceiveYN(String replyPushReceiveYN) {
		this.replyPushReceiveYN = replyPushReceiveYN;
	}
	public String getRecommendPushReceiveYN() {
		return recommendPushReceiveYN;
	}
	public void setRecommendPushReceiveYN(String recommendPushReceiveYN) {
		this.recommendPushReceiveYN = recommendPushReceiveYN;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getInquiryUserPushReceiveYN() {
		return inquiryUserPushReceiveYN;
	}
	public void setInquiryUserPushReceiveYN(String inquiryUserPushReceiveYN) {
		this.inquiryUserPushReceiveYN = inquiryUserPushReceiveYN;
	}
	public String getNewUserPushReceiveYN() {
		return newUserPushReceiveYN;
	}
	public void setNewUserPushReceiveYN(String newUserPushReceiveYN) {
		this.newUserPushReceiveYN = newUserPushReceiveYN;
	}
}
