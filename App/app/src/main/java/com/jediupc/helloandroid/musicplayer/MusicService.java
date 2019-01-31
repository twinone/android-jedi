package com.jediupc.helloandroid.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

public class MusicService extends Service {

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_STOP = "action_stop";

    MediaPlayer mMediaPlayer;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        if (intent.getAction() == null) return START_NOT_STICKY;

        if (intent.getAction().equals(ACTION_PLAY)) {
            if (intent.hasExtra("path")) {
                if (mMediaPlayer != null &&
                        mMediaPlayer.isPlaying()) mMediaPlayer.stop();

                String path = intent.getStringExtra("path");
                Uri uri = Uri.parse(path);
                mMediaPlayer = MediaPlayer.create(this, uri);
                mMediaPlayer.start();
            } else {
                // no new song, play existing
                if (mMediaPlayer != null &&
                        mMediaPlayer.isPlaying()) mMediaPlayer.start();
            }
        }

        if (intent.getAction().equals(ACTION_PAUSE)) {
            if (mMediaPlayer != null &&
                    mMediaPlayer.isPlaying()) mMediaPlayer.pause();
        }

        if (intent.getAction().equals(ACTION_STOP)) {
            if (mMediaPlayer != null &&
                    mMediaPlayer.isPlaying()) mMediaPlayer.stop();
        }


        return super.onStartCommand(intent, flags, startId);
    }

}
