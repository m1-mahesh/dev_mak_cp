package com.nikvay.drnitingroup.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.RootActivity;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

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

//                addNotification(json);
//                handleDataMessage(json);
                showNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }
    private String createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.app_name);
                String id = "mak_it_solutions_channel";
                String description = getString(R.string.app_name);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(id, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                return id;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    private void addNotification(JSONObject jsonObject) {
        try {
            String chanelId = createNotificationChannel();
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, chanelId)
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
    void showNotification(JSONObject jsonObject){
        int icon = R.mipmap.ic_launcher;

        //if message and image url
        int count=0;
        String chanelId = createNotificationChannel();
        Intent intent = new Intent(this, RootActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        try {
            notificationBuilder = new NotificationCompat.Builder(this, chanelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(jsonObject.getString("description"))
                    .setContentText(jsonObject.getString("description"))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setStyle(new NotificationCompat.BigTextStyle().bigText(jsonObject.getString("title")))
                    .setContentIntent(pendingIntent);
            notificationBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int randomNumber = random.nextInt(9999 - 1000) + 1000;
        if (notificationManager!=null && notificationBuilder!=null)
            notificationManager.notify(randomNumber, notificationBuilder.build());

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
