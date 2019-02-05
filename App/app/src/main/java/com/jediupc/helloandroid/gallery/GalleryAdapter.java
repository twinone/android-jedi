package com.jediupc.helloandroid.gallery;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jediupc.helloandroid.ItemTouchHelperAdapter;
import com.jediupc.helloandroid.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> implements ItemTouchHelperAdapter {
    private final OnItemClickListener mListener;
    private ArrayList<GalleryModel> mDataset;
    private Set<Integer> mSelectedPositions = new HashSet<>();

    public Set<Integer> getSelectedPositions() {
        return mSelectedPositions;
    }

    private boolean mContextEnabled;

    public void setContextMode(boolean mode) {
        mContextEnabled = mode;
        //notifyDataSetChanged();
        if (!mContextEnabled) {
            mSelectedPositions = new HashSet<>();
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataset, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDataset, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);

        boolean onItemLongClick(View v, int pos);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ViewGroup mRoot;
        public ImageView mImage;
        public CheckBox mCheckBox;

        public MyViewHolder(ViewGroup v) {
            super(v);

            mRoot = v;
            mImage = v.findViewById(R.id.imageView);
            mCheckBox = v.findViewById(R.id.checkBox);
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
        GalleryModel gm = mDataset.get(position);


        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContextEnabled) {
                    if (mSelectedPositions.contains(position)) {
                        mSelectedPositions.remove(position);
                    } else {
                        mSelectedPositions.add(position);
                    }
                    notifyItemChanged(position);
                }

                mListener.onItemClick(view, position);
            }
        });

        holder.mRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!mContextEnabled) {
                    mSelectedPositions.add(position);
                }

                return mListener.onItemLongClick(view, position);
            }
        });

        Log.d("Gallery", "URL: " + gm.previewURL);

        holder.mCheckBox.setVisibility(mContextEnabled
                ? View.VISIBLE
                : View.GONE);
        holder.mCheckBox.setChecked(
                mSelectedPositions.contains(position));

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