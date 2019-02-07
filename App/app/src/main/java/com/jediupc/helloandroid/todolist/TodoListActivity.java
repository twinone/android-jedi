package com.jediupc.helloandroid.todolist;

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
import com.jediupc.helloandroid.model.AudioModel;
import com.jediupc.helloandroid.model.ModelContainer;
import com.jediupc.helloandroid.model.TodoListModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.BasePermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TodoListActivity extends AppCompatActivity {


    private static final long START_DELAY = 500;
    private static final String TAG = "TodoListActivity";
    private static final String AUDIOS_DIR = "audios";
    private static final long UPDATE_SEEKBAR_INTERVAL = 50;
    private static final long UPDATE_AMPLITUDE_ANIMATION_INTERVAL = 50;
    private static final float FAB_SCALE = 10;


    private RecyclerView mRecyclerView;
    private TodoListAdapter mAdapter;

    private FloatingActionButton mFAB;
    private ModelContainer mModel;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        mModel = ModelContainer.load(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (mModel.lists.size() == 0) {
            TodoListModel tlm = new TodoListModel();
            tlm.title = "New";
            mModel.lists.add(tlm);
        }

        mFAB = findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModel.lists.get(0).items.add(new TodoListModel.TodoListItemModel());
                mAdapter.notifyItemInserted(mModel.lists.get(0).items.size());
            }
        });


        mAdapter = new TodoListAdapter(mModel.lists.get(0).items, new TodoListAdapter.TodoListCallback() {

        });
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);


        enableDragDrop();
    }


    @Override
    public void onStop() {
        super.onStop();

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


}
