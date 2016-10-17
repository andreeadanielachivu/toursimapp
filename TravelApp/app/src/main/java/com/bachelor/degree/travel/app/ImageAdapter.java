package com.bachelor.degree.travel.app;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreea on 6/18/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<ClipData.Item> items = new ArrayList<ClipData.Item>();
    private LayoutInflater inflater;
    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.satumare, R.drawable.maramures,
            R.drawable.suceava, R.drawable.botosani,
            R.drawable.bihor, R.drawable.salaj,
            R.drawable.cluj, R.drawable.bistrita,
            R.drawable.neamt, R.drawable.iasi,
            R.drawable.arad, R.drawable.alba,
            R.drawable.mures, R.drawable.harghita,
            R.drawable.bacau, R.drawable.vaslui,
            R.drawable.timis, R.drawable.hunedoar,
            R.drawable.alba, R.drawable.sibiu,
            R.drawable.brasov, R.drawable.covasna,
            R.drawable.vrancea, R.drawable.galati
            /*R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7*/
    };
}
