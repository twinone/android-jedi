package com.jediupc.helloandroid.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jediupc.helloandroid.R;

import java.util.ArrayList;

import static android.view.View.GONE;

// https://mikescamell.com/shared-element-transitions-part-4-recyclerview/index.html
public class GalleryActivity extends AppCompatActivity {


    public static final String URL = "https://pixabay.com/api/?key=%s&per_page=%d&q=%s";
    public static final String KEY = "11493999-a774b162550f62ff72820dc2e";

    private RecyclerView mRecyclerView;

    RequestQueue queue;
    private GalleryAdapter mAdapter;
    private ActionMode mActionMode;
    private ArrayList<GalleryModel> mDataset;
    private ViewPager mPager;

    // https://guides.codepath.com/android/viewpager-with-fragmentpageradapter
    private MyPagerAdapter mPagerAdapter;
    private FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFAB = findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getVisibility() == View.VISIBLE) {
                    ((GalleryItemFragment) mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem())).send();
                    Log.d("What", mPager.getCurrentItem()+"");
                } else {
                    queryInput();
                }

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

        mPager = findViewById(R.id.viewPager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        getIndex("cat");
        mFAB.hide();
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {
            GalleryModel gm = mDataset.get(i);
            return GalleryItemFragment.newInstance(gm, i);
        }

        @Override
        public int getCount() {
            if (mDataset == null) return 0;
            return mDataset.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "Image Detail" + String.valueOf(position);
        }
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


    @Override
    public void onBackPressed() {
        if (mPager.getVisibility() == View.VISIBLE) {
            mPager.setVisibility(GONE);
            mFAB.hide();
        } else {
            super.onBackPressed();
        }
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
        mDataset = pr.hits;
        mAdapter = new GalleryAdapter(mDataset, new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if (mActionMode != null && mAdapter.getSelectedPositions().size() == 0) {
                    mActionMode.finish();
                    return;
                }

                if (mActionMode == null) {
                    openImageDetails(pos);
                }

            }

            @Override
            public boolean onItemLongClick(View v, int pos) {
                Log.d("Gallery", "OnLongClick");
                startSupportActionMode(mCallback);
                return true;
            }
        });
        Log.d("GalleryAdapter", "Test" + String.valueOf(pr.hits.size()));
        mRecyclerView.setAdapter(mAdapter);
        mPagerAdapter.notifyDataSetChanged();
    }

    private void openImageDetails(int position) {
        mPager.setCurrentItem(position, false);
        mPager.setVisibility(View.VISIBLE);
        mFAB.show();
    }

    private String getQueryURL(String q) {
        Log.d("Gallery", String.format(URL, KEY, 100, q));
        return String.format(URL, KEY, 100, q);
    }


    private ActionMode.Callback mCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.gallery_context, menu);
            mAdapter.setContextMode(true);
            mActionMode = actionMode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.gallery_remove:
                    ArrayList<GalleryModel> tmp = new ArrayList<>();
                    for (int i = 0; i < mDataset.size(); i++) {
                        if (!mAdapter.getSelectedPositions().contains(i)) {
                            tmp.add(mDataset.get(i));
                        }
                    }
                    mDataset.clear();
                    mDataset.addAll(tmp);
                    mActionMode.finish();
                    mAdapter.notifyDataSetChanged();
                    break;

                case R.id.gallery_select_all:
                    if (mAdapter.getSelectedPositions().size() == mDataset.size()) {
                        mAdapter.getSelectedPositions().clear();
                        // don't finish action mode here
                    } else {
                        for (int i = 0; i < mDataset.size(); i++) {
                            mAdapter.getSelectedPositions().add(i);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    break;

            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mAdapter.setContextMode(false);
            mActionMode = null;
        }
    };

}
