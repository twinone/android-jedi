package com.jediupc.helloandroid.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jediupc.helloandroid.R;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQ_PERM = 42;
    Button mBPrev;
    Button mBPlay;
    Button mBNext;
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MusicAdapter mAdapter;
    private ArrayList<MusicModel> mMusic;

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


        mBPrev.setOnClickListener(this);
        mBPlay.setOnClickListener(this);
        mBNext.setOnClickListener(this);


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
        return res;
    }

    private void onPermissionGranted() {
        Log.d("Permission", "Granted");

        mMusic = getMusic();
        mAdapter = new MusicAdapter(mMusic, new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                MusicModel mm = mMusic.get(pos);
                playMusic(mm);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void playMusic(MusicModel mm) {
        Intent i = new Intent(this, MusicService.class);
        i.setAction(MusicService.ACTION_PLAY);
        i.putExtra("path", mm.path);
        startService(i);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bPrev:
                break;
            case R.id.bPlay:
                Intent i = new Intent(this, MusicService.class);
                i.setAction(MusicService.ACTION_PAUSE);
                startService(i);
                break;
            case R.id.bNext:
                break;
        }
    }
}
