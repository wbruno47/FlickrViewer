package com.bruno.william.utils.classes;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by wbruno47 on 7/18/2014.
 */
public class BitmapLoader {

    public BitmapLoader(){

    }

    public void loadBitmap(Resources resources,
                           String url,
                           ImageView imageView,
                           String currentUrl) {

        if (cancelPotentialWork(url, imageView, currentUrl)) {
            final DownloadImageTask task = new DownloadImageTask(imageView);
            final DownloadBitmapDrawable asyncDrawable = new DownloadBitmapDrawable(resources, task);

            imageView.setImageDrawable(asyncDrawable);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }
    }

    /****************************
     *  Sees if the current task of downloading an image should be cancelled.
     *
     * @param downloadUrl
     * @param imageView
     * @param currentUrl
     * @return
     */
    public boolean cancelPotentialWork(String downloadUrl, ImageView imageView, String currentUrl) {
        ///Get DownloadTask Of ImageView
        final DownloadImageTask downloadImageTask = getDownloadImageTask(imageView);

        if (downloadImageTask != null) {
            if (!downloadUrl.equals(currentUrl)){
                downloadImageTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    /**
     * getDownLoadImageTask will get the task associated with the supplied Image View.
     * @param imageView - image view that needs to download an image  .
     * @return - download task associated with the supplied imageview.
     */
    private DownloadImageTask getDownloadImageTask(ImageView imageView) {
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
