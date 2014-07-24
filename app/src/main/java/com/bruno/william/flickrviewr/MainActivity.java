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
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;

import com.bruno.william.utils.classes.Photo;

import self.philbrown.droidQuery.$;
import self.philbrown.droidQuery.AjaxCache;
import self.philbrown.droidQuery.AjaxOptions;


public class MainActivity extends Activity {
    public static final long ANIMATION_DURATION_SHORT = 200;  //for the fade animations throughout the app
    public static final long ANIMATION_DURATION_LONG = 500;

    public static final int MENU_ITEM_REFRESH = 0;
    public static final int MENU_ITEM_SEARCH = 1;
    public static final int MENU_ITEM_RECENT_PHOTOS = 2;

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

    /**
     * *******************************************
     * This function loads the details for the selected Photo
     * Creates a new PhotoDetailFragment and passes along the selected Photo.
     *
     * @param photo - photo selected from FlickrViewFragment.
     *              *******************************************
     */
    public void loadDetailedInformation(Photo photo) {
        Bundle bundle = new Bundle();
        //bundle.putSerializable(PhotoDetailFragment.KEY_PHOTO, photo);
        bundle.putParcelable(PhotoDetailFragment.KEY_PHOTO, photo);

        photoDetailFragment = new PhotoDetailFragment();
        photoDetailFragment.setArguments(bundle);

        addDetailedInformation();
    }

    public String getSearchableStringForFlickr(String submittedText) {
        submittedText = submittedText.trim(); //get rid of white space before and after.
        submittedText = submittedText.replaceAll("\\s+", " "); //replace grouped whitespaces with single space
        submittedText = submittedText.replace(' ', '+'); //replace single space
        return submittedText;
    }

    /**
     * ************************************************
     * Adds the photoDetailFragment to the stack.
     * **************************************************
     */
    public void addDetailedInformation() {
        getFragmentManager().beginTransaction()
                .add(R.id.container, photoDetailFragment, DETAIL_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    /**
     * **********************************************************
     * Capture Back Press
     * If user is viewing a photoDetailFragment, just pop that from
     * the stack to get back to the FlickrViewFragment
     * ************************************************************
     */
    @Override
    public void onBackPressed() {
        if (!popPhotoDetailFragment())
            super.onBackPressed();
    }

    public boolean popPhotoDetailFragment(){
        PhotoDetailFragment photoDetailFragment = (PhotoDetailFragment) getFragmentManager()
                .findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (photoDetailFragment != null && photoDetailFragment.isVisible()) {
            getFragmentManager().popBackStack();
            return true;
        }

        return false;
    }
    private InputFilter alphaNumericFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.isWhitespace(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };

    public SearchView getSearchView() {
        final SearchView searchView = new SearchView(getActionBar().getThemedContext());
        searchView.setQueryHint("Search for Photos…");
        searchView.setIconified(true);

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText editText = (EditText) searchView.findViewById(id);
        editText.setFilters(new InputFilter[]{alphaNumericFilter});

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                popPhotoDetailFragment();  //check to dismiss Detail Fragment

                FlickrViewerFragment fragment = (FlickrViewerFragment) getFragmentManager().findFragmentByTag(FLICKR_VIEWER_FRAGMENT_TAG);
                fragment.searchPhotos(getSearchableStringForFlickr(query));

                searchView.clearFocus(); //Dismiss keyboard
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return searchView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, MENU_ITEM_REFRESH, 0, R.string.refresh)
                .setIcon(getResources().getDrawable(R.drawable.ic_menu_refresh))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(0, MENU_ITEM_SEARCH, 0, R.string.search)
                .setIcon(getResources().getDrawable(android.R.drawable.ic_menu_search))
                .setActionView(getSearchView())
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        menu.add(0, MENU_ITEM_RECENT_PHOTOS, 0, getString(R.string.recent_photos));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_REFRESH:
                FlickrViewerFragment fragment = (FlickrViewerFragment) getFragmentManager().findFragmentByTag(FLICKR_VIEWER_FRAGMENT_TAG);
                fragment.refreshPhotoLoader();
                break;
            case MENU_ITEM_RECENT_PHOTOS:
                popPhotoDetailFragment();
                FlickrViewerFragment fragmentRecentPhotos = (FlickrViewerFragment) getFragmentManager().findFragmentByTag(FLICKR_VIEWER_FRAGMENT_TAG);
                fragmentRecentPhotos.searchPhotos(null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
