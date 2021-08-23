package com.example.arielcast.firebase.model.dataObject;

public class Comment {
    String commentId;
    String commentText;
    String date;
    String fullName;
    String userId;


    public Comment() {
        this.commentId = "";
        this.commentText = "";
        this.date = "";
        this.fullName = "";
        this.userId="";
    }

    public Comment(String commentId, String commentText, String date, String fullName,String userId) {
        this.commentId=commentId;
        this.commentText=commentText;
        this.date=date;
        this.fullName=fullName;
        this.userId=userId;
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

    public String getUserId() {
        return userId;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
