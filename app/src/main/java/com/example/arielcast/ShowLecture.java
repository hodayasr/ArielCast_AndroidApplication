package com.example.arielcast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.arielcast.firebase.model.dataObject.Comment;
import com.example.arielcast.firebase.model.dataObject.Course;
import com.example.arielcast.firebase.model.dataObject.Lecture;
import com.example.arielcast.firebase.model.dataObject.WatchLaterLec;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;

public class ShowLecture extends AppCompatActivity {
    private static final int PICK_VIDEO = 1;
    private static int UpdateVideo=0;
    StorageReference storageReference;
    String lectureID;
    String videoPath ,videoPathD;
    TextView lecture_name;
    ImageButton editButton,deleteButton , addToPlaylist;
    TextView lecNameText , dateText ,commentstitle;
    DatabaseReference dataRef ,dataRefD;
    FirebaseDatabase database;
    String lecturername;
    Dialog myDialog;
    ImageButton button ,fullscreenbtn;
    Uri uri ,uriD;
    CustomVideoView videoView;

    VideoView videoView2; // edit dialod VideoView
    int position;
    private RelativeLayout.LayoutParams defaultVideoViewParams;
    private int defaultScreenOrientationMode;

    TextInputEditText commentbody ;
    ImageButton sendcomment;

    Uri videoUri;


