package com.jediupc.helloandroid.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jediupc.helloandroid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class GalleryItemFragment extends Fragment {
    private GalleryModel mModel;
    private RequestQueue queue;
    private Uri mUri;


    private ImageView mImage;
    private TextView mText;
    private int mPosition;
    private TextView mDesc;


    public static GalleryItemFragment newInstance(GalleryModel gm, int pos) {
        GalleryItemFragment fragmentFirst = new GalleryItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("model", gm);
        args.putInt("position", pos);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = (GalleryModel) getArguments().getSerializable("model");
        mPosition = getArguments().getInt("position");

        queue = Volley.newRequestQueue(getActivity());


    }

    private void onPreviewLoaded() {
        Glide.with(this).asBitmap().load(mModel.largeImageURL).
                into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mImage.setImageBitmap(resource);
                        mUri = getFileUri(saveImageExternal(resource));
                        //                       send();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_item, container, false);
        mImage = view.findViewById(R.id.imageView);

        mText = view.findViewById(R.id.textViewTitle);
        mDesc = view.findViewById(R.id.textViewDesc);

        mText.setText(String.format("@%s (%d views)", mModel.user, mModel.views));
        mDesc.setText("Image by Pixabay");

        Glide.with(getActivity()).asBitmap().load(mModel.previewURL).
                into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mImage.setImageBitmap(resource);
                        onPreviewLoaded();
                    }
                });

        return view;
    }


    private Uri getFileUri(File file) {
        return FileProvider.getUriForFile(
                getActivity(),
                getActivity().getPackageName() + ".provider",
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
            File file = new File(getActivity().getExternalFilesDir("ShareImage"), "to-share-" + System.currentTimeMillis() + ".png");
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
