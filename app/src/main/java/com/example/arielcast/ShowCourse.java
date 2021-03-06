package com.example.arielcast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arielcast.firebase.model.dataObject.Course;
import com.example.arielcast.firebase.model.dataObject.Lecture;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

public class ShowCourse extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static int UpdateImg=0;
    private ImageView imageView;
    RecyclerView lecturesListView;
    MyLecturesAdapter myAdapter;
    FirebaseRecyclerOptions<Course> options;
    FirebaseRecyclerAdapter<Course,MyViewHolder> adapter;
    ArrayList<Lecture> lectures ;
    TextView title, description;
    ImageButton deleteButton ,editButton;
    FloatingActionButton fab;
    DatabaseReference ref;
    static int position;
    String Id,email,cID , courseName,start,end ,imageurl ,lecturerID;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference DataRef;
    Dialog myDialog;
    ImageView courseImage;
    Uri NewimageUri;
    StorageReference storageReference;
    TextView tv3;
    Uri downloadUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_course);

        storageReference = FirebaseStorage.getInstance().getReference().child("Images");
        imageView = findViewById(R.id.lecture_image);
        title = findViewById(R.id.textViewMain);
        description = findViewById(R.id.textViewSub);
        deleteButton = findViewById(R.id.deleteButton5);
        editButton = findViewById(R.id.editButton5);
        fab=findViewById(R.id.floatingActionButton);
        tv3=findViewById(R.id.textView3);
        ref = FirebaseDatabase.getInstance().getReference().child("Courses");

        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


        lecturesListView = findViewById(R.id.watch_later_recycleView);
        lecturesListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lecturesListView.setHasFixedSize(true);
        DataRef = FirebaseDatabase.getInstance().getReference().child("Courses");

        // get Extras from previous intent
         email= getIntent().getExtras().getString("Email");
         Id=getIntent().getExtras().getString("ID");
         cID=getIntent().getExtras().getString("CourseId");

        myAdapter = new MyLecturesAdapter(this, getMyList(),Id);
        lecturesListView.setAdapter(myAdapter);


        //get course image
        DatabaseReference imRef = FirebaseDatabase.getInstance().getReference().child("Courses").child(cID);

        imRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 imageurl=snapshot.child("image").getValue(String.class);
                Picasso.with(getApplicationContext()).load(imageurl).fit().into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ref.child(String.valueOf(cID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                     courseName = snapshot.child("courseName").getValue(String.class);
                    lecturerID = snapshot.child("lecturerId").getValue(String.class);
                    start=snapshot.child("startDate").getValue(String.class);
                    end=snapshot.child("endDate").getValue(String.class);

                    // get lecturer name
                    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference().child("Lecturers").child(lecturerID);

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String lecName=snapshot.child("fullname").getValue(String.class);

                            description.setText(lecName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    title.setText(courseName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //check if it's lecturer user
        Query query = myRef.child("Lecturers").orderByChild("lecturerId").equalTo(Id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            private void onClick(View v) {
                myDialog = new Dialog(ShowCourse.this);
                myDialog.setContentView(R.layout.edit_course_dialog);
                myDialog.setTitle("Edit this course ");
                EditText course_name = myDialog.findViewById(R.id.insertNewLectureName);
                course_name.setText(courseName);
                EditText courseStart = myDialog.findViewById(R.id.editTextStartDate);
                courseStart.setText(start);
                EditText courseEnd = myDialog.findViewById(R.id.editTextEndDate);

                courseEnd.setText(end);
                courseImage = myDialog.findViewById(R.id.imageView3);
                Picasso.with(getApplicationContext()).load(imageurl).fit().into(courseImage);
                Button changeImage = myDialog.findViewById(R.id.updateLectureVideo);
                changeImage.setOnClickListener(v13 -> {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE);
                });

                Button editb = myDialog.findViewById(R.id.continueb);
                Button cb = myDialog.findViewById(R.id.cb);
                cb.setOnClickListener(v14 -> myDialog.cancel());
                ImageView iv = myDialog.findViewById(R.id.imv);

                editb.setOnClickListener(v15 -> {
                    // update course on FireBase database
                    if(UpdateImg==1) {
                    final StorageReference myRef = storageReference.child(currentTimeMillis() + "." + getExt(NewimageUri));


                        Task<UploadTask.TaskSnapshot> uploadTask = myRef.putFile(NewimageUri);
                        Task<Uri> taskurl = uploadTask.continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return myRef.getDownloadUrl();
                        }).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                downloadUri = task.getResult();

                                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference().child("Courses").child(cID);

                                Course c = new Course(cID, course_name.getText().toString(), lecturerID, courseStart.getText().toString()
                                                , courseEnd.getText().toString(), downloadUri.toString());
                                   updateRef.setValue(c);

                                Intent intent = new Intent(ShowCourse.this, ShowCourse.class);
                                intent.putExtra("CourseId", cID);
                                intent.putExtra("Email", email);
                                intent.putExtra("ID", Id);
                                startActivity(intent);
                                Toast.makeText(ShowCourse.this, "Course updated!",
                                        Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(ShowCourse.this, "Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if(UpdateImg==0) {
                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference().child("Courses").child(cID);

                        updateRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Course c = dataSnapshot.getValue(Course.class);

                                    c.setCourseName(course_name.getText().toString());
                                    c.setStartDate(courseStart.getText().toString());
                                    c.setEndDate(courseEnd.getText().toString());
                                        /*  Course c = new Course(cID, course_name.getText().toString(), lecturerID, courseStart.getText().toString()
                                                , courseEnd.getText().toString(), downloadUri.toString());
                                        updateRef.setValue(c);*/
                                    updateRef.setValue(c);
                                    Intent intent = new Intent(ShowCourse.this, ShowCourse.class);
                                    intent.putExtra("CourseId", cID);
                                    intent.putExtra("Email", email);
                                    intent.putExtra("ID", Id);
                                    startActivity(intent);
                                    Toast.makeText(ShowCourse.this, "Course updated!",
                                            Toast.LENGTH_LONG).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                myDialog.show();
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    position=0; // lecturer !!
                    for(DataSnapshot data:snapshot.getChildren()) {
                        fab.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                        editButton.setVisibility(View.VISIBLE);

                        //delete course :
                        deleteButton.setOnClickListener(v -> {
                            myDialog =new Dialog(ShowCourse.this);
                            myDialog.setContentView(R.layout.delete_course_dialog);
                            myDialog.setTitle("Delete this course ?");
                            TextView hello=(myDialog.findViewById(R.id.hello));
                            Button db=myDialog.findViewById(R.id.continueb) ;
                            Button cb=myDialog.findViewById(R.id.cb) ;
                            ImageView iv=myDialog.findViewById(R.id.imv) ;
                            myDialog.show();
                            cb.setOnClickListener(v1 -> myDialog.cancel());
                            db.setOnClickListener(v12 -> {
                                DatabaseReference refe=FirebaseDatabase.getInstance().getReference().child("Courses");
                                refe.child(cID).removeValue();

                                // delete all lectures of this course
                                Query leq=FirebaseDatabase.getInstance().getReference().child("Lectures").orderByChild("courseId").equalTo(cID);

                                leq.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        for(DataSnapshot data1 : snapshot1.getChildren())
                                        {
                                            data1.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                Intent i=new Intent(ShowCourse.this, MainActivity.class);
                                i.putExtra("ID",Id);
                                i.putExtra("Email",email);
                                startActivity(i);
                            });
                        });

                        // edit course : name ,StartDate ,EndDate ,image
                        editButton.setOnClickListener(this::onClick);
                    }
                } else {
                    Query query = myRef.child("Students").orderByChild("studentId").equalTo(Id);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                position=1; // student !!
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    fab.setVisibility(View.INVISIBLE);
                                    deleteButton.setVisibility(View.INVISIBLE);
                                    editButton.setVisibility(View.INVISIBLE);
                                }
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
                throw error.toException(); // don't ignore errors
            }
        });

    }


    public void onfabClick(View v)
    {
        // save lecturer's email and start AddLectureActivity
        Intent i = new Intent(ShowCourse.this, AddLectureActivity.class);
        i.putExtra("Email", email);
        i.putExtra("ID", Id);
        i.putExtra("CourseId", cID);
        startActivity(i);
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
        startActivity(new Intent(ShowCourse.this, LoginActivity.class));
    }

    private ArrayList<Lecture> getMyList(){
        lectures = new ArrayList<>();

        Query q = FirebaseDatabase.getInstance().getReference().child("Lectures").orderByChild("courseId").equalTo(cID);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Lecture c = data.getValue(Lecture.class);
                    lectures.add(c);
                    myAdapter.notifyDataSetChanged();
                }

                if(!snapshot.exists()) tv3.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return lectures;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE || requestCode == RESULT_OK ||
                data != null || data.getData() != null) {
            NewimageUri = data.getData();
            courseImage.setImageURI(NewimageUri);
            UpdateImg=1;
        }
    }

    private String getExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}