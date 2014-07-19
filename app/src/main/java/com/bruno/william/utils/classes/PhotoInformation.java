package com.bruno.william.utils.classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wbruno47 on 7/17/2014.
 * PhotoInformation is a class that will store extra info on a photo.
 * Will have a photo as part of the class, and will also have username, and a list of comments.
 */
public class PhotoInformation implements Serializable {
    private Photo photo;            //Photo object that needs extra info...
    private String userName;        //user name
    private ArrayList<Comment> comments; //Comment list.

    public PhotoInformation(Photo photo){
        this.photo = photo;
        this.comments = new ArrayList<Comment>();
    }

    public Photo getPhoto(){
        return photo;
    }

    public String getUserId(){
        return photo.getUserId();
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    //Add a comment to the list of comment
    public void addComment(Comment comment){
        comments.add(comment);
    }

    public String getUserName(){
        return userName;
    }

    public ArrayList<Comment> getComments(){
        return comments;
    }
}
