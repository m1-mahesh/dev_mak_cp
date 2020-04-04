package com.nikvay.drnitingroup.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.RootActivity;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONObject;

import java.util.Map;

import static com.nikvay.drnitingroup.AppController.TAG;

public class FirebaseMessageService extends FirebaseMessagingService {

    UserSession userSession;
    SharedPreferences sharedPreferences;
    AppSingleTone appSingleTone;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject();
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    Log.e(entry.getKey() + "/" + entry.getValue(), "");
                    json.put(entry.getKey(), entry.getValue());
                }

                addNotification(json);
//                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }
    private void addNotification(JSONObject jsonObject) {
        try {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, "")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(jsonObject.getString("title"))
                            .setContentText(jsonObject.getString("description"));

            builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

            //LED
            builder.setLights(Color.RED, 3000, 3000);
            Intent notificationIntent = new Intent(this, RootActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);

            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        Log.e("new Token: ", s);
        userSession.setAttribute("firebaseToken", s);
    }
}
