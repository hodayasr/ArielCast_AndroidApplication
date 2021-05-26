package com.example.arielcast;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhonebookHolder extends RecyclerView.ViewHolder {

    TextView mTitle, mEmail , mPhone;

    public PhonebookHolder(@NonNull View itemView) {
        super(itemView);

        this.mTitle = itemView.findViewById(R.id.textViewMain);
        this.mEmail=itemView.findViewById(R.id.textViewSub);
        this.mPhone=itemView.findViewById(R.id.textViewSub2);
    }

}
