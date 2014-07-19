package com.bruno.william.utils.classes;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by William Bruno on 7/18/2014.
 *
 * Drawable that holds a reference to the DownloadTask that will be
 * retrieving the bitmap for the drawable.
 *
 * The reference is used in determining if the task will need to be cancelled or not.
 */
public class DownloadBitmapDrawable extends BitmapDrawable {
    private final WeakReference<DownloadImageTask> downloadImageTaskWeakReference;

    public DownloadBitmapDrawable(Resources res,
                         DownloadImageTask bitmapWorkerTask) {
        super(res);
        downloadImageTaskWeakReference =
                new WeakReference<DownloadImageTask>(bitmapWorkerTask);
    }

    public DownloadImageTask getDownloadImageTask() {
        return downloadImageTaskWeakReference.get();
    }
}
