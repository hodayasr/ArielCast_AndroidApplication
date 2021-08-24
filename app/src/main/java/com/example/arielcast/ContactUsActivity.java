package com.example.arielcast;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ContactUsActivity extends AppCompatActivity {

    TextView textView;
    EditText fullname , mail;
    TextInputEditText content;
    ImageButton imageButton ;
    String cId , lecturerId,userId,userKind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);


        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


        textView=findViewById(R.id.textView1);
        fullname=findViewById(R.id.editTextTextPersonName);
        mail=findViewById(R.id.editTextmail);
        content=findViewById(R.id.textInputEditText);
        imageButton=findViewById(R.id.imageButton);


        imageButton.setOnClickListener(v -> {

            String et = fullname.getText().toString().trim();
            String em = mail.getText().toString().trim();
            String ec = content.getText().toString().trim();

            //check that title and body text isn't empty
            if (et.isEmpty()) {
                fullname.setError("full name is required!");
                fullname.requestFocus();
            } else if (ec.isEmpty()) {
                content.setError("Message text is required!");
                content.requestFocus();
            }
            else if (em.isEmpty()) {
                mail.setError("your email is required!");
                mail.requestFocus();
            }
            else {
                        try {
                            GMailSender sender = new GMailSender("castariel01@gmail.com", "cast@1234567");
                            sender.sendMail(fullname.getText().toString().trim()
                                            +" - "+mail.getText().toString().trim(),
                                    content.getText().toString().trim(),
                                    "castariel01@gmail.com",
                                    "castariel01@gmail.com");


                            Toast.makeText(getApplicationContext(), "Your message has been sent to us and will be processed soon !", Toast.LENGTH_LONG).show();

                            final Intent in = new Intent(getApplicationContext(), LoginActivity.class);

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
                            Toast.makeText(ContactUsActivity.this,
                                    "email not sent !",
                                    Toast.LENGTH_LONG).show();
                        }


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

}