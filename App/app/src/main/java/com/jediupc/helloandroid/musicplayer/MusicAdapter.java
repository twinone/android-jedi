package com.jediupc.helloandroid.musicplayer;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jediupc.helloandroid.MenuItem;
import com.jediupc.helloandroid.R;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private final OnItemClickListener mListener;
    private ArrayList<MusicModel> mDataset;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    // Provide a reference to the views for each data text
    // Complex data items may need more than one view per text, and
    // you provide access to all the views for a data text in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data text is just a string in this case
        public RelativeLayout mRoot;
        public TextView mTextView;
        public TextView mDuration;

        public MyViewHolder(RelativeLayout v) {
            super(v);

            mRoot = v;
            mTextView = v.findViewById(R.id.tvTitle);
            mDuration = v.findViewById(R.id.tvDuration);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MusicAdapter(ArrayList<MusicModel> myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(view, holder.getAdapterPosition());
            }
        });

        holder.mTextView.setText(mDataset.get(position).name);
        holder.mDuration.setText(msToString(mDataset.get(position).duration));

    }


    @SuppressLint("DefaultLocale")
    private String msToString(long ms) {
        long seconds = (ms / 1000) % 60;
        long minutes = ms / 1000 / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}