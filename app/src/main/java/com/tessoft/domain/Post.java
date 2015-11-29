package com.tessoft.domain;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post extends ListItemModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String postID;
	protected String latitude;
	protected String longitude;
	protected String toAddress;
	protected String message;
	protected String fromLatitude;
	protected String fromLongitude;
	protected String fromAddress;
	protected String content;
	protected Object tag;
	protected User user;
	protected String createdDate;
	protected String reward;
	private String sexInfo;
	private String numOfUsers;
	private String departureDate;
	private String departureTime;
	private String departureDateTime;
	private String fromDistance;
	private String toDistance;
	private String status;
	protected String type="taxi";
	private String vehicle;
	private String fareOption;
	
	private String repetitiveYN = "";
	
	private boolean moreFlag;
	
	private boolean bPushOff;
	
	protected List<PostLike> postLikes;
	protected List<PostReply> postReplies;
	
	private int readCount = 0;
	private int replyCount = 0;
	
	public Post()
	{
		setItemType("POST");
	}
	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPostID() {
		return postID;
	}
	public void setPostID(String postID) {
		this.postID = postID;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createDate) {
		this.createdDate = createDate;
	}

	public List<PostLike> getPostLikes() {
		return postLikes;
	}
	public void setPostLikes(List<PostLike> postLikes) {
		this.postLikes = postLikes;
	}
	public List<PostReply> getPostReplies() {
		return postReplies;
	}
	public void setPostReplies(List<PostReply> postReplies) {
		this.postReplies = postReplies;
	}
	/*
	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}
	*/

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFromLatitude() {
		return fromLatitude;
	}

	public void setFromLatitude(String fromLatitude) {
		this.fromLatitude = fromLatitude;
	}

	public String getFromLongitude() {
		return fromLongitude;
	}

	public void setFromLongitude(String fromLongitude) {
		this.fromLongitude = fromLongitude;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getSexInfo() {
		return sexInfo;
	}

	public void setSexInfo(String sexInfo) {
		this.sexInfo = sexInfo;
	}

	public String getNumOfUsers() {
		return numOfUsers;
	}

	public void setNumOfUsers(String numOfUsers) {
		this.numOfUsers = numOfUsers;
	}

	public String getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getFromDistance() {
		return fromDistance;
	}

	public void setFromDistance(String fromDistance) {
		this.fromDistance = fromDistance;
	}

	public String getToDistance() {
		return toDistance;
	}

	public void setToDistance(String toDistance) {
		this.toDistance = toDistance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public boolean isMoreFlag() {
		return moreFlag;
	}

	public void setMoreFlag(boolean moreFlag) {
		this.moreFlag = moreFlag;
	}

	public String getDepartureDateTime() {
		return departureDateTime;
	}

	public void setDepartureDateTime(String departureDateTime) {
		this.departureDateTime = departureDateTime;
	}

	public boolean isbPushOff() {
		return bPushOff;
	}

	public void setbPushOff(boolean bPushOff) {
		this.bPushOff = bPushOff;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public String getFareOption() {
		return fareOption;
	}

	public void setFareOption(String fareOption) {
		this.fareOption = fareOption;
	}

	public String getRepetitiveYN() {
		return repetitiveYN;
	}

	public void setRepetitiveYN(String repetitiveYN) {
		this.repetitiveYN = repetitiveYN;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
}
