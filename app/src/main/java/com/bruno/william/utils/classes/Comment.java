package com.bruno.william.utils.classes;

import java.io.Serializable;

/**
 * Comment Class holds Comment, the author of the comment, and the author's icon.
 * Created by wbruno47 on 7/17/2014.
 */
public class Comment implements Serializable {
    private String iconUrl;
    private String authorName;
    private String comment;

    public Comment(){

    }

    //Create the icon url for the comment given the comments information.
    //Based on documentation from flick api.
    public void setIconUrl(String farm, String server, String authorId){
        //http://farm{icon-farm}.staticflickr.com/{icon-server}/buddyicons/{nsid}.jpg
        if (farm.equals("0"))
            iconUrl = "https://www.flickr.com/images/buddyicon.gif";
        else
            iconUrl = "http://farm"+farm+".staticflickr.com/"+server+"/buddyicons/"+authorId+".jpg";
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getComment() {
        return comment;
    }

}
