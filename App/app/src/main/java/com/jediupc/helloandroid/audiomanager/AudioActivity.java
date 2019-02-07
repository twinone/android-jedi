package com.jediupc.helloandroid.audiomanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import com.jediupc.helloandroid.R;
import com.jediupc.helloandroid.model.ModelContainer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;

import java.io.File;
import java.io.IOException;

public class AudioActivity extends AppCompatActivity {


    private static final long START_DELAY = 500;
    private static final String TAG = "AudioActivity";
    private static final String AUDIOS_DIR = "audios";
    private static final long UPDATE_SEEKBAR_INTERVAL = 80;

    // user is pressing on FAB
    private boolean mFABFingerDown;

    private RecyclerView mRecyclerView;
    private AudioAdapter mAdapter;

    // we have the audio permission
    private boolean mHasPermission;

    private File mDir;
    private File mCurrentPlayingFile;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;

    private FloatingActionButton mFAB;
    private ModelContainer mModel;
    private long mRecordStartTime;
    private File mRecordOutFile;


    private Handler mHandler = new Handler();
    private int mCurrentPlayingPos;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        mModel = ModelContainer.load(this);

        checkPermissions();

        mDir = new File(getFilesDir(), AUDIOS_DIR);
        mDir.mkdirs();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFAB = findViewById(R.id.fab);
        mFAB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mFABFingerDown) return false; // already pressed
                        mFABFingerDown = true;
                        // start recording after START_DELAY millis if not canceled
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startRecording();
                                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                            }
                        }, START_DELAY);
                        break;

                    case MotionEvent.ACTION_UP:
                        if (mRecorder == null) return true;
                        mFABFingerDown = false;
                        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                        stopRecording();
                        break;
                }
                return true;
            }
        });


        mAdapter = new AudioAdapter(mModel.audios, new AudioAdapter.AudioAdapterCallback() {
            @Override
            public void onPlay(View v, int pos) {
                startPlaying(pos);
            }

            @Override
            public void onSeek(View v, int pos, float progress) {
                int msec = Math.round(mModel.audios.get(pos).duration * progress);
                mPlayer.seekTo(msec);
            }
        });
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);
        checkPermissions();


        mHandler.post(mUpdateTimeRunnable);
        enableDragDrop();
    }

    private void checkPermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new BasePermissionListener() {
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        super.onPermissionDenied(response);
                        mHasPermission = false;
                    }

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        super.onPermissionGranted(response);
                        mHasPermission = true;

                    }
                }).check();
    }


    private void startPlaying(int pos) {
        if (mPlayer != null) stopPlaying();

        mCurrentPlayingPos = pos;
        mCurrentPlayingFile = new File(mModel.audios.get(pos).path);

        if (mCurrentPlayingFile == null) return;

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mCurrentPlayingFile.getAbsolutePath());
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if (mPlayer == null) return;
        mPlayer.release();
        mPlayer = null;
        mCurrentPlayingFile = null;
    }

    private void startRecording() {
        if (!mHasPermission) return;
        if (mRecorder != null) stopRecording();

        mRecordStartTime = System.currentTimeMillis();


        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecordOutFile = new File(mDir, "audio-" + mRecordStartTime + ".3gpp");
        mRecorder.setOutputFile(mRecordOutFile.getAbsolutePath());

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        if (mRecorder == null) return;
        mRecorder.stop();
        mRecorder.release();

        mRecorder = null;

        // add recorded media to model
        AudioModel am = new AudioModel();
        am.duration = System.currentTimeMillis() - mRecordStartTime;
        am.path = mRecordOutFile.getAbsolutePath();
        mModel.audios.add(0, am);

        mRecordOutFile = null;

        // logic on what happens next:
        mAdapter.notifyItemInserted(0);
    }


    @Override
    public void onStop() {
        super.onStop();

        stopPlaying();
        stopRecording();
        mModel.save(this);
    }


    private void enableDragDrop() {
        ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder src,
                                  @NonNull RecyclerView.ViewHolder dst) {
                int srcPos = src.getAdapterPosition();
                int dstPos = dst.getAdapterPosition();

                mAdapter.onItemMove(srcPos, dstPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        });
        ith.attachToRecyclerView(mRecyclerView);


    }

    public interface ItemTouchHelperAdapter {
        void onItemMove(int from, int to);

        void onItemDismiss(int pos);
    }


    private Runnable mUpdateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null) {
                AudioAdapter.MyViewHolder vh = (AudioAdapter.MyViewHolder) mRecyclerView.
                        findViewHolderForAdapterPosition(mCurrentPlayingPos);
                vh.update(mPlayer.getDuration(), mPlayer.getCurrentPosition());

            }
            mHandler.postDelayed(this, UPDATE_SEEKBAR_INTERVAL);
        }
    };

}
