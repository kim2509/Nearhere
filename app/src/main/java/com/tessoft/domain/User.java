package com.tessoft.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.tessoft.common.Util;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends ListItemModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userNo;
	private String userID;
	private String userName;
	private String latitude;
	private String longitude;
	private String distance;
	private String password;
	private String regID;
	private String profileImageURL;
	private String age;
	private String birthday;
	private String jobTitle;
	private String sex;
	private String profilePoint;
	private String mobileNo;
	private String uuid;
	private String address;
	private String kakaoID;
	private String kakaoThumbnailImageURL;
	private String kakaoProfileImageURL;
	private String type;
	private String facebookID;
	private String facebookProfileImageURL;
	private String facebookURL;
	
	private boolean moreFlag;

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRegID() {
		return regID;
	}

	public void setRegID(String regID) {
		this.regID = regID;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getProfileImageURL() {
		return profileImageURL;
	}

	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getProfilePoint() {
		if ( Util.isEmptyString( profilePoint ) )
			return "0";
		
		return profilePoint;
	}

	public void setProfilePoint(String profilePoint) {
		this.profilePoint = profilePoint;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isMoreFlag() {
		return moreFlag;
	}

	public void setMoreFlag(boolean moreFlag) {
		this.moreFlag = moreFlag;
	}

	public String getKakaoID() {
		return kakaoID;
	}

	public void setKakaoID(String kakaoID) {
		this.kakaoID = kakaoID;
	}

	public String getKakaoProfileImageURL() {
		return kakaoProfileImageURL;
	}

	public void setKakaoProfileImageURL(String kakaoProfileImageURL) {
		this.kakaoProfileImageURL = kakaoProfileImageURL;
	}

	public String getKakaoThumbnailImageURL() {
		return kakaoThumbnailImageURL;
	}

	public void setKakaoThumbnailImageURL(String kakaoThumbnailImageURL) {
		this.kakaoThumbnailImageURL = kakaoThumbnailImageURL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFacebookID() {
		return facebookID;
	}

	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}

	public String getFacebookProfileImageURL() {
		return facebookProfileImageURL;
	}

	public void setFacebookProfileImageURL(String facebookProfileImageURL) {
		this.facebookProfileImageURL = facebookProfileImageURL;
	}

	public String getFacebookURL() {
		return facebookURL;
	}

	public void setFacebookURL(String facebookURL) {
		this.facebookURL = facebookURL;
	}
}
