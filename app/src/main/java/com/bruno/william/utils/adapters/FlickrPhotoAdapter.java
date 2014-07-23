package com.bruno.william.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bruno.william.flickrviewr.R;
import com.bruno.william.utils.classes.BitmapLoader;
import com.bruno.william.utils.classes.Photo;
import com.bruno.william.utils.holders.ImageHolder;

import java.util.ArrayList;

/*******************************************************
 * FlickrPhotoAdapter
 * Created by William Bruno on 7/17/2014.
 * Last update 7/18/2014
 *
 * Adapter to Display Photos in the GridView in FlickrViewerFragment
 *******************************************************/
public class FlickrPhotoAdapter extends BaseAdapter {
    private BitmapLoader bitmapLoader;   //Loader class.  Will stop downloading images that are scrolled out of view in the list view.
    private Context mContext;
    private ArrayList<Photo> items;       //List of Photos to display

    public FlickrPhotoAdapter(Context mContext, ArrayList<Photo> items) {
        this.mContext = mContext;
        this.items = items;
        this.bitmapLoader = new BitmapLoader();
    }

    @Override
    public int getCount() {
       return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageHolder holder;                       //Image Holder.
        //Photo photo = items.getPhoto(position); //Photo that is to be displayed.
        Photo photo = items.get(position);

        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.flickr_image_holder, null);

            holder = new ImageHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.url = photo.getThumbnailUrl();
            convertView.setTag(holder);

        } else {
            holder = (ImageHolder) convertView.getTag();

            //if holder url and the photo to be displayed url are the same.....
            //Then either photo is displayed, or the photo is being downloaded to be displayed
            //either way nothing else needs to be done so return current view.
            if (holder.url.equals(photo.getThumbnailUrl()))
                return convertView;
        }

        //Load Bitmap with current photo url and current holder url
        //loadBitmap will cancel any task running for this imageview when the 2 urls don't match
        bitmapLoader.loadBitmap(mContext.getResources(), photo.getThumbnailUrl(), holder.imageView, holder.url);
        holder.url = photo.getThumbnailUrl(); //update url
        return convertView;
    }
}

