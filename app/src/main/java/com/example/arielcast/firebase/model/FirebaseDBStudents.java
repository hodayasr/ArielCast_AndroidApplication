package com.example.arielcast.firebase.model;

import com.example.arielcast.firebase.model.dataObject.StudentObj;
import com.google.firebase.database.DatabaseReference;

public class FirebaseDBStudents extends FirebaseBaseModel {
    public void addStudentToDB(String email,String fname,String lname,String phone,String password)
    {
        StudentObj studentReg=new StudentObj(email,fname,lname,phone,password);
        myRef.child("students").child(email).setValue(studentReg);
    }

    public DatabaseReference getStudentFromDB(String studentID)
    {
        return myRef.child("students").child(studentID);
    }


}