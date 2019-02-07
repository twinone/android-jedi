package com.jediupc.helloandroid.todolist;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.jediupc.helloandroid.R;
import com.jediupc.helloandroid.model.TodoListModel;

import java.util.ArrayList;
import java.util.Collections;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.MyViewHolder> implements TodoListActivity.ItemTouchHelperAdapter {
    private final TodoListCallback mListener;
    private ArrayList<TodoListModel.TodoListItemModel> mDataset;

    public interface TodoListCallback {
    }

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

    // Provide a reference to the views for each data text
    // Complex data items may need more than one view per text, and
    // you provide access to all the views for a data text in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data text is just a string in this case
        public RelativeLayout mRoot;
        public EditText mET;
        public CheckBox mCB;
        public TextWatcher mWatcher;

        public MyViewHolder(RelativeLayout v) {
            super(v);

            mRoot = v;
            mET = v.findViewById(R.id.etItem);
            mCB = v.findViewById(R.id.cbChecked);


        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TodoListAdapter(ArrayList<TodoListModel.TodoListItemModel> myDataset, TodoListCallback listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TodoListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        int pos = holder.getAdapterPosition();
        TodoListModel.TodoListItemModel im = mDataset.get(pos);

        holder.mCB.setChecked(im.checked);
        holder.mET.setText(im.text);

        holder.mCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDataset.get(holder.getAdapterPosition()).checked = isChecked;
            }
        });

        if (holder.mWatcher != null) {
            holder.mET.removeTextChangedListener(holder.mWatcher);

        }
        holder.mWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDataset.get(holder.getAdapterPosition()).text = s.toString();
            }
        };
        holder.mET.addTextChangedListener(holder.mWatcher);

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}