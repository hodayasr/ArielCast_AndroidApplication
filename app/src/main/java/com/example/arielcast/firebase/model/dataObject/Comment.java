package com.example.arielcast.firebase.model.dataObject;

public class Comment {
    String commentId;
    String commentText;
    String date;
    String fullName;


    public Comment() {
        this.commentId = "";
        this.commentText = "";
        this.date = "";
        this.fullName = "";
    }

    public Comment(String commentId, String commentText, String date, String fullName) {
        this.commentId=commentId;
        this.commentText=commentText;
        this.date=date;
        this.fullName=fullName;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getDate() {
        return date;
    }

    public String getFullName() {
        return fullName;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
