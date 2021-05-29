package com.example.arielcast;

import android.content.Intent;
import android.os.Bundle;

import com.example.arielcast.firebase.model.dataObject.Course;
import com.example.arielcast.firebase.model.dataObject.Lecture;
import com.example.arielcast.firebase.model.dataObject.Lecturer;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PhonebookActivity extends AppCompatActivity {

    RecyclerView lecturerListView;
    PhonebookAdapter myAdapter;
    ArrayList<Lecturer> lecturers ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        lecturerListView = findViewById(R.id.phonelist);
        lecturerListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lecturerListView.setHasFixedSize(true);


        myAdapter = new PhonebookAdapter(this, getMyList());

        lecturerListView.setAdapter(myAdapter);

        myAdapter.notifyDataSetChanged();
    }

    private ArrayList<Lecturer> getMyList() {
        lecturers= new ArrayList<>();

        Query q = FirebaseDatabase.getInstance().getReference().child("Lecturers");

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Lecturer l = data.getValue(Lecturer.class);
                    lecturers.add(l);
                    myAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Collections.sort(lecturers);
        return lecturers ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            // this event will enable the back
            // function to the button on press
        if (item.getItemId() == android.R.id.home)
        {
                    this.finish();
                    return true;
        }
        return super.onOptionsItemSelected(item);
    }

}