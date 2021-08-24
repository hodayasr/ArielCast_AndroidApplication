package com.example.arielcast;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MailActivity extends AppCompatActivity {

    TextView textView;
    EditText title;
    TextInputEditText content;
    ImageButton imageButton ;
    //backButton;
    String cId , lecturerId,userId,userKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
     //   Toolbar toolbar = findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


        /*if (android.os.Build.VERSION.SDK_INT > 9) {*/ // unnecessary
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


        textView=findViewById(R.id.textView1);
        title=findViewById(R.id.editTextTextPersonName);
        content=findViewById(R.id.textInputEditText);
        imageButton=findViewById(R.id.imageButton);

        Intent intent = getIntent();
        cId=intent.getExtras().getString("CourseId");
        lecturerId=intent.getExtras().getString("lecID");
        userId=intent.getExtras().getString("ID");
        userKind=intent.getExtras().getString("userKind");


        imageButton.setOnClickListener(v -> {

            String et = title.getText().toString().trim();
            String ec = content.getText().toString().trim();

            //check that title and body text isn't empty
            if (et.isEmpty()) {
                title.setError("Email title is required!");
                title.requestFocus();
            } else if (ec.isEmpty()) {
                content.setError("Email body is required!");
                content.requestFocus();
            }
            else {
            if (userKind.equals("student")) {
                // get lecturer email by lecId
                Query query = FirebaseDatabase.getInstance().getReference().child("Lecturers").child(lecturerId);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = snapshot.child("email").getValue(String.class);

                        try {
                            GMailSender sender = new GMailSender("castariel01@gmail.com", "cast123456");
                            sender.sendMail(title.getText().toString().trim(),
                                    content.getText().toString().trim(),
                                    "castariel01@gmail.com",
                                    email);


                            Toast.makeText(getApplicationContext(), "email sent !", Toast.LENGTH_LONG).show();

                            final Intent in = new Intent(getApplicationContext(), StudentActivity.class);
                            in.putExtra("userKind","student");
                            in.putExtra("ID",userId);
                            in.putExtra("Email", "");

                            Thread thread = new Thread(){
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                                        startActivity(in);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            thread.start();

                        } catch (Exception e) {
                            Toast.makeText(MailActivity.this,
                                    "email not sent !",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (userKind.equals("lecturer")) {
                // get list of followers (students) of this course
                // and find their email - send massage all followers

                // get course by courseid and send email to students who following this course
                Query query = FirebaseDatabase.getInstance().getReference().child("Courses").child(cId);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String courseId = snapshot.child("courseId").getValue(String.class);

                        //find students following
                        Query quS = FirebaseDatabase.getInstance().getReference().child("StudentCourses").orderByChild("courseId").equalTo(cId);
                        quS.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    String studenti = data.child("studentId").getValue(String.class);
                                    Query qs = FirebaseDatabase.getInstance().getReference().child("Students").child(studenti);
                                    qs.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String stuemail = snapshot.child("email").getValue(String.class);
                                            try {
                                                GMailSender sender = new GMailSender("castariel01@gmail.com", "cast@1234567");
                                                sender.sendMail(title.getText().toString().trim(),
                                                        content.getText().toString().trim(),
                                                        "castariel01@gmail.com",
                                                        stuemail);


                                                Toast.makeText(getApplicationContext(), "email sent !", Toast.LENGTH_LONG).show();

                                                final Intent in = new Intent(getApplicationContext(), MainActivity.class);
                                                in.putExtra("userKind","lecturer");
                                                in.putExtra("ID",userId);
                                                in.putExtra("Email","");

                                                Thread thread = new Thread(){
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                                                            startActivity(in);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };

                                                thread.start();
                                            } catch (Exception e) {
                                                Toast.makeText(MailActivity.this,
                                                        "email not sent !",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(userKind.equals("lecturer")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
        }
        else if(userKind.equals("student"))
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.student_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logOut();
            return true;
        }
        else // this event will enable the back
             // function to the button on press
            if (item.getItemId() == android.R.id.home)
            {
                    this.finish();
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
        startActivity(new Intent(MailActivity.this, LoginActivity.class));
    }
}