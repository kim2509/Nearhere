/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tessoft.nearhere;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.domain.User;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    NearhereApplication application = null;

    public void checkIfAdminUser()
    {
        try
        {
            File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            //Get the text file
            File file = new File(sdcard,"nearhere.txt");

            if ( !file.exists() ) return;

            //Read text from file
            StringBuilder text = new StringBuilder();

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            String loginInfo = text.toString();

            if ( Util.isEmptyString(loginInfo) ) return;

            String[] tokens = loginInfo.split("\\;");

            String userNo = "";
            String userID = "";
            String pw = "";
            String pushOffOnNewPost = "";
            String server = "";

            for ( int i = 0; i < tokens.length; i++ )
            {
                String key = tokens[i].split("\\=")[0];
                String value = tokens[i].split("\\=")[1];
                if ( "userNo".equals( key ) )
                    userNo = value;
                else if ( "userID".equals( key ) )
                    userID = value;
                else if ( "pw".equals( key ) )
                    pw = value;
                else if ( "pushOffOnNewPost".equals( key ) )
                    pushOffOnNewPost = value;
                else if ( "server".equals( key ) )
                    server = value.trim();
            }

            if (!"이근처합승".equals(pw.trim()))
            {
                Constants.bAdminMode = false;
                return;
            }

            if ( "Y".equals( pushOffOnNewPost.trim() ) ) Constants.bPushOffOnNewPost = true;
            else Constants.bPushOffOnNewPost = false;

            if ( "REAL".equals( server ) )
                Constants.bReal = true;
            else if ( "DEV".equals( server ) )
                Constants.bReal = false;

            Constants.bAdminMode = true;

            User user = application.getLoginUser();
            user.setUserNo(userNo);
            user.setUserID(userID);
            application.setLoginUser(user);

            application.setMetaInfo("registerUserFinished", "true");
            application.setMetaInfo("logout", "false");
        }
        catch( Exception ex )
        {

        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        application = (NearhereApplication) getApplication();

        // 어드민 설정을 읽는다.
        checkIfAdminUser();

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification( extras );
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification( extras );
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
            	
            	/*
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                
                */
            	
                sendNotification( extras );
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification( Bundle extras ) {
    	
    	String title = extras.getString("title");
    	String type = extras.getString("type");
    	String msg = extras.getString("message");
    	 
    	if ( "locationUpdate".equals( type ) )
    	{
    		try
        	{
        		Intent locUpdateIntent = new Intent( getApplicationContext(), LocationUpdateService.class);
            	startService(locUpdateIntent);	
        	}
        	catch( Exception ex )
        	{
        		Log.e("error", ex.getMessage());
        	}
    		return;
    	}
    	
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle( title )
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg)
        .setAutoCancel(true);
        
        Intent intent = null;
        
        boolean isActive = false;
        
        // check if background
//        ActivityManager activityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
//        List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
//        for(int i = 0; i < procInfos.size(); i++)
//        {
//            if(procInfos.get(i).processName.equals("com.com.tessoft.nearhere"))
//	            isActive = true;
//        }
        
        // check if foreground
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase( getApplicationContext().getPackageName().toString())) {
        	isActive = true;
        }

        if ( isActive )
        {
        	intent = new Intent("updateUnreadCount");
        	intent.putExtra("type", type );
        	if ( "message".equals( type ) )
        	{
            	intent.putExtra("fromUserID", extras.getString("fromUserID") );
            	
            	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
                
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
        	}
            else if ( "postReply".equals( type ) )
            {
            	intent.putExtra("postID", extras.getString("postID") );
            	
            	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
                
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
            else
            {
            	return;
            }
        	
            getApplicationContext().sendBroadcast(intent);
        }
        else
        {
            if ( "message".equals( type ) )
            {
            	intent = new Intent(this, UserMessageActivity.class);
            	HashMap hash = new HashMap();
    			hash.put("fromUserID",  extras.getString("fromUserID") );
    			hash.put("userID",  extras.getString("toUserID") );
    			intent.putExtra("messageInfo", hash);
            	mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                mBuilder.setVibrate(new long[] { 1000, 1000 });
            }
            else if ( "postReply".equals( type ) )
            {
            	intent = new Intent(this, TaxiPostDetailActivity.class);
            	intent.putExtra("postID", extras.getString("postID") );
            	mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                mBuilder.setVibrate(new long[] { 1000, 1000 });
            }
            else if ( "newPostByDistance".equals( type ))
            {
            	intent = new Intent(this, TaxiPostDetailActivity.class);
            	intent.putExtra("postID", extras.getString("postID") );
            }
            else if ( "event".equals( type ))
            {
            	intent = new Intent(this, EventViewerActivity.class);
            	intent.putExtra("eventSeq", extras.getString("eventSeq") );
            	intent.putExtra("pushNo", extras.getString("pushNo") );
            	
            	if ( "on".equals( extras.getString("sound") ) )
            		mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            	if ( "on".equals( extras.getString("vibrate") ) )
            		mBuilder.setVibrate(new long[] { 1000, 1000 });
            	
            }
            else if ( "eventssl".equals( type ))
            {
            	intent = new Intent(this, EventViewerActivity.class);
            	intent.putExtra("eventSeq", extras.getString("eventSeq") );
            	intent.putExtra("pushNo", extras.getString("pushNo") );
            	intent.putExtra("ssl", "true" );
            	
            	if ( "on".equals( extras.getString("sound") ) )
            		mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            	if ( "on".equals( extras.getString("vibrate") ) )
            		mBuilder.setVibrate(new long[] { 1000, 1000 });
            	
            }
            else if ( "inquiryUser".equals( type ))
            {
            	intent = new Intent(this, UserProfileActivity.class);
            	intent.putExtra("userID", extras.getString("userID") );
            	
            	if ( extras.containsKey("sound") && "on".equals( extras.getString("sound") ) )
            		mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            	if ( extras.containsKey("vibrate") && "on".equals( extras.getString("vibrate") ) )
            		mBuilder.setVibrate(new long[] { 1000, 1000 });
            	
            }
            else
            {
            	return;
            }
            
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                   intent , PendingIntent.FLAG_UPDATE_CURRENT);
            
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }
}
