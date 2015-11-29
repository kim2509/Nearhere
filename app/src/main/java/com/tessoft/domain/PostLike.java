package com.tessoft.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostLike extends ListItemModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String likeID;
	private String postID;
	private String userID;
	private String userName;
	private String createDate;
	public String getLikeID() {
		return likeID;
	}
	public void setLikeID(String likeID) {
		this.likeID = likeID;
	}
	public String getPostID() {
		return postID;
	}
	public void setPostID(String postID) {
		this.postID = postID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
