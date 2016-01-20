package com.tessoft.common;

public class Constants {

	public static boolean bReal = false;
	
	public static String protocol = "http://";
	
	public static String FAIL = "9999";
	
	public static boolean bPushOffOnNewPost = false;
	
	public static boolean bAdminMode = false;
	
	public static boolean bKakaoLogin = false;
	public static final int HTTP_UPDATE_LOCATION = 20;
	public static final int HTTP_APP_INFO = 1010;
	public static final int HTTP_LOGIN_BACKGROUND2 = 1020;
	public static final int HTTP_GET_RANDOM_ID_FOR_GUEST = 1030;
	public static final int HTTP_LOGOUT = 1040;
	public static final int HTTP_GET_RANDOM_ID_V2 = 1050;
	public static final int HTTP_PROFILE_IMAGE_UPLOAD = 1060;
	public static final int HTTP_UPDATE_FACEBOOK_INFO = 1070;
	public static final int HTTP_GET_NEW_LOCATION = 1080;
	public static final int HTTP_ADD_LOCATION = 1090;
	public static final int HTTP_FINISH_LOCATION_TRACKING = 1100;
	public static final int HTTP_SEARCH_USER_DESTINATIONS = 1110;

	public static final String BROADCAST_REFRESH = "BROADCAST_REFRESH";
	public static final String BROADCAST_TAXI_REFRESH = "BROADCAST_TAXI_REFRESH";
	public static final String BROADCAST_SET_DESTINATION = "BROADCAST_SET_DESTINATION";
	public static final String BROADCAST_LOCATION_UPDATED = "BROADCAST_LOCATION_UPDATED";
	public static final String BROADCAST_STOP_LOCATION_SERVICE = "BROADCAST_STOP_LOCATION_SERVICE";
	public static final String BROADCAST_DESTINATION_REFRESH = "BROADCAST_DESTINATION_REFRESH";

	public static final String METAINFO_LOCATION_MAP_VISIBILITY = "METAINFO_LOCATION_MAP_VISIBILITY";

	public static final int ACTIVITY_REQ_CODE_FB_CONNECT = 3000;

	public static String DAUM_MAP_API_KEY = "1ce23b1035fde7488e6be71df90904d7";
	
	public static String getServerURL()
	{
		return Constants.bReal ? "http://www.hereby.co.kr/nearhere" : "http://192.168.10.110:8080/nearhere";
	}
	
	public static String getServerSSLURL()
	{
		return Constants.bReal ? "https://www.hereby.co.kr/nearhere" : "http://192.168.10.110:8080/nearhere";
	}
	
	public static String getThumbnailImageURL()
	{
		return Constants.bReal ? "http://www.hereby.co.kr/thumbnail/" : "http://192.168.10.110/thumbnail/";
	}
	
	public static String getImageURL()
	{
		return Constants.bReal ? "http://www.hereby.co.kr/image/" : "http://192.168.10.110/image/";
	}
}
