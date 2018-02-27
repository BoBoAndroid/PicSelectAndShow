package com.bobo.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by bobo on 2018/2/27.
 */

public class PositionView extends FrameLayout {
    public Context mContext;
    public TextView tv_name;
    public PositionView(Context context) {
        super(context);
        init(context,null);
    }

    public PositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public PositionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    public void init(Context context, AttributeSet attrs){
        this.mContext=context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_ope_info,
                this, true);
        tv_name=view.findViewById(R.id.tv_name);
    }

    public void refreshView(PathSelectActivity.BaseInfo baseInfo){
        tv_name.setText(baseInfo.name);
        tv_name.setTag(baseInfo.path);
    }

    public String getPath(){
        return (String) tv_name.getTag();
    }
}
