package com.jediupc.helloandroid.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jediupc.helloandroid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


// https://stackoverflow.com/a/50265329
public class GalleryDetailActivity extends AppCompatActivity {

    private ImageView mImage;
    private GalleryModel mModel;
    private RequestQueue queue;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        mModel = (GalleryModel) getIntent().getSerializableExtra("model");
        mImage = findViewById(R.id.imageViewDetail);

        queue = Volley.newRequestQueue(this);


        Glide.with(this).asBitmap().load(mModel.largeImageURL).
                into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mUri = getFileUri(saveImageExternal(resource));
                        send();
                    }
                });
    }

    private Uri getFileUri(File file) {
        return FileProvider.getUriForFile(
                GalleryDetailActivity.this,
                getPackageName() + ".provider",
                file);

    }

    public void send() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, mUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Send"));
    }

    private File saveImageExternal(Bitmap image) {
        //TODO - Should be processed in another thread
        try {
            File file = new File(getExternalFilesDir("ShareImage"), "to-share.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
            return file;
        } catch (IOException e) {
            Log.d("Gallery", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return null;
    }


}
