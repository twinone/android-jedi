package com.jediupc.helloandroid.gallery;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jediupc.helloandroid.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private final OnItemClickListener mListener;
    private ArrayList<GalleryModel> mDataset;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ViewGroup mRoot;
        public ImageView mImage;

        public MyViewHolder(ViewGroup v) {
            super(v);

            mRoot = v;
            mImage = v.findViewById(R.id.imageView);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GalleryAdapter(ArrayList<GalleryModel> myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(view, position);
            }
        });

        // TODO load into imageView
        GalleryModel gm = mDataset.get(position);

        Log.d("Gallery", "URL: " + gm.previewURL);

        Glide
                .with(holder.mImage.getContext())
                .load(gm.previewURL)
                .apply(new RequestOptions()
                        .centerCrop())
                .into(holder.mImage);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("Gallery", "GetItemCount" + String.valueOf(mDataset.size()));
        return mDataset.size();
    }
}