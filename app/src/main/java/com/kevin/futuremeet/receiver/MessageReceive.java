package com.kevin.futuremeet.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kevin.futuremeet.R;
import com.kevin.futuremeet.activity.SplashActivity;
import com.kevin.futuremeet.utility.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageReceive extends BroadcastReceiver {

    private static final String TAG_ACTION="com.kevin.futuremeet.action.message.UPDATE_STATUS";
    private static final String TAG_DATA = "com.avos.avoscloud.Data";
    private static final String TYPE_LIKE = "like";
    private static final String TYPE_COMMENT = "comment";

    private static final int NEW_MSG_NOTIFICATION_CODE = 100;

    private Context mContext;

    public MessageReceive() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("mytag", "onReceive: ");
        mContext = context;
        if (intent.getAction().equals(TAG_ACTION)) {
            try {
                JSONObject jsonObject = new JSONObject(intent.getExtras().getString(TAG_DATA));
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Config.PREF_KEY_IF_ANY_NEW_MESSAGE, true);
                // TODO: 2016/4/26 update message icon  badge at the bottom tab(broadcast)
                String type = jsonObject.getString("type");
                int likeNum = preferences.getInt(Config.PREF_KEY_LIKE_NUMBER, 0);
                int commentNum = preferences.getInt(Config.PREF_KEY_COMMENT_NUMBER, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(context.getString(R.string.app_name));

                NotificationManager notificationManagerCompat =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(alarmSound);

                if (TYPE_LIKE.equals(type)) {
                    likeNum++;
                    editor.putInt(Config.PREF_KEY_LIKE_NUMBER, likeNum);
                    editor.apply();
                    // TODO: 2016/4/26 update the message badge at the message fragment
                    builder.setContentText(context.getString(R.string.someone_like_you_moment))
                            .setNumber(likeNum + commentNum);

                    Intent resultIntent = new Intent(context, SplashActivity.class);
                    resultIntent.setAction(Intent.ACTION_MAIN);
                    resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                            resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    builder.setAutoCancel(true);
                    notificationManagerCompat.notify(NEW_MSG_NOTIFICATION_CODE, builder.build());
                } else {
                    // TODO: 2016/4/26 if is a comment type
                }

                sendNewsBroadcast();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendNewsBroadcast() {
        Intent intent = new Intent(Config.INTNET_ACTION_NEWS);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

}
