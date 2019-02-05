package com.jediupc.helloandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.jediupc.helloandroid.gallery.GalleryActivity;
import com.jediupc.helloandroid.musicplayer.MusicActivity;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        setTitle(R.string.title_menu);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        final ArrayList<MenuItem> myDataset = new ArrayList<>();
        myDataset.add(new MenuItem("Level", LevelActivity.class));
        myDataset.add(new MenuItem("Navigation", NavActivity.class));
        myDataset.add(new MenuItem("Music Player", MusicActivity.class));
        myDataset.add(new MenuItem("Gallery", GalleryActivity.class));

        mAdapter = new MyAdapter(myDataset, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Log.d("ListActivity", "Click item " + String.valueOf(pos));
                MenuItem mi = myDataset.get(pos);

                Intent i = new Intent(ListActivity.this, mi.cls);
                startActivity(i);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        enableDragDrop();


    }

    private void enableDragDrop() {
        // https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf
        // https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-6a6f0c422efd
        ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
                mAdapter.onItemMove(source.getAdapterPosition(),
                        target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }
        });

        ith.attachToRecyclerView(mRecyclerView);
    }


}
