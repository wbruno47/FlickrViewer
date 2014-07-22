package com.bruno.william.utils.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bruno.william.utils.FlickrConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by William Bruno on 7/16/2014.
 * FlickPhotoLoaderHelper is used to gather Photo Information from Flickr to be
 * displayed in a Grid View
 */
public class FlickPhotoLoaderHelper {
    private Context mContext;

    private ArrayList<Photo> photoList;     //List of Photos to be displayed
    private PhotoSetDoneLoading photoSetDoneLoading; //Interface used when photo download task is complete.
    private int lastPageLoaded = 1; //Tracker to keep track of the last page that was loaded.

    private JSONParser jsonParser;
    private PhotoLoadTask photoLoadTask;  //task to download and get photo information.

    private String searchText;

    public FlickPhotoLoaderHelper(Context context, String searchText) {
        mContext = context;
        photoList = new ArrayList<Photo>();
        jsonParser = new JSONParser();
        this.searchText = searchText;
    }

    /**
     * Search for the given text
     * @param text  the text to do a new search for. If {@code null}, clear the search.
     */
    public void search(String text) {
        //TODO
    }

    /**
     * Loads new content
     */
    public void refresh() {
        //TODO
    }

    /**
     * ***********
     * Function to see if current Photo Download Task is running.
     *
     * @return - if the AsyncTask is Running.
     */
    public boolean isLoading() {
        if (photoLoadTask == null)
            return false;
        else {
            if (photoLoadTask.getStatus() == AsyncTask.Status.RUNNING)
                return true;
        }
        return false;
    }

    /**
     * LoadInitialPhoto list gets the first set of Photo from flickr.
     */
    public void loadInitialPhotoList() {
        photoLoadTask = new PhotoLoadTask(true, searchText);
        photoLoadTask.execute();
    }

    /**
     * ***
     * Load next Page will get the next set of data from Flickr and add it to the list.
     */
    public void loadNextPage() {
        lastPageLoaded++;
        photoLoadTask = new PhotoLoadTask(false, searchText);
        photoLoadTask.execute();
    }

    public ArrayList<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoSetDoneLoading(PhotoSetDoneLoading doneLoading) {
        this.photoSetDoneLoading = doneLoading;
    }

    public void dismissLoaderDialog(){
        if (photoLoadTask != null){
            photoLoadTask.dismissDialog();
        }
    }

    /**
     * *****************
     * PhotoLoadTask gets a json object from Flickr, parses through it to gather the
     * info on the photos that are to be displayed.
     */
    private class PhotoLoadTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;  //progress bar.
        private boolean showProgress;           //boolean to show progress...progress is shown on initial load, not on 'load next page"
        private String apiUrlGetPhotos;         //the string used to get photo data.
        private String searchText;

        /**
         * ???
         * @param show          ???
         * @param searchText    The text to search for, or {@code null} for no search filter.
         */
        public PhotoLoadTask(boolean show, String searchText) {  //send boolean to determine whether or not to show progress.
            showProgress = show;
            this.searchText = searchText;
        }

        /**
         * **
         * On Post:
         * if dialog is displayed, dismiss it.
         * if interface is set, call interface function.
         * set global photoLoadTask object to null.
         *
         * @param aVoid - void
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissDialog();
            if (photoSetDoneLoading != null) {
                photoSetDoneLoading.onPhotoLoadingCompleted();
            }

            photoLoadTask = null;
        }

        /**
         * Set up progress dialog if it is to be shown
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Flick", "Pre photo.");
            if (showProgress) {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Loading Flickr Images...");
                progressDialog.show();
            } else {
                progressDialog = null;
            }

            //Make the url to get the json object for photos
            //this is the order for making the string url...
            //1= api 2 = search text 3 = format 4 = per page 5 = page number
            apiUrlGetPhotos =
                    String.format(FlickrConstants.apiUrlGetSearchPhotos,
                            FlickrConstants.API_KEY,
                            (searchText == null ? "" : ("&text=" + searchText)),
                            FlickrConstants.apiFormat,
                            FlickrConstants.searchPerPageCount,
                            lastPageLoaded);
            // String.format(FlickrConstants.apiUrlGetRecentPhotos, FlickrConstants.API_KEY, FlickrConstants.apiFormat, FlickrConstants.searchPerPageCount));

        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonObject = jsonParser.get(apiUrlGetPhotos);

            try {
                //get the array of Photos....
                JSONArray photoArray = jsonObject.getJSONObject(FlickrConstants.KEY_PHOTO_OBJECT)
                        .getJSONArray(FlickrConstants.KEY_PHOTO_ARRAY);

                JSONObject photoObject;  //single Photo within the above JSON Array
                Photo flickrPhoto;       //Object to hold photo information.

                for (int i = 0; i < photoArray.length(); i++) {
                    photoObject = photoArray.getJSONObject(i); //get the next Photo
                    flickrPhoto = new Photo();                  //create information holder for that Photo.....

                    //Gather needed photo information;
                    flickrPhoto.setId(photoObject.getString(FlickrConstants.KEY_PHOTO_ID)); //photo_id
                    flickrPhoto.setUserId(photoObject.getString(FlickrConstants.KEY_USER_ID)); //user id
                    flickrPhoto.setFarm(photoObject.getString(FlickrConstants.KEY_FARM));
                    flickrPhoto.setSecret(photoObject.getString(FlickrConstants.KEY_SECRET));
                    flickrPhoto.setServer(photoObject.getString(FlickrConstants.KEY_SERVER));
                    //create bitmap urls from gathered information....
                    flickrPhoto.setThumbnailUrl();
                    flickrPhoto.setDetailUrl();
                    photoList.add(flickrPhoto);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void dismissDialog(){
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

        }
    }

    //Used so calling activity/fragment can know when download task is complete.
    public interface PhotoSetDoneLoading {
        public void onPhotoLoadingCompleted();
    }
}
