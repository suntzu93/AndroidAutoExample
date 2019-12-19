package com.zalo.androidautoexample;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button btnBasicInlineReply;

    private static final String CHANNEL_ID = "msg_01";
    private static final String CHANNEL_NAME = "Messages";
    private static final String GROUND_ID = "chat";
    private static final String GROUND_NAME = "Chat";
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    public static final int ACTION_REPLY = 11;
    public static final int ACTION_DISMISS = 12;

    private static NotificationManagerCompat sNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBasicInlineReply = (Button) findViewById(R.id.btn_basic_inline_reply);
        btnBasicInlineReply.setOnClickListener(this);

        createNotificationChannel();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_basic_inline_reply:
                showNotification();
                break;
        }
    }

    private void createNotificationChannel() {
        sNotificationManager = NotificationManagerCompat.from(this);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setVibrationPattern(new long[]{0, 200, 0, 400});

        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
        builder.setUsage(AudioAttributes.USAGE_NOTIFICATION);
        notificationChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, builder.build());

        NotificationChannelGroup group = new NotificationChannelGroup(GROUND_ID, GROUND_NAME);
        sNotificationManager.createNotificationChannelGroup(group);
        notificationChannel.setGroup(GROUND_ID);

        sNotificationManager.createNotificationChannel(notificationChannel);
    }


    private void showNotification() {
        NotificationCompat.Builder builder = createNotiBuilder();
        NotificationCompat.Action replyAction = createReplyAction();
        NotificationCompat.Action dismissAction = createDismissAction();
        NotificationCompat.MessagingStyle messagingStyle = createMessageStyle();
        builder.addAction(replyAction);
        builder.addAction(dismissAction);
        builder.setStyle(messagingStyle);
        sNotificationManager.notify(1, builder.build());
    }


    private NotificationCompat.Builder createNotiBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("Hello")
                .setSmallIcon(R.drawable.notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification));
        return builder;
    }

    private NotificationCompat.Action createReplyAction() {
        RemoteInput remoteInputWear = new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel("Reply").build();
        Intent intent = new Intent(this, PopupReplyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ACTION_REPLY, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_reply_icon, "Reply", pendingIntent)
                .setAllowGeneratedReplies(true)
                .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
                .setShowsUserInterface(false)
                .addRemoteInput(remoteInputWear)
                .build();
        return action;
    }

    private NotificationCompat.Action createDismissAction() {
        Intent intent = new Intent(this, DismissReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ACTION_DISMISS, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_reply_icon, "Mark as read", pendingIntent)
                .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
                .setShowsUserInterface(false)
                .build();
        return action;
    }

    private NotificationCompat.MessagingStyle createMessageStyle() {
        IconCompat icon = IconCompat.createWithResource(this, R.drawable.sun_tzu);
        Person person = new Person.Builder()
                .setIcon(icon)
                .setName("Un in").build();
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person);
        messagingStyle.setConversationTitle("Chat");
        messagingStyle.setGroupConversation(false);

        messagingStyle.addMessage("Chao un in", System.currentTimeMillis(), person);

        return messagingStyle;
    }

}
