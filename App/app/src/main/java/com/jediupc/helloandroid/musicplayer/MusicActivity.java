package com.jediupc.helloandroid.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.jediupc.helloandroid.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQ_PERM = 42;
    FloatingActionButton mBPrev;
    FloatingActionButton mBPlay;
    FloatingActionButton mBNext;
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MusicAdapter mAdapter;
    private ArrayList<MusicModel> mMusic;
    private boolean mPlaying;
    private AppCompatSeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mBPrev = findViewById(R.id.bPrev);
        mBPlay = findViewById(R.id.bPlay);
        mBNext = findViewById(R.id.bNext);
        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSeekBar = findViewById(R.id.seekBar);
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Intent i = new Intent(MusicActivity.this, MusicService.class);
                    i.setAction(MusicService.ACTION_SEEK);
                    i.putExtra("position", progress);
                    startService(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mBPrev.setOnClickListener(this);
        mBPlay.setOnClickListener(this);
        mBNext.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPermission();
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQ_PERM);
            }
        } else {
            onPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted();
        }
    }


    private void onPermissionGranted() {
        Log.d("Permission", "Granted");

        Intent i = new Intent(this, MusicService.class);
        i.setAction(MusicService.ACTION_PERMISSION_GRANTED);
        startService(i);
    }

    @Subscribe
    public void onReceiveMusicList(ArrayList<MusicModel> list) {
        mMusic = list;
        mAdapter = new MusicAdapter(mMusic, new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                MusicModel mm = mMusic.get(pos);
                playMusic(mm, pos);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    private void playMusic(MusicModel mm, int pos) {
        Intent i = new Intent(this, MusicService.class);
        i.setAction(MusicService.ACTION_PLAY);
        i.putExtra("pos", pos);
        startService(i);
    }

    @Subscribe
    public void onServiceResult(Boolean playing) {
        mPlaying = playing;
        int id = playing
                ? R.drawable.ic_pause
                : R.drawable.ic_play;
        Drawable d = getResources().getDrawable(id);
        mBPlay.setImageDrawable(d);
    }


    @Subscribe
    public void onTicksEvent(TicksEvent te) {
        mSeekBar.setProgress((int) (100 * te.position / te.duration));
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.bPrev:
                i = new Intent(this, MusicService.class);
                i.setAction(MusicService.ACTION_PREVIOUS);
                startService(i);
                break;
            case R.id.bPlay:

                i = new Intent(this, MusicService.class);
                i.setAction(mPlaying
                        ? MusicService.ACTION_PAUSE
                        : MusicService.ACTION_PLAY);
                startService(i);
                break;
            case R.id.bNext:
                i = new Intent(this, MusicService.class);
                i.setAction(MusicService.ACTION_NEXT);
                startService(i);
                break;
        }
    }
}
