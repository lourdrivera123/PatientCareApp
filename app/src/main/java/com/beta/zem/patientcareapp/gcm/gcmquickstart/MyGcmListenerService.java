/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beta.zem.patientcareapp.gcm.gcmquickstart;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.beta.zem.patientcareapp.Activities.OrderDetailsActivity;
import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.beta.zem.patientcareapp.SwipeTabsModule.MasterTabActivity;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.util.Log.d;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        d("data_bundle", data + "");
        d(TAG, "From: " + from);
        d(TAG, "Message: " + data);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(data);
//        notifysa(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    private void sendNotification(Bundle data) {
        Intent intent;

        if( data.getString("intent").equals("ReferralFragment") ) {
            intent = new Intent(this, MasterTabActivity.class);
            intent.putExtra("selected", 0);
        } else if(data.getString("intent").equals("OrderDetailsActivity")){
            intent = new Intent(this, OrderDetailsActivity.class);
            intent.putExtra("order_id", Integer.parseInt(data.getString("order_id")));
        } else {
            intent = new Intent(this, SidebarActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_app)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//        try {
//            JSONObject json_msg = new JSONObject(data.getString("message"));
//            d("json_obj", json_msg + "");
//            d("json_obj_title", data.getString("title") + "");
//            d("json_obj_intent", data.getString("intent") + "");

            notificationBuilder.setContentTitle(data.getString("title")).setContentText(data.getString("text"));

//        } catch (JSONException e) { e.printStackTrace(); }

//        notificationBuilder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
