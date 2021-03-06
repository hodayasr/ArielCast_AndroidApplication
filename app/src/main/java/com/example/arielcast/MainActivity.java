package com.example.arielcast;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arielcast.firebase.model.dataObject.Course;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "com.example.arielcast.MainActivity.FILE_NAME";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    RecyclerView coursesListView;
    MyAdapter myAdapter;
    ArrayList<Course> courses ;
    String email ,lecId , password;
    SharedPreferences sharedPreferences;
    EditText text;

    private static final String SHARED_PREF_NAME="mypfer";
    private static final String KEY_PASS="password";
    private static final String KEY_EMAIL="email";
    private static final String KEY_ID="ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);

        email=sharedPreferences.getString("email",null);
         password=sharedPreferences.getString("password",null);
         lecId=sharedPreferences.getString("ID",null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        text=findViewById(R.id.textview);
        coursesListView = findViewById(R.id.watch_later_recycleView);
        coursesListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        coursesListView.setHasFixedSize(true);

        setSupportActionBar(toolbar);

        // get lecturer's email from MainActivity
        Intent intent = getIntent();
        email = intent.getExtras().getString("Email");
        lecId = intent.getExtras().getString("ID");

        //show my courses
        myAdapter = new MyAdapter (this, getMyList(),lecId);

        coursesListView = findViewById(R.id.watch_later_recycleView);
        coursesListView.setAdapter(myAdapter);

        myAdapter.notifyDataSetChanged();

        // add lecture Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.fab) {

                    // save lecturer's email and start AddCourseActivity
                    Intent i = new  Intent(MainActivity.this, AddCourseActivity.class);
                    i.putExtra("Email",email);
                    i.putExtra("ID",lecId);
                    startActivity(i);
                }
            }
        });
        closeKeyboard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logOut();
            return true;
        }

        if(item.getItemId()==R.id.aboutus)
        {
            startActivity(new Intent(this, AboutUsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private ArrayList<Course> getMyList(){
        courses = new ArrayList<>();


        Query q = FirebaseDatabase.getInstance().getReference().child("Courses").child("").orderByChild("lecturerId").equalTo(lecId);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Course c = data.getValue(Course.class);
                    courses.add(c);
                    myAdapter.notifyDataSetChanged();

                }
                if(courses.isEmpty())
                {

                    text.setVisibility(View.VISIBLE);
                    text.setText("");
                    text.setError("Add New Course here",null);
                    text.requestFocus();
                    closeKeyboard();
                    return;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return courses;
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
