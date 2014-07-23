package com.bruno.william.utils.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wbruno47 on 7/16/2014.
 * Photo Class holds all the relevant data associated with a flickr photo.
 * Will have two images: thumbnail and detail
 * Thumbnail is displayed in gridview (main activity)
 * Detail is larger image used when viewing photo details.
 */
public class Photo implements Parcelable {
    private String id;      //photo id
    private String userId;  //user id (of author)

    private String secret;  //photo secret
    private String server;  //photo server      these three are used to make image url's (along with photo id);
    private String farm;    //photo farm

    private String thumbnailUrl;
    private String detailImageUrl;

    public Photo() {

    }

    public Photo(String newId, String newUserId) {
        id = newId;
        userId = newUserId;
    }

    //Constructor to read parcel
    public Photo(Parcel in) {
        id = in.readString();
        userId = in.readString();

        secret = in.readString();
        server = in.readString();
        farm = in.readString();

        thumbnailUrl = in.readString();
        detailImageUrl = in.readString();
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDetailImageUrl() {
        return detailImageUrl;
    }

    public void setId(String newId) {
        id = newId;
    }

    public void setUserId(String newUserId) {
        userId = newUserId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public void setDetailUrl(){
        //_q for a larger square image
        detailImageUrl = "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + "_q.jpg";
    }
    public void setThumbnailUrl() {
        //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
        //_s == thumbnail;
        thumbnailUrl = "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + "_s.jpg";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(secret);
        dest.writeString(server);
        dest.writeString(farm);
        dest.writeString(thumbnailUrl);
        dest.writeString(detailImageUrl);
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>()
    {

        /** Construct and return Photo from a Parcel*/
        @Override
        public Photo createFromParcel(Parcel in)
        {
            return new Photo(in);
        }

        /**
         * Creates a new array of Photos
         */
        @Override
        public Photo[] newArray(int size)
        {
            return new Photo[size];
        }
    };

}
