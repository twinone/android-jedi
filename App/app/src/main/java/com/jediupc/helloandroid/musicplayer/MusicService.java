package com.jediupc.helloandroid.musicplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.jediupc.helloandroid.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class MusicService extends Service {

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_PREVIOUS = "action_prev";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PERMISSION_GRANTED = "action_permission_granted";
    public static final String ACTION_GET_STATE = "action_get_state";


    private static final String CHANNEL_ID = "default";
    private static final int NOTIFICATION_ID = 42;

    int mPos = -1;

    MediaPlayer mMediaPlayer;
    private ArrayList<MusicModel> mMusic;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        if (intent.getAction() == null) return START_NOT_STICKY;

        if (intent.getAction().equals(ACTION_GET_STATE)) {
            EventBus.getDefault().post(isPlaying());
        }

        if (intent.getAction().equals(ACTION_PREVIOUS)) {
            mPos--;
            if (mPos == -1) mPos = mMusic.size() - 1;
            playAtPosition(mPos);
        }
        if (intent.getAction().equals(ACTION_NEXT)) {
            mPos++;
            if (mPos == mMusic.size()) mPos = 0;
            playAtPosition(mPos);
        }

        if (intent.getAction().equals(ACTION_PERMISSION_GRANTED)) {
            mMusic = getMusic();
        }

        if (intent.getAction().equals(ACTION_PLAY)) {
            if (intent.hasExtra("pos")) {

                mPos = intent.getIntExtra("pos", 0);
                playAtPosition(mPos);

            }
            startMusic();
        }

        if (intent.getAction().equals(ACTION_PAUSE)) {
            pauseMusic();
        }

        if (intent.getAction().equals(ACTION_STOP)) {
            stopMusic();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void playAtPosition(int pos) {
        mPos = pos;

        stopMusic();

        MusicModel mm = mMusic.get(mPos);
        Uri uri = Uri.parse(mm.path);
        mMediaPlayer = MediaPlayer.create(this, uri);
        startMusic();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "default";
            String description = "default";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startMusic() {
        if (!isPlaying()) mMediaPlayer.start();
        updateNotification();
        EventBus.getDefault().post(true);
    }

    private void updateNotification() {
        Intent intent = new Intent(this, MusicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        String actionTitle = isPlaying() ? "Pause" : "Play";
        PendingIntent piAction = getPendingIntent(isPlaying() ? ACTION_PAUSE : ACTION_PLAY);
        // build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MusicPlayer")
                .setContentText(mMusic.get(mPos).name)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setOngoing(isPlaying())
                .addAction(0, "Prev", getPendingIntent(ACTION_PREVIOUS))
                .addAction(0, actionTitle, piAction)
                .addAction(0, "Next", getPendingIntent(ACTION_NEXT));

        // show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define


        if (isPlaying()) {
            startForeground(NOTIFICATION_ID, builder.build());
        } else {
            stopForeground(false);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

    }


    private ArrayList<MusicModel> getMusic() {
        ArrayList<MusicModel> res = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String projection[] = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.DURATION,
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor c = getContentResolver()
                .query(uri, projection, selection,
                        null, null);
        while (c != null && c.moveToNext()) {
            MusicModel mm = new MusicModel();
            mm.path = c.getString(0);
            mm.name = mm.path.substring(mm.path.lastIndexOf("/") + 1);
            mm.duration = c.getLong(1);
            res.add(mm);
        }

        EventBus.getDefault().post(res);

        return res;
    }


    private boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        return PendingIntent.getService(this, 0, intent, 0);
    }

    private void stopMusic() {
        if (isPlaying()) mMediaPlayer.stop();
        updateNotification();
        EventBus.getDefault().post(false);
    }

    private void pauseMusic() {
        if (isPlaying()) mMediaPlayer.pause();
        updateNotification();
        EventBus.getDefault().post(false);
    }

}
