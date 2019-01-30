package com.jediupc.helloandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        setTitle(R.string.title_menu);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        final ArrayList<MenuItem> myDataset = new ArrayList<>();
        myDataset.add(new MenuItem("Level", LevelActivity.class));
        myDataset.add(new MenuItem("Main", MainActivity.class));

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


    }
}
