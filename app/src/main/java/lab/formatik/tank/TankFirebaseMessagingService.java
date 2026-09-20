package lab.formatik.tank;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class TankFirebaseMessagingService extends FirebaseMessagingService {
    public static final String PREFS = "formatiklab_tank";
    public static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String CHANNEL_ID = "tank_alarms";

    @Override public void onNewToken(String token) {
        super.onNewToken(token);
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    @Override public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        String title = "FormatikLab Tank";
        String body = "Nuovo avviso serbatoio";
        if (message.getNotification() != null) {
            if (message.getNotification().getTitle() != null) title = message.getNotification().getTitle();
            if (message.getNotification().getBody() != null) body = message.getNotification().getBody();
        }
        String deviceId = message.getData().get("device_id");
        showNotification(title, body, deviceId);
    }

    private void showNotification(String title, String body, String deviceId) {
        NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch=new NotificationChannel(CHANNEL_ID,"Allarmi serbatoio",NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Avvisi livello pellet e sensore offline"); nm.createNotificationChannel(ch);
        }
        Intent i=new Intent(this,MainActivity.class);
        if(deviceId!=null) i.putExtra("device_id",deviceId);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT|(Build.VERSION.SDK_INT>=23?PendingIntent.FLAG_IMMUTABLE:0));
        android.app.Notification.Builder b=Build.VERSION.SDK_INT>=26?new android.app.Notification.Builder(this,CHANNEL_ID):new android.app.Notification.Builder(this);
        b.setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(body).setAutoCancel(true).setContentIntent(pi);
        nm.notify((int)(System.currentTimeMillis() & 0x7fffffff),b.build());
    }
}