    CommentAdapter commentAdapter;
    RecyclerView commentlist;
    ArrayList<Comment> comments;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_lecture);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
           // requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }

        storageReference = FirebaseStorage.getInstance().getReference().child("Video");

        //comments
        commentstitle=findViewById(R.id.titleComments);
        commentstitle.setText("Comments \t\t\t | \t\t\t 0");
        commentbody=findViewById(R.id.commentbody);
        sendcomment=findViewById(R.id.sendcomment);
       /* if(commentbody.getText().toString().isEmpty())
            sendcomment.setClickable(false);
        else
            sendcomment.setClickable(true);

        sendcomment.setOnClickListener(v -> {
            if(sendcomment.isClickable()) {
                commentbody.setText("it's works");
            }
        }); */



        lecture_name = findViewById(R.id.et_video_name);
        lecNameText=findViewById(R.id.textViewSub_lecName);
        dateText=findViewById(R.id.textViewSub_date);
        editButton=findViewById(R.id.editButton5);
        deleteButton=findViewById(R.id.deleteButton);
        addToPlaylist=findViewById(R.id.add_to_watch_later_list);
        videoView = findViewById(R.id.lecture_view);
        fullscreenbtn=findViewById(R.id.fullscreenbtn);
        Drawable fcd = ContextCompat.getDrawable(getApplicationContext(), R.drawable.fullscreenexit);

        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        fullscreenbtn.setOnClickListener(v -> {
            fullscreenbtn.setBackground(fcd);

             defaultScreenOrientationMode = getResources().getConfiguration().orientation;
            defaultVideoViewParams = (RelativeLayout.LayoutParams) videoView.getLayoutParams();

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            videoView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);

                    videoView.setLayoutParams(params);
                    videoView.layout(10, 10, 10, 10);

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            }, 700);

        });

        lectureID = getIntent().getExtras().getString("lecID");
        String id =getIntent().getExtras().getString("ID");
        String lecturerId= getIntent().getExtras().getString("lecturerId");
        lecture_name.setText(lectureID);

        //comments list
        commentAdapter=new CommentAdapter(this,getMylist(),id);
        commentlist= findViewById(R.id.comments_recycleview);
        commentlist.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();



        String SCId = id + "-" + lectureID;
        DatabaseReference dbl = FirebaseDatabase.getInstance().getReference().child("WatchLaterLec").child(SCId);
        dbl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    // remove from playlist
                    Drawable d = ContextCompat.getDrawable(ShowLecture.this, R.drawable.ic_baseline_playlist_add_check_24);
                    //addToPlaylist.setImageDrawable(d);
                    addToPlaylist.setBackground(d);
                    addToPlaylist.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataRef.child(SCId).removeValue();
                            Toast.makeText(ShowLecture.this,"removed from Watch Later",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // add to playlist
                    Drawable d = ContextCompat.getDrawable(ShowLecture.this, R.drawable.capture1212);
                    //addToPlaylist.setImageDrawable(d);
                    addToPlaylist.setBackground(d);
                    addToPlaylist.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataRef = FirebaseDatabase.getInstance().getReference("WatchLaterLec");
                            WatchLaterLec lec = new WatchLaterLec(id, lectureID);
                            dataRef.child(SCId).setValue(lec);
                            Toast.makeText(ShowLecture.this,"added to Watch Later",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //check if it's lecturer user
        Query query = FirebaseDatabase.getInstance().getReference().child("Lecturers").orderByChild("lecturerId").equalTo(id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot data:snapshot.getChildren()) {

                        // if lecturer
                        position=0; // lecturer !!
                        editButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                        addToPlaylist.setVisibility(View.INVISIBLE);

                        //delete lecture
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog =new Dialog(ShowLecture.this);
                                myDialog.setContentView(R.layout.delete_course_dialog);
                                myDialog.setTitle("Delete this lecture ?");
                                TextView hello=(TextView) myDialog.findViewById(R.id.hello);
                                hello.setText("Are you sure you want to delete this lecture ?");
                                Button db=(Button)myDialog.findViewById(R.id.continueb) ;
                                Button cb=(Button)myDialog.findViewById(R.id.cb) ;
                                db.setText("Delete this lecture");
                                ImageView iv=(ImageView)myDialog.findViewById(R.id.imv) ;
                                myDialog.show();
                                cb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialog.cancel();
                                    }
                                });
                                db.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatabaseReference refe=FirebaseDatabase.getInstance().getReference().child("Lectures");
                                        refe.child(lectureID).removeValue();
                                        Intent i=new Intent(ShowLecture.this, MainActivity.class);
                                        i.putExtra("ID",id);
                                        startActivity(i);
                                    }
                                });
                            }
                        });

                        editButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                myDialog =new Dialog(ShowLecture.this);
                                myDialog.setContentView(R.layout.edit_lecture_dialog);
                                myDialog.setTitle("Edit lecture ");
                                myDialog.show();
                                VideoView videoDialog=(VideoView)myDialog.findViewById(R.id.videoView2);
                                dataRefD = FirebaseDatabase.getInstance().getReference().child("Lectures").child(lectureID);


                                dataRefD.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()) {
                                            videoPathD = snapshot.child("video_url").getValue(String.class);
                                            uriD = Uri.parse(videoPathD);
                                            videoDialog.setVideoURI(uriD);
                                            videoDialog.requestFocus();
                                            videoDialog.start();

                                        }}

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                EditText lecturenameEdit=myDialog.findViewById(R.id.insertNewLectureName);
                                lecturenameEdit.setText(lecture_name.getText());
                                Button updatebtn=myDialog.findViewById(R.id.updateLectureVideo);
                                updatebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        videoView2=myDialog.findViewById(R.id.videoView2);
                                        // choose video
                                        Intent intent = new Intent();
                                        intent.setType("video/*, audio/*");
                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(intent,PICK_VIDEO);
                                    }
                                });

                                Button updateLecBtn=myDialog.findViewById(R.id.continueb);
                                updateLecBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO : edit lecture name and video file
                                        if(UpdateVideo==1) {
                                            final StorageReference myRef = storageReference.child(currentTimeMillis() + "." + getExt(videoUri));


                                            Task<UploadTask.TaskSnapshot> uploadTask = myRef.putFile(videoUri);
                                            Task<Uri> taskurl = uploadTask.continueWithTask(task -> {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                return myRef.getDownloadUrl();
                                            }).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Uri downloadUri = task.getResult();

                                                    DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference()
                                                            .child("Lectures").child(lectureID);

                                                    updateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            Lecture lec= snapshot.getValue(Lecture.class);
                                                            lec.setLectureName(lecturenameEdit.getText().toString());
                                                            lec.setVideo_url(downloadUri.toString());
                                                            FirebaseDatabase.getInstance().getReference().child("Lectures")
                                                                    .child(lectureID).getRef().removeValue();
                                                            FirebaseDatabase.getInstance().getReference().child("Lectures")
                                                                    .child(lecturenameEdit.getText().toString()).setValue(lec);


                                                            Intent intent = new Intent(ShowLecture.this, ShowLecture.class);
                                                            intent.putExtra("lecID",lecturenameEdit.getText().toString());
                                                            intent.putExtra("lecturerId", lecturerId);
                                                            intent.putExtra("ID", id);

                                                            startActivity(intent);
                                                            Toast.makeText(ShowLecture.this, "Lecture updated!",
                                                                    Toast.LENGTH_LONG).show();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });


                                                } else {
                                                    Toast.makeText(ShowLecture.this, "Failed",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        if(UpdateVideo==0) {
                                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference()
                                                    .child("Lectures").child(lectureID);

                                            updateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()) {
                                                        Lecture lec = snapshot.getValue(Lecture.class);
                                                        lec.setLectureName(lecturenameEdit.getText().toString());

                                                        FirebaseDatabase.getInstance().getReference().child("Lectures")
                                                                .child(lectureID).getRef().removeValue();
                                                        FirebaseDatabase.getInstance().getReference().child("Lectures")
                                                                .child(lecturenameEdit.getText().toString()).setValue(lec);

                                                        Intent intent = new Intent(ShowLecture.this, ShowLecture.class);
                                                        intent.putExtra("lecID", lecturenameEdit.getText().toString());
                                                        intent.putExtra("lecturerId", lecturerId);
                                                        intent.putExtra("ID", id);

                                                        startActivity(intent);
                                                        Toast.makeText(ShowLecture.this, "Lecture updated!",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                        }
                                    }
                                });

                                Button cb=(Button)myDialog.findViewById(R.id.cb) ;
                                cb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialog.cancel();
                                    }
                                });
                            }
                        });
                    }
                } else {
                    position=1; // student !!
                    editButton.setVisibility(View.INVISIBLE);
                    deleteButton.setVisibility(View.INVISIBLE);
                    addToPlaylist.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException(); // don't ignore errors
            }
        });

        dataRef = FirebaseDatabase.getInstance().getReference().child("Lectures").child(lectureID);

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    videoPath = snapshot.child("video_url").getValue(String.class);
                    uri = Uri.parse(videoPath);
                    videoView.setVideoURI(uri);
                    videoView.requestFocus();

                    //MediaController mc=new MediaController(getApplicationContext());

                    videoView.start();

                    String date = snapshot.child("date").getValue(String.class);
                    dateText.setText(snapshot.child("date").getValue(String.class));

                    Query qe=FirebaseDatabase.getInstance().getReference().child("Lecturers").orderByChild("lecturerId").equalTo(lecturerId);

                    qe.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            lecturername=snapshot.child(lecturerId).child("fullname").getValue(String.class);
                            lecNameText.setText(lecturername);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // download button !

                    button = (ImageButton) findViewById(R.id.downloadbutton);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadManager.Request r = new DownloadManager.Request(uri);

                            // This put the download in the same Download dir the browser uses
                            r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, lecture_name.getText().toString());

                            // When downloading music and videos they will be listed in the player
                            // (Seems to be available since Honeycomb only)
                            r.allowScanningByMediaScanner();

                            // Notify user when download is completed
                            // (Seems to be available since Honeycomb only)
                            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                            // Start download
                            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                            dm.enqueue(r);
                            Toast.makeText(ShowLecture.this, "starting download.. be patient", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(position==0) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
        }
        else if(position==1)
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
        startActivity(new Intent(ShowLecture.this, LoginActivity.class));
    }

    /*private void UploadVideo(){
        String videoName = editText.getText().toString();
        String search = editText.getText().toString().toLowerCase();
        if (uri != null ) {
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
                            Toast.makeText(ShowLecture.this, "Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO || requestCode == RESULT_OK ||
                data != null || data.getData() != null) {
            videoUri = data.getData();
            UpdateVideo=1;
            videoView2.setVideoURI(videoUri);
            videoView2.requestFocus();
            videoView2.start();
        }
    }


    private String getExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private ArrayList<Comment> getMylist() {
        comments=new  ArrayList<Comment>();


        Query q = FirebaseDatabase.getInstance().getReference().child("Comments").child("")
                .orderByChild("lectureId").equalTo(lectureID);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Comment c = data.getValue(Comment.class);
                    comments.add(c);
                    commentAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return comments;
    }

}