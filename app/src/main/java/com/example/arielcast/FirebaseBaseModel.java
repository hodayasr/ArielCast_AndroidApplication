package com.example.arielcast;
import com.google.firebase.database.DatabaseReference;
import  com.google.firebase.database.FirebaseDatabase;

public class FirebaseBaseModel {
    protected DatabaseReference myRef;

    public FirebaseBaseModel()
    {
        myRef=FirebaseDatabase.getInstance().getReference();
    }
}