package com.jediupc.helloandroid.audiorecorder;

import android.annotation.SuppressLint;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jediupc.helloandroid.R;

import java.util.ArrayList;
import java.util.Collections;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.MyViewHolder> implements AudioActivity.ItemTouchHelperAdapter {
    private final AudioAdapterCallback mListener;
    private ArrayList<AudioModel> mDataset;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataset, i, i + 1);
                Log.d("Gallery", "From " + fromPosition + " " + toPosition);
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

    public interface AudioAdapterCallback {
        void onPlay(View v, int pos);

        void onSeek(View v, int pos, float progress);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout mRoot;
        private TextView mTVDuration;
        private TextView mTVCurrTime;
        private SeekBar mSeekbar;
        private FloatingActionButton mButton;

        public MyViewHolder(RelativeLayout v) {
            super(v);

            mRoot = v;
            mTVDuration = v.findViewById(R.id.tvDuration);
            mTVCurrTime = v.findViewById(R.id.tvCurrTime);
            mSeekbar = v.findViewById(R.id.seekBar);
            mButton = v.findViewById(R.id.fab);

        }


        public void update(long duration, int progress) {
            mTVDuration.setText(msToString(duration));
            mTVCurrTime.setText(msToString(progress));
            mSeekbar.setProgress((int) (progress * mSeekbar.getMax() / duration));
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AudioAdapter(ArrayList<AudioModel> myDataset, AudioAdapterCallback listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AudioAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPlay(view, holder.getAdapterPosition());
            }
        });
        holder.mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                mListener.onSeek(seekBar, holder.getAdapterPosition(), progress / (float) holder.mSeekbar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        AudioModel am = mDataset.get(position);
        holder.update(am.duration, 0);

    }


    @SuppressLint("DefaultLocale")
    private static String msToString(long ms) {
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