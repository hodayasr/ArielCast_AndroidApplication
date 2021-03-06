package com.example.arielcast;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arielcast.firebase.model.dataObject.Course;
import com.example.arielcast.firebase.model.dataObject.Lecture;
import com.example.arielcast.firebase.model.dataObject.Lecturer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class PhonebookAdapter extends RecyclerView.Adapter<PhonebookHolder> {
    Context context;
    ArrayList<Lecturer> lecturers;

    public PhonebookAdapter(Context context,ArrayList<Lecturer> l) {
        this.context = context;
        this.lecturers = l;

    }

    @NonNull
    @Override
    public PhonebookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_phone,null);
        return new PhonebookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhonebookHolder holder, int position) {
        Collections.sort(lecturers, new Comparator<Lecturer>() {
            @Override
            public int compare(Lecturer o1, Lecturer o2) {
                return o1.getFullname().charAt(0) < o2.getFullname().charAt(0) ? -1 : (o1.getFullname().charAt(0) < o2.getFullname().charAt(0) ) ? 1 : 0;
            }

        });
        holder.mTitle.setText(lecturers.get(position).getFullname());
        holder.mEmail.setText(lecturers.get(position).getEmail());
        holder.mPhone.setText(lecturers.get(position).getPhone());


    }

    @Override
    public int getItemCount() {
        return lecturers.size();
    }

    public void filterList(ArrayList<Lecturer> filteredList){
        lecturers = filteredList;
        notifyDataSetChanged();
    }

}