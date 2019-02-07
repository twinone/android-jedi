package com.jediupc.helloandroid.gallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.jediupc.helloandroid.MyAdapter;
import com.jediupc.helloandroid.R;
import com.jediupc.helloandroid.model.ModelContainer;
import com.jediupc.helloandroid.musicplayer.MusicAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {


    public static final String URL = "https://pixabay.com/api/?key=%s&per_page=%d&q=%s";
    public static final String KEY = "11493999-a774b162550f62ff72820dc2e";
    private static final long ANIM_DURATION = 1500;

    private RecyclerView mRecyclerView;

    private String mQuery = "cat";

    RequestQueue queue;
    private GalleryAdapter mAdapter;
    private ActionMode mActionMode;
    private ArrayList<GalleryModel> mDataset;
    private ViewPager mViewPager;
    private MyPagerAdapter mViewPagerAdapter;

    private AdView mAdView;
    private ModelContainer mModel;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("q", mQuery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        MobileAds.initialize(this, "ca-app-pub-5756278739960648~1971385974");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        Log.d("Ads", "Hello!");

        mModel  = ModelContainer.load(this);
        mModel.print();


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("Ads", "Loaded!");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("Ads", "Failed :(");

            }
        });
        mAdView.loadAd(adRequest);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //queryInput();

                if (mViewPager.getVisibility() == View.VISIBLE) {
                    GalleryItemFragment gif = (GalleryItemFragment) mViewPagerAdapter
                            .getRegisteredFragment(mViewPager.getCurrentItem());

                    gif.send();

                }

            }
        });


        mRecyclerView = findViewById(R.id.recyclerView);
        mViewPager = findViewById(R.id.viewPager);

        int cols =
                getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT
                        ? 4
                        : 8;
        GridLayoutManager glm = new GridLayoutManager(this, cols);
        mRecyclerView.setLayoutManager(glm);


        queue = Volley.newRequestQueue(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("q"))
                savedInstanceState.getString("q");
        }
        getIndex(mQuery);
    }

    @Override
    public void onPause() {
        super.onPause();

        mModel.save(this);
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
        mDataset = pr.hits;

        mAdapter = new GalleryAdapter(mDataset, new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if (mActionMode != null && mAdapter.getSelectedPositions().size() == 0) {
                    mActionMode.finish();
                    return;
                }

                if (mActionMode == null) {
                    mViewPager.setCurrentItem(pos, false);
                    showViewPager(v);
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


        mViewPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
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

            if (menuItem.getItemId() == R.id.gallery_remove) {
                mModel.removed += mAdapter.getSelectedPositions().size();
                List<Integer> positions = new ArrayList<>(mAdapter.getSelectedPositions());
                Collections.sort(positions, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2 - o1;
                    }
                });
                for (int pos : positions) {
                    mDataset.remove(pos);
                    mAdapter.notifyItemRemoved(pos);
                }
                mAdapter.getSelectedPositions().clear();
                mViewPagerAdapter.notifyDataSetChanged();
                mActionMode.finish();
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mAdapter.setContextMode(false);
            mActionMode = null;
        }
    };

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private SparseArray<Fragment> registeredFragments = new SparseArray<>();


        // yet another ugly android hack
        // https://stackoverflow.com/a/7287121
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

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
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getVisibility() == View.VISIBLE) {
            hideViewPager();
        } else {
            super.onBackPressed();
        }
    }

    // https://developer.android.com/training/animation/reveal-or-hide-view#Reveal
    private void showViewPager(View v) {
        mViewPager.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = v.getLeft() + v.getWidth() / 2;
            int cy = v.getTop() + v.getHeight() / 2;
            float smallR = (float) Math.hypot(v.getWidth(), v.getHeight());

            int xmax = Math.max(cx, ((View) v.getParent()).getWidth() - cx);
            int ymax = Math.max(cy, ((View) v.getParent()).getHeight() - cy);

            float finalRadius = (float) Math.hypot(xmax, ymax);

            Animator anim = ViewAnimationUtils.createCircularReveal(mViewPager, cx, cy, smallR / 3, finalRadius);
            anim.setDuration(ANIM_DURATION);
            anim.start();
        }
    }

    private void hideViewPager() {
        final int current = mViewPager.getCurrentItem();
        mRecyclerView.scrollToPosition(current);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                View v = mRecyclerView.getLayoutManager().findViewByPosition(current);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = v.getLeft() + v.getWidth() / 2;
                    int cy = v.getTop() + v.getHeight() / 2;

                    float smallR = (float) Math.hypot(v.getWidth(), v.getHeight());
                    Log.d("Hypot", "cx, w " + cx + " " + mViewPager.getWidth());
                    int xmax = Math.max(cx, mRecyclerView.getWidth() - cx);
                    int ymax = Math.max(cy, mRecyclerView.getHeight() - cy);

                    float finalRadius = (float) Math.hypot(xmax, ymax);

                    Animator anim = ViewAnimationUtils.createCircularReveal(
                            mViewPager, cx, cy, finalRadius, smallR / 3);
                    anim.setDuration(ANIM_DURATION);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mViewPager.setVisibility(View.GONE);
                        }
                    });
                    anim.start();
                } else {
                    mViewPager.setVisibility(View.GONE);
                }
            }
        });

    }
}
