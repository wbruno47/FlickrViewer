package com.bruno.william.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bruno.william.flickrviewr.R;
import com.bruno.william.utils.classes.BitmapLoader;
import com.bruno.william.utils.classes.Comment;
import com.bruno.william.utils.holders.CommentHolder;

import java.util.ArrayList;

/**
 * CommentAdapter
 * Created by William Bruno on 7/17/2014.
 * Last Update on 07/18/2014
 *
 * Custom Adapter to display Comments in the DetailPhotoFragment.
 */
public class CommentAdapter extends ArrayAdapter<Comment> {

    private Context mContext;
    private ArrayList<Comment> items;   //Array of Comments
    private BitmapLoader bitmapLoader;  //loader for Icon Images..

    //Constructor initialize bitmap loader
    public CommentAdapter(Context context, int resource, ArrayList<Comment> itemSet) {
        super(context, resource);
        mContext = context;
        items = itemSet;
        bitmapLoader = new BitmapLoader();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentHolder holder;  //View Holder for comment list.

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.photo_detail_comment_layout, null);
            holder = new CommentHolder();

            //set holder views.
            holder.imageView = (ImageView) convertView.findViewById(R.id.comment_user_icon);
            holder.userName = (TextView) convertView.findViewById(R.id.comment_user_name);
            holder.comment = (TextView) convertView.findViewById(R.id.comment_content);

            convertView.setTag(holder);
        } else {
            holder = (CommentHolder) convertView.getTag();
        }

        Comment comment = items.get(position);

        if (comment != null){
            //set Comment information to list view.
            holder.userName.setText(comment.getAuthorName());
            holder.comment.setText(comment.getComment());

            //Load icon image. Send both icon url and holder url
            //if they don't match then any task for downloading an icon will be cancelled.
            bitmapLoader.loadBitmap(mContext.getResources(),
                    comment.getIconUrl(), holder.imageView, holder.url);
            holder.url = comment.getIconUrl(); //update holder url.
        }

        return convertView;
    }
}
