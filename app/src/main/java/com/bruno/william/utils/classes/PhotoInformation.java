package com.bruno.william.utils.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wbruno47 on 7/17/2014.
 * PhotoInformation is a class that will store extra info on a photo.
 * Will have a photo as part of the class, and will also have username, and a list of comments.
 */
public class PhotoInformation implements Parcelable {
    private Photo photo;            //Photo object that needs extra info...
    private String userName;        //user name
    private ArrayList<Comment> comments; //Comment list.

    public PhotoInformation(Photo photo){
        this.photo = photo;
        this.comments = new ArrayList<Comment>();
    }

    public PhotoInformation(Parcel in){
        if (comments == null){
            comments = new ArrayList<Comment>();
        } else {
            comments.clear();
        }

        photo = Photo.CREATOR.createFromParcel(in);
        userName = in.readString();
        in.readTypedList(comments, Comment.CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        photo.writeToParcel(dest, flags);
        dest.writeString(userName);
        dest.writeTypedList(comments);
    }


    public static final Parcelable.Creator<PhotoInformation> CREATOR = new Parcelable.Creator<PhotoInformation>()
    {

        /** Construct and return PhotoInformation from a Parcel*/
        @Override
        public PhotoInformation createFromParcel(Parcel in)
        {
            return new PhotoInformation(in);
        }

        /**
         * Creates a new array of PhotoInformation
         */
        @Override
        public PhotoInformation[] newArray(int size)
        {
            return new PhotoInformation[size];
        }
    };
}
