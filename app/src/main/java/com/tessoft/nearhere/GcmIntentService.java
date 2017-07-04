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
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tessoft.common.Constants;
import com.tessoft.nearhere.activities.EventViewerActivity;
import com.tessoft.nearhere.activities.MainActivity;
import com.tessoft.nearhere.activities.PopupWebViewActivity;
import com.tessoft.nearhere.activities.TaxiPostDetailActivity;
import com.tessoft.nearhere.activities.UserMessageActivity;
import com.tessoft.nearhere.activities.UserProfileActivity;

import java.util.HashMap;
import java.util.List;

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

    @Override
    protected void onHandleIntent(Intent intent) {

        application = (NearhereApplication) getApplication();

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
        	if ( "message".equals( type ) )
        	{
                intent = new Intent("updateUnreadCount");
                intent.putExtra("type", type );
            	intent.putExtra("fromUserID", extras.getString("fromUserID") );
            	
            	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
                
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
        	}
            else if ( "postReply".equals( type ) )
            {
                intent = new Intent("updateUnreadCount");
                intent.putExtra("type", type );
            	intent.putExtra("postID", extras.getString("postID") );
            	
            	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
                
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }
            else if ( "friendRequest".equals( type ))
            {
                intent = new Intent("updateUnreadCount");
                intent.putExtra("type", type );

                if ( "on".equals( extras.getString("sound") ) )
                {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                }
                if ( "on".equals( extras.getString("vibrate") ) )
                {
                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
            }
            else if ("acceptFriendRequest".equals(type))
            {
                intent = new Intent("showToastMessage");
                intent.putExtra("msg", "친구 요청이 수락되었습니다." );
            }
            else if ("askLocationRequest".equals(type) )
            {
                intent = new Intent("showToastMessage");
                intent.putExtra("msg", msg );
            }
            else if ( "webView".equals( type ))
            {
                intent = new Intent("updateUnreadCount");
            }
            else
            {
            	return;
            }

            if ( intent != null )
            {
                getApplicationContext().sendBroadcast(intent);
            }
        }
        else
        {
            if ( "message".equals( type ) )
            {
            	intent = new Intent(this, UserMessageActivity.class);
                HashMap hash = new HashMap();
                hash.put("fromUserID", extras.getString("fromUserID"));
                hash.put("userID", extras.getString("toUserID"));
    			intent.putExtra("messageInfo", hash);
                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                mBuilder.setVibrate(new long[]{1000, 1000});
            }
            else if ( "postReply".equals( type ) )
            {
            	intent = new Intent(this, TaxiPostDetailActivity.class);
            	intent.putExtra("postID", extras.getString("postID"));
                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                mBuilder.setVibrate(new long[]{1000, 1000});
            }
            else if ( "newPostByDistance".equals( type ))
            {
            	intent = new Intent(this, TaxiPostDetailActivity.class);
                intent.putExtra("postID", extras.getString("postID") );

                Intent broadcastIntent = new Intent( Constants.BROADCAST_TAXI_REFRESH );
                getApplicationContext().sendBroadcast(broadcastIntent);
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
            else if ( "friendRequest".equals( type ))
            {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("tab", "friend");

                if ( extras.containsKey("sound") && "on".equals( extras.getString("sound") ) )
                    mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                if ( extras.containsKey("vibrate") && "on".equals( extras.getString("vibrate") ) )
                    mBuilder.setVibrate(new long[] { 1000, 1000 });

            }
            else if ( "acceptFriendRequest".equals( type ))
            {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("tab", "friend");

                if ( extras.containsKey("sound") && "on".equals( extras.getString("sound") ) )
                    mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                if ( extras.containsKey("vibrate") && "on".equals( extras.getString("vibrate") ) )
                    mBuilder.setVibrate(new long[] { 1000, 1000 });

            }
            else if ( "webView".equals( type ))
            {
                intent = new Intent(this, PopupWebViewActivity.class);
                intent.putExtra("url", extras.getString("url"));
                intent.putExtra("title", extras.getString("param"));

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
