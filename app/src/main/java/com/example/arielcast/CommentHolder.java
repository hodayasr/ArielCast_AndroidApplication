package com.example.arielcast;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentHolder extends RecyclerView.ViewHolder {
    TextView mTitle, mDes , mDate;
    ImageButton mdel;

    public CommentHolder(@NonNull View itemView) {
        super(itemView);
        this.mTitle = itemView.findViewById(R.id.textViewMain);
        this.mDate=itemView.findViewById(R.id.textViewdate);
        this.mDes = itemView.findViewById(R.id.textViewSub);
        this.mdel=itemView.findViewById(R.id.deletecommentbtn);

    }

}
