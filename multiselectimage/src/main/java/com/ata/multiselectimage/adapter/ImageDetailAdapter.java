package com.ata.multiselectimage.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by bobo on 2017/12/19.
 */

public class ImageDetailAdapter extends PagerAdapter {
    public Context mContext;
    public ArrayList<String> list;
    public ImageDetailAdapter(Context mContext,ArrayList<String> list){
        this.mContext=mContext;
        this.list=list;
    }

    @Override
    public int getCount() {

        return list.size()==0?0:list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView=new PhotoView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        photoView.setLayoutParams(params);
        Glide.with(mContext).load(list.get(position)).into(photoView);
        container.addView(photoView);
        return photoView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
