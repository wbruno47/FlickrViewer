package com.bruno.william.utils;

/**
 * Created by wbruno47 on 7/16/2014.
 * Constants will hold all information relevant to making api calls
 * and parsing data retrieved from api calls.
 */
public class FlickrConstants {
    public final static String API_KEY = "47a3c2208768b8b89346e7d8770a1664";
    public final static String apiFormat = "json&nojsoncallback=1";
    public final static String searchTag = "android";

    public final static String searchPerPageCount = "200";

    //apiUrl.....
    //These Strings are used to make calls to Flickr to get an appropriate JSON object
    //Make Search Query
    //Get Recent Photos
    //Get User Information
    //Get Photo Comments
    //Built to take in arguments so when using these strings, must call String.Format()

    //1= api 2 = search text 3 = format 4 = per page 5 = page number
    public final static String apiUrlGetSearchPhotos =
           "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=%s&text=%s&format=%s&per_page=%s&page=%s&sort=relevance&safe_search=1";
    //public final static String apiUrlGetRecentPhotos =
     //       "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=%s&format=%s&per_page=%s";//14487514250

    //public final static String apiUrlGetPhotoSizes =
    //        "https://api.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=%s&photo_id=%s&format=%s";
    public final static String apiUrlGetUserInformation =
            "https://api.flickr.com/services/rest/?method=flickr.people.getInfo&api_key=%s&user_id=%s&format=%s";
    public final static String apiUrlGetPhotoComments =
            "https://api.flickr.com/services/rest/?method=flickr.photos.comments.getList&api_key=%s&photo_id=%s&format=%s";

    //Constants used for parsing JSON object sent back from getting Recent Photos......
    public static final String KEY_PHOTO_ID = "id";
    public static final String KEY_USER_ID = "owner";
    public static final String KEY_PHOTO_OBJECT = "photos";
    public static final String KEY_PHOTO_ARRAY = "photo";
    public static final String KEY_SECRET = "secret";
    public static final String KEY_FARM = "farm";
    public static final String KEY_SERVER = "server";

    //Constants used for parsing JSON object sent back from getting User Information.....
    public static final String KEY_USER_OBJECT = "person";
    public static final String KEY_USER_NAME = "username";

    //Constants used for parsing JSON object sent back from getting comments......
    public static final String KEY_COMMENT_OBJECT = "comments";
    public static final String KEY_COMMENT_ARRAY = "comment";
    public static final String KEY_COMMENT_AUTHOR = "authorname";

    //for getting user icon in comments...
    public static final String KEY_AUTHOR_ID = "author";
    public static final String KEY_ICON_SERVER = "iconserver";
    public static final String KEY_ICON_FARM = "iconfarm";

    public static final String KEY_CONTENT = "_content"; //used for user name and comments....

}
