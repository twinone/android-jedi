package com.jediupc.helloandroid.gallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.jediupc.helloandroid.R;

public class GalleryDetailActivity extends AppCompatActivity {

    private ImageView mImage;
    private GalleryModel mModel;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        mModel = (GalleryModel) getIntent().getSerializableExtra("model");
        mImage = findViewById(R.id.imageViewDetail);

        queue = Volley.newRequestQueue(this);
        


        Glide
                .with(this)
                .load(mModel.largeImageURL)
                .into(mImage);
    }
}
