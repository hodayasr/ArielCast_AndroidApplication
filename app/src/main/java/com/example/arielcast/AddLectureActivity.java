package com.example.arielcast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.arielcast.firebase.model.dataObject.Course;
import com.example.arielcast.firebase.model.dataObject.Lecture;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.in;

public class AddLectureActivity extends AppCompatActivity{
    private static final int PICK_VIDEO = 1;
    VideoView videoView;
    Button addLec;
    ProgressBar progressBar;
    EditText editText;
    Uri videoUri;
    MediaController mediaController;
    Lecture lecture;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    UploadTask uploadTask;
    String lecturerEmail,lecId,cId;
    String coursename;

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(AddLectureActivity.this, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_lecture);

        lecture = new Lecture();
        storageReference = FirebaseStorage.getInstance().getReference().child("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Lectures");

        Intent intent=getIntent();
        lecturerEmail=intent.getExtras().getString("Email");
        lecId=intent.getExtras().getString("ID");
        cId=intent.getExtras().getString("CourseId");

        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        videoView = findViewById(R.id.videoview_main);
        addLec = findViewById(R.id.addLectureButton);
        progressBar = findViewById(R.id.progressBar);
        editText = findViewById(R.id.et_video_name);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        addLec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    UploadVideo();
            }
        });
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO || requestCode == RESULT_OK ||
                data != null || data.getData() != null) {
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }
    }

    public void ChooseVideo(View view) {
        Intent intent = new Intent();
        intent.setType("video/*, audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_VIDEO);
    }

    private String getExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)); 
    }


    public void ShowVideo(View view) {
    }

    private void UploadVideo(){
        String videoName = editText.getText().toString();
        String search = editText.getText().toString().toLowerCase();
        if (videoUri != null ) {
            if(!TextUtils.isEmpty(videoName)) {
                progressBar.setVisibility(View.VISIBLE);
                final StorageReference myRef = storageReference.child(currentTimeMillis() + "." + getExt(videoUri));
                uploadTask = myRef.putFile(videoUri);

                Task<Uri> taskurl = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return myRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddLectureActivity.this, "Data saved!",
                                    Toast.LENGTH_LONG).show();


                            // get Email ( from Extras) from LecturerActivity

                            lecture.setLectureName(videoName);
                            lecture.setLecturerId(lecId);
                            lecture.setCourseId(cId);
                            lecture.setVideo_url(downloadUri.toString());
                            lecture.setSearch(search);

                            Date presentTime_Date = Calendar.getInstance().getTime();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                            String date= dateFormat.format(presentTime_Date);

                            lecture.setDate(date);
                        //    lecture.setLecturerEmail(lecturerEmail); // id
                            databaseReference.child(videoName).setValue(lecture);
                            Intent i=new Intent(AddLectureActivity.this, ShowCourse.class);
                            i.putExtra("Email",lecturerEmail);
                            i.putExtra("ID",lecId);
                            i.putExtra("lecID",cId);
                            i.putExtra("CourseId",cId);
                            startActivity(i);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(AddLectureActivity.this, "My Notification");
                            builder.setSmallIcon(R.drawable.ic_baseline_chat_24);
                            builder.setContentTitle("new lecture was upload");

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
                                                            sender.sendMail(coursename,
                                                                    "A New lecture " + lecture.getLectureName() + " was upload to the course " + coursename,
                                                                    "castariel01@gmail.com",
                                                                    stuemail);

                                                        } catch (Exception e) {
                                                         /*   Toast.makeText(AddLectureActivity.this,
                                                                    "email not sent !",
                                                                    Toast.LENGTH_LONG).show();*/
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

                            //notification for lecturer
                            Query coursequery = FirebaseDatabase.getInstance().getReference().child("Courses").child("").orderByChild("courseId").equalTo(lecture.getCourseId());
                            coursequery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        Course c = data.getValue(Course.class);
                                        coursename=c.getCourseName();
                                        builder.setContentText("A New lecture " + lecture.getLectureName() + " was upload to the course " + coursename);
                                        builder.setAutoCancel(true);

                                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(AddLectureActivity.this);
                                        managerCompat.notify(1, builder.build());
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }});

                        } else {
                            Toast.makeText(AddLectureActivity.this, "Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            } else if (TextUtils.isEmpty(videoName)) {
                editText.setError("please enter video name");
                editText.requestFocus();
                return;
            } else {
            Toast.makeText(this,"All Fields are required",Toast.LENGTH_SHORT).show();
        }
    }



}
