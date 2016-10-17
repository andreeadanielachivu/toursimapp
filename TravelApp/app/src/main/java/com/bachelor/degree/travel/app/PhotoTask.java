package com.bachelor.degree.travel.app;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreea on 8/23/2016.
 */
abstract class PhotoTask extends AsyncTask<String, Void, List<PhotoTask.AttributedPhoto>> {
    private int mHeight;

    private int mWidth;

    private GoogleApiClient mGoogleApiClient;
    private List<AttributedPhoto> list = new ArrayList<AttributedPhoto>();

    public PhotoTask(int width, int height, GoogleApiClient googleApiClient) {
        mHeight = height;
        mWidth = width;
        mGoogleApiClient = googleApiClient;
    }

    /**
     * Loads the first photo for a place id from the Geo Data API.
     * The place id must be the first (and only) parameter.
     */
    @Override
    protected List<AttributedPhoto> doInBackground(String... params) {
        if (params.length != 1) {
            return null;
        }
        final String placeId = params[0];
        AttributedPhoto attributedPhoto = null;

        if (mGoogleApiClient != null) {
            System.out.println("Isn't null");
        }
        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();
        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            int min = Math.min(10, photoMetadataBuffer.getCount());
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                // Get the first bitmap and its attributions.
                for (int i = 0; i < min; i++) {
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(i);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();
                    attributedPhoto = new AttributedPhoto(attribution, image);
                    list.add(attributedPhoto);
                }
            }
            // Release the PlacePhotoMetadataBuffer.
            photoMetadataBuffer.release();
        }
        return list;
    }

    /**
     * Holder for an image and its attribution.
     */
    class AttributedPhoto {

        public final CharSequence attribution;

        public final Bitmap bitmap;

        public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
            this.attribution = attribution;
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }
    }
}

