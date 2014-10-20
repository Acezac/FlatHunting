package com.example.AndroidMVC;

import android.content.Context;
import android.content.Intent;
 
// it declares some variables that are used commonly by other classes

public final class CommonUtilities {
     
    // give your server registration url here
     public static final String SERVER_URL = "http://www.androidmvc.netau.net/Controller/"; 
     public static final String SERVER_URL_IMAGES = "http://www.androidmvc.netau.net/";
	// give your localhost registration here
	// public static final String SERVER_URL = "http://31.220.252.132:8080/gcm_server_php/Controller/";
    // public static final String SERVER_URL_IMAGES = "http://31.220.252.132:8080/gcm_server_php/";
    // Google project id
    static final String SENDER_ID = "645335110678"; 
 
    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";
 
    static final String DISPLAY_MESSAGE_ACTION =
            "com.example.AndroidMVC.DISPLAY_MESSAGE";
 
    static final String EXTRA_MESSAGE = "message";
    
    static final String TAG_SUCCESS = "success";
	static final String TAG_EVENTS = "houses";
	static final String TAG_EID = "idHouse";
	static final String TAG_NAME = "name";
	static final String TAG_IMAGE = "image";
	static final String TAG_PRICE = "price";	 
	static final String TAG_AREA = "area";
	static final String TAG_MARKED = "marked";
	static final String TAG_COUPLES= "couples";
	static final String TAG_DATE_POSTED= "datePosted";
	static final String TAG_DATE_AVAILABLE= "dateAvailable";
	static final String TAG_ROOM_TYPE= "roomType";
	static final String TAG_DESCRIPTION= "description";
	static final String TAG_IMG_SIZE = "imgSize";
	static final String TAG_REGID = "regId";
	static final String TAG_TEAMNAME = "teamName";
	static final String TAG_SELLERTYPE = "sellerType";
	static final String TAG_LATITUDE = "latitude";
	static final String TAG_LONGITUDE = "longitude";
    
    
 
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}