package com.bruno.william.flickrviewr;
/******************************************************************************************
 FlickrViewerFragment
 Created by William Bruno on 7/17/2014.
 Last Update 07/18/2014

 This fragment displays a "set" of Flickr Photos.
 For this project I chose to display the search results for photos with the text "Android"

 Uses the class FlickPhotoLoaderHelper which gets the search results for "android" photos.
 and displays them into a gridview with a custom gridview adapter

 *******************************************************************************************/

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.bruno.william.utils.adapters.FlickrPhotoAdapter;
import com.bruno.william.utils.classes.FlickPhotoLoaderHelper;
import com.bruno.william.utils.classes.Photo;

public class FlickrViewerFragment extends Fragment {
    private FlickPhotoLoaderHelper flickrPhotoLoaderHelper;  //helper class to download photo info.
    private FlickrPhotoAdapter flickrPhotoAdapter; //gridview adapter

    private GridView mGridView;
    private Button mFooterButton;  //Button shows "more photos" when you scroll to the end of gridview.

    private Parcelable gridViewInfo; //stores GridView info on Screen Orientation switch, etc....

    //Empty Constructor.
    public FlickrViewerFragment(){

    }

    /**************
     *  Creates Fragment View
     * @param inflater - inflater
     * @param container - container
     * @param savedInstanceState - saved state
     * @return - view to be created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.flickr_viewer_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        this.setGridViews();                //find GridView in layout, set listeners to grid view.
        this.initiateFlickrPhotoLoader();   //create photo helper object and have it start to download photo infomration.
    }

    @Override
    public void onPause() {
        super.onPause();
        flickrPhotoLoaderHelper.dismissLoaderDialog(); //check if task is still loading and dismiss dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        gridViewInfo = mGridView.onSaveInstanceState(); //save state of grid view to keep user in same spot for screen changes, etc.
        //TODO save photo list so it doesn't refresh after orientation change
    }

    /*****************************************
     * Create the Flickr Photo Helper object
     * Create the Grid View Adapter to show flickr photos
     * Set adapter to Gridview.
     * Set interface so when the Task of downloading photo information is complete, the
     * Grid View adapter can be updated.
     *
     *
     *****************************************/
    public void initiateFlickrPhotoLoader() {
        flickrPhotoLoaderHelper = new FlickPhotoLoaderHelper(getActivity(), null);
        flickrPhotoAdapter = new FlickrPhotoAdapter(getActivity(), flickrPhotoLoaderHelper.getPhotoList());
        mGridView.setAdapter(flickrPhotoAdapter);

        flickrPhotoLoaderHelper.setPhotoSetDoneLoading(new FlickPhotoLoaderHelper.PhotoSetDoneLoading() {

            //Function called when the asynctask that gets photo information is complete.
            @Override
            public void onPhotoLoadingCompleted() {
                //When task is done, make sure to notify adapter that there are new object
                //in the Grid View
                flickrPhotoAdapter.notifyDataSetChanged();

                if (gridViewInfo != null) { //If there was a saved state for the grid view
                    //make sure to restore it so the user stays in same spot for screen changes, etc....
                    mGridView.onRestoreInstanceState(gridViewInfo);
                    gridViewInfo = null;
                }
            }
        });

        flickrPhotoLoaderHelper.loadInitialPhotoList(); //Loads first set of data
    }

    /*********************************************************************
     * Refresh Photo Loader
     * This function will refresh the photo list that is displayed.
     **********************************************************************/
    public void refreshPhotoLoader() {
        if (!flickrPhotoLoaderHelper.isLoading())  //If there is not a process to get images
            flickrPhotoLoaderHelper.refresh(); //then refresh the photo list.
    }

    /*************************************************************
     * Find the view within the fragment that will be used.
     */
    public void setGridViews(){

        mGridView = (GridView) getView().findViewById(R.id.photo_grid_view);
        //Footer Button will only be displayed when the Grid View is at the bottom of the list.
        mFooterButton = (Button) getView().findViewById(R.id.footer_button);
        mFooterButton.setVisibility(View.GONE);

        //Click Listener for Grid View.  When a photo is clicked display the "Photo Detail" fragment.
        //this will show a larger image, the username, and comments related to the photo.
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).loadDetailedInformation((Photo) flickrPhotoAdapter.getItem(position));
            }
        });

        //Scroll Listener will be set so when the Grid View reaches the bottom of the list
        //the footer button will come back into view.
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount
                        && totalItemCount != 0) {
                    // last item in grid is on the screen, show footer:
                    mFooterButton.setVisibility(View.VISIBLE);
                    mFooterButton.setAlpha(.9f);
                } else if (mFooterButton.getVisibility() != View.GONE) {
                    // last item in grid not on the screen, hide footer:
                    mFooterButton.setVisibility(View.GONE);
                }
            }
        });

        //Footer button adds mor photos to the Grid View.
        mFooterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flickrPhotoLoaderHelper.isLoading())
                    flickrPhotoLoaderHelper.loadNextPage();
            }
        });
    }
}
