package com.jediupc.helloandroid.gallery;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jediupc.helloandroid.R;

public class GalleryActivity extends AppCompatActivity {


    public static final String URL = "https://pixabay.com/api/?key=%s&per_page=%d&q=%s";
    public static final String KEY = "11493999-a774b162550f62ff72820dc2e";

    private RecyclerView mRecyclerView;

    RequestQueue queue;
    private GalleryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryInput();

            }
        });


        mRecyclerView = findViewById(R.id.recyclerView);

        int cols =
                getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT
                        ? 4
                        : 8;
        GridLayoutManager glm = new GridLayoutManager(this, cols);
        mRecyclerView.setLayoutManager(glm);


        queue = Volley.newRequestQueue(this);

        getIndex("cat");
    }

    private void queryInput() {
        final EditText et = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Query")
                .setView(et)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getIndex(et.getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void getIndex(String q) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getQueryURL(q),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Gallery", response);

                        PixabayResp pr = new Gson()
                                .fromJson(response, PixabayResp.class);
                        onIndexLoaded(pr);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void onIndexLoaded(PixabayResp pr) {
        mAdapter = new GalleryAdapter(pr.hits, new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

            }
        });
        Log.d("GalleryAdapter", "Test" + String.valueOf(pr.hits.size()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private String getQueryURL(String q) {
        Log.d("Gallery", String.format(URL, KEY, 100, q));
        return String.format(URL, KEY, 100, q);
    }


}
