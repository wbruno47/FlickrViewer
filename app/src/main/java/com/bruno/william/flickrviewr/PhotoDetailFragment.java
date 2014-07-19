package com.bruno.william.flickrviewr;


/******************************************************************************************
 PhotoViewerFragment
 Created by William Bruno on 7/17/2014.
 Last Update 07/18/2014

 This fragment is passed a Photo Object
 Will take that object and get the associated Username and Comments List for that Fragment
 ******************************************************************************************/

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bruno.william.utils.FlickrConstants;
import com.bruno.william.utils.adapters.CommentAdapter;
import com.bruno.william.utils.classes.BitmapLoader;
import com.bruno.william.utils.classes.Comment;
import com.bruno.william.utils.classes.JSONParser;
import com.bruno.william.utils.classes.Photo;
import com.bruno.william.utils.classes.PhotoInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PhotoDetailFragment extends Fragment {
    public static final String KEY_PHOTO = "photo";  //Key used for passing the Photo object to this fragment.

    private BitmapLoader bitmapLoader;              //Helper Object to load the image that was selected and user icons for comments.
    private PhotoInformation photoInformation;      //Object that holds the photo given, and the details that are needed in this fragment for that photo.

    private ImageView imageView;                    //Displays a larger image of the selected image.
    private TextView userName;                      //user name
    private TextView emptyTextView;                 //if there are not comments to display
    private ListView commentListView;
    private CommentAdapter commentAdapter;
    private ProgressBar progressBar;                //displays when comments are being downloaded.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo_details_fragment, container, false);  //load fragment layout
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bitmapLoader = new BitmapLoader();

        loadPhotoDetails(); //start downloading details about the photo...right now this just gets the user name.
        initiateViews(); //Once Activity is created, initiate Views that will be used throughout the fragment.
        setViewInformation(); //then set each of those views with the proper data.

        this.setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable("info", photoInformation);
    }

    /******************************
     * Find the view that will be used throughout this fragment
     *******************************/
    public void initiateViews() {
        imageView = (ImageView) getView().findViewById(R.id.detail_image);
        userName = (TextView) getView().findViewById(R.id.detail_user_name);
        commentListView = (ListView) getView().findViewById(R.id.detail_list_comments);

        emptyTextView = (TextView) getView().findViewById(R.id.empty_text);
        emptyTextView.setVisibility(View.GONE);

        commentListView.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) getView().findViewById(R.id.comment_progress_bar);

        View root = getView().findViewById(R.id.detail_root);
        //Find the root override OnTouchListener.
        //This prevents fragments that are beneath this fragment to respond to OnTouch's
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /***************************************************
     *  SetViewInformation creates the adapter for the Comments List
     *  Starts the comment loading task
     *  And starts downloading the larger photo of the photo selected
     **************************************************/
    public void setViewInformation() {
        commentAdapter = new CommentAdapter(getActivity(), R.layout.photo_detail_comment_layout, photoInformation.getComments());
        commentListView.setAdapter(commentAdapter);

        LoadCommentsTask commentsTask = new LoadCommentsTask();
        commentsTask.execute();

        //LoadBitmap = send Photo url, imageView, and "Current Url"
        //current url is used when this helper is loading images in listviews, etc.
        //send photoUrl for both parameters, since they match the image will be downloaded.
        bitmapLoader.loadBitmap(getResources(), photoInformation.getPhoto().getDetailImageUrl(),
                imageView, photoInformation.getPhoto().getDetailImageUrl());
    }

    /************************************************************************
     *  LoadPhotoDetails will get the Photo that was passed to this fragment
     *  Create a photoInformation object for that Photo and starts the LoadDetailTask
     *  Again, right now LoadDetailTask primarily just finds the user name.
     ************************************************************************/
    public void loadPhotoDetails() {
        Photo photo = (Photo) getArguments().getSerializable(KEY_PHOTO);
        photoInformation = new PhotoInformation(photo);
        LoadDetailedInformationTask task = new LoadDetailedInformationTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null); //run tasks parallel
    }


    /*****************************************************************
     * LoadCommentTask downloads all the comments for the Photo Passed to this fragment.
     *
     *************************************************************************************/
    private class LoadCommentsTask extends AsyncTask<Void, Void, Void> {

        //Before loading comments ensure progress bar is visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        //After loading, hide progress bar
        //Show either List or Empty List Text
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);

            //Set my own Empty Text View listener
            if (commentAdapter.getCount() == 0){
                commentListView.setVisibility(View.INVISIBLE);
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                emptyTextView.setVisibility(View.INVISIBLE);
                commentListView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Get JSON of Comments and parse through to get content.
            JSONParser jsonParser = new JSONParser();
            JSONObject commentObject = jsonParser.get(
                    String.format(FlickrConstants.apiUrlGetPhotoComments,
                            FlickrConstants.API_KEY,
                            photoInformation.getPhoto().getId(),
                            FlickrConstants.apiFormat));

            JSONObject comments;

            try {
                //get the Comment object within the result.
                comments = commentObject.getJSONObject(FlickrConstants.KEY_COMMENT_OBJECT);
                //get the comment array that is part of the just made comment object.
                JSONArray commentArray = comments.getJSONArray(FlickrConstants.KEY_COMMENT_ARRAY);

                //set progress bar to match how many comments there are.
                progressBar.setMax(commentArray.length());
                progressBar.setProgress(0);

                Comment comment;        //comment object to add to list
                JSONObject jsonComment; //json object retrieved from commentArray;

                for (int i = 0; i < commentArray.length(); i++) {
                    jsonComment = commentArray.getJSONObject(i);
                    comment = new Comment();

                    //Gather the information for the new Comment.
                    comment.setAuthorName(jsonComment.getString(FlickrConstants.KEY_COMMENT_AUTHOR));
                    comment.setComment(jsonComment.getString(FlickrConstants.KEY_CONTENT));
                    comment.setComment(Html.fromHtml(comment.getComment()).toString());
                    comment.setIconUrl(jsonComment.getString(FlickrConstants.KEY_ICON_FARM),
                            jsonComment.getString(FlickrConstants.KEY_ICON_SERVER),
                            jsonComment.getString(FlickrConstants.KEY_AUTHOR_ID));

                    //Add that comment to the photoInformation object
                    photoInformation.addComment(comment);
                    publishProgress(); //show the user that comments are being downloaded..
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(progressBar.getProgress() + 1);
        }
    }

    /******
     * LoadDetailedInformationTask is a background task that will download
     * extra information about the photo passed.
     * In this example it will get the username for the photo.
     */
    private class LoadDetailedInformationTask extends AsyncTask<Void, Void, Void> {

        //When username is found, set it to the photoInformation object
        //Then fade the username into view..
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            userName.clearAnimation();
            userName.setText(photoInformation.getUserName());

            ObjectAnimator animator = ObjectAnimator.ofFloat(userName, View.ALPHA, 0, 1);
            animator.setDuration(MainActivity.ANIMATION_DURATION_LONG);
            animator.start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jsonParser = new JSONParser();
            JSONObject userObject = jsonParser.get(
                    String.format(FlickrConstants.apiUrlGetUserInformation, FlickrConstants.API_KEY, photoInformation.getUserId(), FlickrConstants.apiFormat)
            );

            try {
                //Set the user name.
                JSONObject user = userObject.getJSONObject(FlickrConstants.KEY_USER_OBJECT);
                JSONObject userName = user.getJSONObject(FlickrConstants.KEY_USER_NAME);
                photoInformation.setUserName(userName.getString(FlickrConstants.KEY_CONTENT));
                //Log.d("Flick", "username = " + photoInformation.getUserName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
