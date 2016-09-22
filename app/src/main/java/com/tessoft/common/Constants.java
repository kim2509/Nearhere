package com.tessoft.common;

public class Constants {

	public static boolean bReal = true;
	
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
	public static final int HTTP_INSERT_USER_TERMS_AGREEMENT = 1120;
	public static final int HTTP_GET_MAIN_INFO = 1130;

	public static final String BROADCAST_REFRESH = "BROADCAST_REFRESH";
	public static final String BROADCAST_TAXI_REFRESH = "BROADCAST_TAXI_REFRESH";
	public static final String BROADCAST_SET_DESTINATION = "BROADCAST_SET_DESTINATION";
	public static final String BROADCAST_START_LOCATION_UPDATE = "BROADCAST_START_LOCATION_UPDATE";
	public static final String BROADCAST_LOCATION_UPDATED = "BROADCAST_LOCATION_UPDATED";
	public static final String BROADCAST_STOP_LOCATION_SERVICE = "BROADCAST_STOP_LOCATION_SERVICE";
	public static final String BROADCAST_DESTINATION_REFRESH = "BROADCAST_DESTINATION_REFRESH";
	public static final String BROADCAST_OPEN_SEARCH_PAGE = "BROADCAST_OPEN_SEARCH_PAGE";
	public static final String BROADCAST_LOGOUT = "BROADCAST_LOGOUT";
	public static final String BROADCAST_UPDATE_UNREAD_COUNT = "updateUnreadCount";
	public static final String BROADCAST_REFRESH_NOTIFICATION = "BROADCAST_REFRESH_NOTIFICATION";
	public static final String BROADCAST_FRIEND_REQUEST = "BROADCAST_FRIEND_REQUEST";
	public static final String BROADCAST_REFRESH_FRIEND_LIST = "BROADCAST_REFRESH_FRIEND_LIST";
	public static final String BROADCAST_REFRESH_LOCATION_HISTORY = "BROADCAST_REFRESH_LOCATION_HISTORY";
	public static final String BROADCAST_REFRESH_NEWS = "BROADCAST_REFRESH_NEWS";
	public static final String BROADCAST_REFRESH_TRAVEL_INFO = "BROADCAST_REFRESH_TRAVEL_INFO";

	public static final String ACTION_SEARCH_MAP = "ACTION_SEARCH_MAP";
	public static final String ACTION_OPEN_DATE_PICKER = "ACTION_OPEN_DATE_PICKER";
	public static final String ACTION_OPEN_TIME_PICKER = "ACTION_OPEN_TIME_PICKER";
	public static final String ACTION_FINISH_ACTIVITY = "ACTION_FINISH_ACTIVITY";
	public static final String ACTION_SET_NEW_POST_URL = "ACTION_SET_NEW_POST_URL";

	public static final String METAINFO_LOCATION_MAP_VISIBILITY = "METAINFO_LOCATION_MAP_VISIBILITY";

	public static final int STATISTICS_DEBUG = 1234;

	public static final int ACTIVITY_REQ_CODE_FB_CONNECT = 3000;

	public static String DAUM_MAP_API_KEY = "1ce23b1035fde7488e6be71df90904d7";
	
	public static String getServerURL()
	{
		return Constants.bReal ? "http://www.hereby.co.kr/nearhere" : "http://172.30.1.200:8080/nearhere";
//		return Constants.bReal ? "http://www.hereby.co.kr/nearhere" : "http://192.168.43.137:8080/nearhere";
	}

	public static String getServerSSLURL()
	{
		return Constants.bReal ? "https://www.hereby.co.kr/nearhere" : "http://172.30.1.200:8080/nearhere";
//		return Constants.bReal ? "https://www.hereby.co.kr/nearhere" : "http://192.168.43.137:8080/nearhere";
	}

	public static String getThumbnailImageURL()
	{
		return Constants.bReal ? "http://www.hereby.co.kr/thumbnail/" : "http://172.30.1.200/thumbnail/";
//		return Constants.bReal ? "http://www.hereby.co.kr/thumbnail/" : "http://192.168.43.137/thumbnail/";
	}

	public static String getThumbnailImageSSLURL()
	{
		return Constants.bReal ? "https://www.hereby.co.kr/thumbnail/" : "http://172.30.1.200/thumbnail/";
//		return Constants.bReal ? "http://www.hereby.co.kr/thumbnail/" : "http://192.168.43.137/thumbnail/";
	}

	public static String getImageURL()
	{
		return Constants.bReal ? "http://www.hereby.co.kr/image/" : "http://172.30.1.200/image/";
//		return Constants.bReal ? "http://www.hereby.co.kr/image/" : "http://192.168.43.137/image/";
	}
}