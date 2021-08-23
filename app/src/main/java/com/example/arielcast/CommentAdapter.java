package com.example.arielcast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arielcast.firebase.model.dataObject.Comment;
import com.example.arielcast.firebase.model.dataObject.Course;
import com.example.arielcast.firebase.model.dataObject.StudentCourses;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {
    Context context;
    ArrayList<Comment> comments;
    String userId;

    public CommentAdapter(Context context, ArrayList<Comment> comments, String userID) {
        this.context = context;
        this.comments= comments;
        this.userId = userID;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment,null);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.mTitle.setText(comments.get(position).getCommentText());
        holder.mDate.setText(comments.get(position).getDate());
        holder.mDes.setText(comments.get(position).getFullName());

        if(comments.get(position).getUserId().toString().equals(userId)) {
            holder.mdel.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void filterList(ArrayList<Comment> filteredList){
        comments = filteredList;
        notifyDataSetChanged();
    }
}
