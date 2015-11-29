package com.tessoft.domain;

import java.util.List;

public class MainInfo extends ListItemModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User user = null;
	private List<Post> postList = null;
	private List<User> userList = null;
	private int postCount = 0;

	public List<Post> getPostList() {
		return postList;
	}

	public void setPostList(List<Post> postList) {
		this.postList = postList;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public int getPostCount() {
		return postCount;
	}

	public void setPostCount(int postCount) {
		this.postCount = postCount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
