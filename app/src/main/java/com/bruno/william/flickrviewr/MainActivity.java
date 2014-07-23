package com.bruno.william.flickrviewr;
/*****************************************************************************************
 * Main Activity
 Created by William Bruno on 7/17/2014.
 Last Update 07/17/2014

 Activity that is loaded when App is launched.
 Upon creating, adds a FlickrViewFragment.

 When an item is click from FlickrViewFragment, a PhotoDetailFragment is displayed to show
 the clicked photo's details...
 ******************************************************************************************/

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bruno.william.utils.classes.Photo;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxCache;
import self.philbrown.droidQuery.AjaxOptions;


public class MainActivity extends Activity {
    public static final long ANIMATION_DURATION_SHORT = 200;  //for the fade animations throughout the app
    public static final long ANIMATION_DURATION_LONG = 500;

    public static final int MENU_ITEM_REFRESH = 0;

    public final static String FLICKR_VIEWER_FRAGMENT_TAG = "flickr";
    public final static String DETAIL_FRAGMENT_TAG = "detail";   //Tag used when adding Detail Fragment to stack
    private PhotoDetailFragment photoDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Add Fragment to Activity.
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new FlickrViewerFragment(), FLICKR_VIEWER_FRAGMENT_TAG)
                    .commit();
        }

        $.ajaxSetup(new AjaxOptions().cache(true).cacheTimeout(AjaxCache.TIMEOUT_NEVER));
    }

    /**********************************************
     *  This function loads the details for the selected Photo
     *  Creates a new PhotoDetailFragment and passes along the selected Photo.
     *
     * @param photo - photo selected from FlickrViewFragment.
     *********************************************/
    public void loadDetailedInformation(Photo photo){
        Bundle bundle = new Bundle();
        bundle.putSerializable(PhotoDetailFragment.KEY_PHOTO, photo);

        photoDetailFragment = new PhotoDetailFragment();
        photoDetailFragment.setArguments(bundle);

        addDetailedInformation();
    }

    /***************************************************
     *  Adds the photoDetailFragment to the stack.
     ****************************************************/
    public void addDetailedInformation(){
        getFragmentManager().beginTransaction()
                .add(R.id.container, photoDetailFragment, DETAIL_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    /*************************************************************
     *  Capture Back Press
     *  If user is viewing a photoDetailFragment, just pop that from
     *  the stack to get back to the FlickrViewFragment
     **************************************************************/
    @Override
    public void onBackPressed() {
        PhotoDetailFragment photoDetailFragment = (PhotoDetailFragment) getFragmentManager()
                .findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (photoDetailFragment != null && photoDetailFragment.isVisible()){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ITEM_REFRESH, 0, R.string.refresh)
                .setIcon(getResources().getDrawable(R.drawable.ic_menu_refresh))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case MENU_ITEM_REFRESH:
                FlickrViewerFragment fragment = (FlickrViewerFragment) getFragmentManager().findFragmentByTag(FLICKR_VIEWER_FRAGMENT_TAG);
                fragment.refreshPhotoLoader();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
