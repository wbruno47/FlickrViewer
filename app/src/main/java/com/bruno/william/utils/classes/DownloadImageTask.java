package com.bruno.william.utils.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

import self.philbrown.droidQuery.AjaxCache;
import self.philbrown.droidQuery.AjaxOptions;

/**
 * Created by wbruno47 on 7/18/2014.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewWeakReference;

    public DownloadImageTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    //Param[0] is the url for the image to download.
    @Override
    protected Bitmap doInBackground(String... params) {
        return getBitmapFromUrl(params[0]);
    }

    /** The URL used to download the image. */
    private String url;

    /****************
     * Download Bitmap
     * @param url - url to download bitmap from
     * @return - Bitmap
     */
    public Bitmap getBitmapFromUrl(String url) {
        this.url = url;
        Bitmap bmp = (Bitmap) AjaxCache.sharedCache().getCachedResponse(new AjaxOptions().url(url));
        if (bmp != null)
            return bmp;
        InputStream inputStream;
        try {
            inputStream = new URL(url).openStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (isCancelled()) { //if cancelled do nothing.
            return;
        }

        //if not cancelled and the reference isn't null..
        //then set the bitmap to the imageview.
        if (imageViewWeakReference != null && bitmap != null) {
                final ImageView imageView = imageViewWeakReference.get();  //get imageview from reference.
                final DownloadImageTask downloadImageTask = getDownloadImageTask(imageView); //get the download task from the image view.
                if (this == downloadImageTask && imageView != null) {  //if this is the current task then set the downloaded image.
                    imageView.setImageBitmap(bitmap);
                    AjaxCache.sharedCache().cacheResponse(bitmap, new AjaxOptions().url(url));
                }
            }
        }


    private static DownloadImageTask getDownloadImageTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadBitmapDrawable) {
                final DownloadBitmapDrawable asyncDrawable = (DownloadBitmapDrawable) drawable;
                return asyncDrawable.getDownloadImageTask();
            }
        }
        return null;
    }
}
