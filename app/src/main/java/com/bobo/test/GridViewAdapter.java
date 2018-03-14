package com.bobo.test;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bobo on 2017/12/18.
 */

public class GridViewAdapter extends BaseAdapter {
    public Context mContext;
    public ArrayList<String> list;
    public LayoutInflater inflater;
    final int mGridWidth;
    public GridViewAdapter(Context context, ArrayList<String> list, int column){
        this.mContext=context;
        this.list=list;
        inflater=LayoutInflater.from(mContext);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }else{
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / column;
    }
    @Override
    public int getCount() {
        return list.size()==0?0:list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(view==null){
            view=inflater.inflate(R.layout.item_pic_show,null);
            viewHolder=new ViewHolder();
            viewHolder.iv_show=view.findViewById(R.id.iv_show);
            viewHolder.fl_video_sign=view.findViewById(R.id.fl_video_sign);
            ViewGroup.LayoutParams layoutParams = viewHolder.iv_show.getLayoutParams();
            layoutParams.height = mGridWidth;
            viewHolder.iv_show.setLayoutParams(layoutParams);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        /*Picasso.with(mContext).load(new File(list.get(i))).placeholder(com.ata.multiselectimage.R.drawable.mis_default_error)
                .tag(MultiImageSelectorFragment.TAG)
                .resize(mGridWidth, mGridWidth)
                .centerCrop().into(viewHolder.iv_show);*/
        if(list.get(i).endsWith("3gp")||list.get(i).endsWith("mp4")){
            viewHolder.fl_video_sign.setVisibility(View.VISIBLE);
        }else {
            viewHolder.fl_video_sign.setVisibility(View.GONE);
        }
        Glide.with(mContext).load(new File(list.get(i))).override(mGridWidth,mGridWidth).placeholder(com.ata.multiselectimage.R.drawable.mis_default_error).error(com.ata.multiselectimage.R.drawable.mis_default_error).centerCrop().into(viewHolder.iv_show);

        return view;
    }
    public class ViewHolder{
        ImageView iv_show;
        FrameLayout fl_video_sign;
    }
}
