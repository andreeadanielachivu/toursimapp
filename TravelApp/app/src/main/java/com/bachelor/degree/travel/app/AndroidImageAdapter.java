package com.bachelor.degree.travel.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Andreea on 8/23/2016.
 */
public class AndroidImageAdapter extends PagerAdapter {
    Context mContext;
    List<Bitmap> bitmapsList;

    AndroidImageAdapter(Context context, List<Bitmap> bitmaps) {
        this.mContext = context;
        this.bitmapsList = bitmaps;
    }

    @Override
    public int getCount() {
        return this.bitmapsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageBitmap(bitmapsList.get(i));
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }
}
